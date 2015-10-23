package com.vmware.vchs.longrunning.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.base.Throwables;
import com.vmware.vchs.longrunning.base.State;
import com.vmware.vchs.longrunning.base.TestContext;
import com.vmware.vchs.longrunning.base.TestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.Map;

/**
 * Created by sjun on 8/27/15.
 */
public class TestCaseRunner implements TestContext {
    protected static final Logger logger = LoggerFactory.getLogger(TestCaseRunner.class);

    private Class<?> testClass;
    private String storageFolder;
    private Map<String, String> globalData;
    private ObjectMapper mapper;

    private String state;
    private String previousState;
    private Object testData;
    private Object testInstance;
    private Field dataField;
    private File jsonFile;

    public TestCaseRunner(Class<?> testClass, String storageFolder, Map<String, String> globalData) {
        this.testClass = testClass;
        this.storageFolder = storageFolder;
        this.globalData = globalData;
        this.mapper = new ObjectMapper();
        this.testData = null;
        this.testInstance = null;
        this.state = TestStates.INIT_STATE;
        this.previousState = TestStates.UNKNOWN_STATE;

        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public String getTestCaseName() {
        return testClass.getCanonicalName();
    }

    protected boolean initTestCase() {
        jsonFile = new File(storageFolder, getTestCaseName());
        dataField = getDataField();
        testInstance = newInstance(testClass);

        if (testInstance == null) {
            return false;
        }

        if (dataField != null) {
            dataField.setAccessible(true);
        }

        if (!loadTestData()) {
            return false;
        }
        return true;
    }

    protected Object newInstance(Class<?> instClass) {
        try {
            return instClass.newInstance();
        } catch (Exception e) {
            logger.warn("Failed to create instance for <{}>", instClass.getCanonicalName());
            logger.warn(Throwables.getStackTraceAsString(e));
        }
        return null;
    }

    protected boolean isTestMethodForState(Method method, String stateName) {
        if (Modifier.isPublic(method.getModifiers())) {
            State stateAnnotation = method.getAnnotation(State.class);
            if (state != null && stateAnnotation.name().equalsIgnoreCase(stateName)) {
                if (String.class.equals(method.getReturnType())) {
                    Parameter[] parameters = method.getParameters();
                    if (parameters.length == 0) {
                        return true;
                    } else if (parameters.length == 1 && parameters[0].getType().equals(TestContext.class)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected Method getTestMethodsByState(String stateName) {
        for (Method method: testClass.getDeclaredMethods()) {
            if (isTestMethodForState(method, stateName)) {
                return method;
            }
        }
        for (Method method:testClass.getMethods()) {
            if (isTestMethodForState(method, stateName)) {
                return method;
            }
        }
        return null;
    }
    protected Field getDataField() {
        for (Field field:testClass.getDeclaredFields()) {
            if (field.getAnnotation(TestData.class) != null) {
                return field;
            }
        }

        Class tempClass = testClass.getSuperclass();
        while(tempClass != null) {
            for (Field field:tempClass.getDeclaredFields()) {
                if (field.getAnnotation(TestData.class) != null) {
                    if (Modifier.isProtected(field.getModifiers()) || Modifier.isPublic(field.getModifiers())) {
                        return field;
                    }
                }
            }
            tempClass = tempClass.getSuperclass();
        }

        return null;
    }

    protected boolean saveTestData() {
        Object testData = null;

        if (dataField != null) {
            try {
                testData = dataField.get(testInstance);
            } catch (IllegalAccessException e) {
                logger.warn("Could not get data for <{}>", testClass.getCanonicalName());
                logger.warn(Throwables.getStackTraceAsString(e));
                return false;
            }
        }

        try {
            ObjectNode root = mapper.createObjectNode();
            root.set("state", new TextNode(state));
            root.set("previousState", new TextNode(previousState));
            root.set("data", mapper.valueToTree(testData));
            mapper.writeValue(jsonFile, root);
        } catch (IOException e) {
            logger.warn("Could not save data for <{}>", testClass.getCanonicalName());
            logger.warn(Throwables.getStackTraceAsString(e));
            return false;
        }

        return true;
    }

    protected boolean loadTestData() {
        if (jsonFile.exists()) {
            try {
                JsonNode root = mapper.readTree(jsonFile);
                state = root.get("state").asText();
                previousState = root.get("previousState").asText();
                if (dataField != null) {
                    testData = mapper.treeToValue(root.get("data"), dataField.getType());
                }

            } catch (IOException e) {
                logger.warn("Could not load data for <{}>", testClass.getCanonicalName());
                logger.warn(Throwables.getStackTraceAsString(e));
            }
        }

        if (dataField != null) {
            if (testData == null) {
                testData = newInstance(dataField.getType());
                if (testData == null) {
                    return false;
                }
            }

            try {
                dataField.set(testInstance, testData);
            } catch (IllegalAccessException e) {
                logger.info("Could not set data for <{}>", testClass.getCanonicalName());
                logger.warn(Throwables.getStackTraceAsString(e));
                return false;
            }
        }

        return true;
    }

    public boolean runTestCase() {
        if (!initTestCase()) {
            return false;
        }

        if (state.equals(TestStates.FINISHED_STATE)) {
            return false;
        }

        Method method = getTestMethodsByState(state);

        if (method == null && state.equals(TestStates.ERROR_STATE)) {
            return false;
        }

        logger.info("Test Begin <{}> <{}>", getTestCaseName(), state);

        if (method == null) {
            logger.error("Method for state <{}> in class <{}> is not found.", state, testClass.getCanonicalName());
        } else {
            runMethod(method);
        }

        saveTestData();

        logger.info("Test End <{}> <{}>", getTestCaseName(), previousState);
        return true;
    }

    protected void runMethod(Method method) {
        previousState = state;
        State stateAnnotation = method.getAnnotation(State.class);
        try {
            if (method.getParameterCount() == 0) {
                state = method.invoke(testInstance).toString();
            } else {
                state = method.invoke(testInstance, this).toString();
            }

            logger.info("Execution passed for <{}> state:<{}>, new state:<{}>", testClass.getCanonicalName(), previousState, state);
            if (stateAnnotation.verification()) {
                logger.info("TestCase {}.{} passed.", testClass.getCanonicalName(), method.getName());
            }
        } catch (Exception e) {
            state = TestStates.ERROR_STATE;
            logger.warn("Execution failed for <{}> state:<{}>, new state:<{}>", testClass.getCanonicalName(), previousState, state);
            if (e instanceof InvocationTargetException) {
                logger.error(Throwables.getStackTraceAsString(((InvocationTargetException) e).getTargetException()));
            } else {
                logger.error(Throwables.getStackTraceAsString(e));
            }
            if (stateAnnotation.verification()) {
                logger.info("TestCase {}.{} failed.", testClass.getCanonicalName(), method.getName());
            }
        }
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public String getPreviousState() {
        return previousState;
    }

    @Override
    public void setGlobalValue(String key, String value) {
        globalData.put(key, value);
    }

    @Override
    public String getGlobalValue(String key) {
        return globalData.get(key);
    }
}
