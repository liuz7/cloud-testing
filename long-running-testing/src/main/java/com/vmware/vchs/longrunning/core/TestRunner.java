package com.vmware.vchs.longrunning.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by sjun on 8/25/15.
 */
public class TestRunner {
    protected static final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    private TestSuite suite;
    private String storageFolder;
    private ObjectMapper mapper;
    private HashMap<String, String> globalData;
    private File jsonFile;

    public TestRunner(TestSuite suite, String storageFolder) {
        this.suite = suite;
        this.storageFolder = storageFolder;
        this.mapper = new ObjectMapper();
        this.jsonFile = new File(storageFolder, "global");

        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    protected void readGlobalData() {
        globalData = null;
        if (jsonFile.exists()) {
            try {
                globalData = mapper.readValue(jsonFile, new TypeReference<HashMap<String, String>>() {
                });
            } catch (IOException e) {
                logger.warn("Could not load global data.");
                logger.warn(Throwables.getStackTraceAsString(e));
            }
        }

        if (globalData == null) {
            globalData = new HashMap<>();
        }
    }

    protected void saveGlobalData() {
        try {
            mapper.writeValue(jsonFile, globalData);
        } catch (IOException e) {
            logger.warn("Could not save global data.");
            logger.warn(Throwables.getStackTraceAsString(e));
        }
    }

    public boolean run() {
        boolean allFinished = true;
        readGlobalData();
        for(Class<?> testClass: suite.getTestClasses()) {
            TestCaseRunner caseRunner = new TestCaseRunner(testClass, storageFolder, globalData);
            MDC.put("testcase", caseRunner.getTestCaseName());
            if(caseRunner.runTestCase()) {
                allFinished = false;
            }
            MDC.remove("testcase");
            saveGlobalData();
        }
        return allFinished;
    }
}
