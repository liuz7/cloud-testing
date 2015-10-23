package com.vmware.vchs.launcher;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vmware.vchs.base.InstanceGuardListener;
import com.vmware.vchs.common.utils.Utils;
import com.vmware.vchs.test.client.cds.CDSClient;
import com.vmware.vchs.test.config.Configuration;
import com.vmware.vchs.testng.ConcurrentListener;
import com.vmware.vchs.testng.IgnoreListener;
import com.vmware.vchs.testng.NodeListener;
import com.vmware.vchs.testng.TestStatusListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.testng.TestNG;
import org.testng.xml.*;
import org.uncommons.reportng.HTMLReporter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by georgeliu on 15/3/2.
 */
public class TestLauncher {

    private static final String PLAN_NAME = "planName";
    private static final String DB_VERSION = "dbEngineVersion";
    private static final String SUITE_NAME = "end-to-end-testing";
    private static final String PACKAGE_NAME_PREFIX = "com.vmware.vchs";
    protected static final Logger logger = LoggerFactory.getLogger(TestLauncher.class);

    private static XmlSuite createTestSuite(List<String> includeGroups, List<String> excludeGroups) {
        String testName = String.join("+", includeGroups);
        XmlSuite suite = new XmlSuite();
        suite.setName(SUITE_NAME);
        suite.setConfigFailurePolicy(XmlSuite.CONTINUE);
        XmlTest test = new XmlTest(suite);
        test.setName(testName);
        List<XmlPackage> packages = Lists.newArrayList(
                new XmlPackage(PACKAGE_NAME_PREFIX + ".instance.*"),
                new XmlPackage(PACKAGE_NAME_PREFIX + ".backup.*"),
                new XmlPackage(PACKAGE_NAME_PREFIX + ".snapshot.*"),
                new XmlPackage(PACKAGE_NAME_PREFIX + ".role.*"),
                new XmlPackage(PACKAGE_NAME_PREFIX + ".billing.*"),
                new XmlPackage(PACKAGE_NAME_PREFIX + ".datapath.*"),
                new XmlPackage(PACKAGE_NAME_PREFIX + ".pipeline.*")
        );
        test.setPackages(packages);
        test.setIncludedGroups(includeGroups);
        test.setExcludedGroups(excludeGroups);
        return suite;
    }

    private static XmlSuite createTestSuite(String testClassName) {
        return createTestSuite(testClassName, null);
    }

    private static XmlSuite createTestSuite(String testClassName, String testMethodName) {
        Class testClass = TestFilter.getTestClass(PACKAGE_NAME_PREFIX, testClassName);
        if (testClass == null) {
            logger.error("TestClass <" + testClassName + "> not found.");
            return null;
        }

        XmlSuite suite = new XmlSuite();
        suite.setName(SUITE_NAME);
        suite.setConfigFailurePolicy(XmlSuite.CONTINUE);
        XmlTest test = new XmlTest(suite);
        test.setName(testClassName);
        XmlClass xmlClass = new XmlClass(testClass.getCanonicalName());
        if (testMethodName != null) {
            XmlInclude xmlInclude = new XmlInclude(testMethodName);
            xmlClass.setIncludedMethods(Lists.newArrayList(xmlInclude));
        }
        List<XmlClass> classes = Lists.newArrayList(xmlClass);
        test.setClasses(classes);
        return suite;
    }

    private static void runTestSuite(XmlSuite suite) {
        runTestSuite(suite, XmlSuite.DEFAULT_PARALLEL);
    }

    private static void runTestSuite(XmlSuite suite, String parallel) {
        TestNG tng = new TestNG();
        tng.setXmlSuites(Lists.newArrayList(suite));
        List<Class> listeners = Lists.newArrayList(
                ConcurrentListener.class,
                IgnoreListener.class,
                HTMLReporter.class,
                TestStatusListener.class
        );

        if("prod".equalsIgnoreCase(TestHelper.getConfiguration().getDeployEnv())){
            listeners.add(NodeListener.class);
        }

        listeners.add(InstanceGuardListener.class);

        tng.setListenerClasses(listeners);
        tng.run();
    }

    private static Map<String, String> getTestGroupMap() {
        Map<String, String> groupsMap = Maps.newHashMap();
        // for include
        groupsMap.put("alphatest", "alpha");
        groupsMap.put("sanitytest", "sanity");
        groupsMap.put("fulltest", "full");
        groupsMap.put("instancetest", "instance");
        groupsMap.put("sanitytest", "sanity");
        groupsMap.put("backuptest", "backup");
        groupsMap.put("snapshottest", "snapshot");
        groupsMap.put("iamtest", "iamtest");
        groupsMap.put("billingtest", "billing");
        groupsMap.put("pipelinetest", "pipeline");
        groupsMap.put("datapathtest", "datapath");

        // for exclude
        groupsMap.put("snsonlytest", "snsonly");
        groupsMap.put("largediskonlytest", "largediskonly");
        groupsMap.put("populatetest", "populateData");
        groupsMap.put("verifytest", "verifyData");
        groupsMap.put("datatest", "datatest");
        groupsMap.put("nodeConsumetest", "nodeConsume");
        return groupsMap;
    }

    private static List<String> parseGroups(String groups) {
        Map<String, String> groupsMap = getTestGroupMap();
        List<String> ret = new ArrayList<>();
        for (String group : groups.split(",")) {
            group = group.trim();
            if (groupsMap.containsKey(group)) {
                ret.add(groupsMap.get(group));
            } else {
                // logger.warn("Group <" + group + "> is not found.");
                ret.add(group);
            }
        }
        return ret;
    }

    private static XmlSuite createTestSuite() {
        Configuration configuration = TestHelper.getConfiguration();
        String testGroup = configuration.getTestGroup();
        String testExcludeGroup = configuration.getTestExcludeGroup();
        String testClass = configuration.getTestClass();
        String testMethod = configuration.getTestMethod();

        XmlSuite suite = null;
        if (testClass.isEmpty()) {
            if (!testMethod.isEmpty()) {
                logger.error("testMethod is " + testMethod + ", but testClass is empty.");
                return null;
            }
            if (testGroup.isEmpty()) {
                logger.error("Both testGroup and testClass are empty.");
                return null;
            }
            List<String> includeGroups = parseGroups(testGroup);

            List<String> excludeGroups = null;
            if (testExcludeGroup.isEmpty()) {
                excludeGroups = Lists.newArrayList();
            } else {
                excludeGroups = parseGroups(testExcludeGroup);
            }

            String host = TestHelper.getContext().getEnvironment().getProperty("mysql.host");
            String port = TestHelper.getContext().getEnvironment().getProperty("mysql.port");

            if (!Utils.checkHostAndPort(host, port)) {
                excludeGroups.add("billing");
            }

            if (!configuration.isSns()) {
                excludeGroups.add("snsonly");
            }

            if (!configuration.isAuthentication()) {
                excludeGroups.add("iamtest");
            }

            logger.info("include group: " + includeGroups.toString() + "; exclude group: " + excludeGroups.toString());
            suite = createTestSuite(includeGroups, excludeGroups);
        } else {
            if (testMethod.isEmpty()) {
                suite = createTestSuite(testClass);
            } else {
                suite = createTestSuite(testClass, testMethod);
            }
        }
        return suite;
    }

    private static void getDeploymentInfo(Configuration configuration) {
        CDSClient cdsClient = new CDSClient("http://" + configuration.getCdsServer().getBaseUrl());
        Map<String, String> deploymentInfo = cdsClient.getCurrentReleaseVersions();
        logger.info("Deployment Information:" + deploymentInfo.toString());
        Path newFile = Paths.get("deploymentInfo.properties");
        try {
            Files.deleteIfExists(newFile);
            newFile = Files.createFile(newFile);
        } catch (IOException ex) {
            Utils.getStackTrace(ex);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(
                newFile, Charset.defaultCharset())) {
            for (String deploymentName : deploymentInfo.keySet()) {
                List<Map<String, String>> componentVersions = cdsClient.getCurrentReleaseComponentVersions(deploymentName);
                Properties properties = new Properties();
                properties.setProperty(deploymentName.split("-")[0], deploymentInfo.get(deploymentName));
                for (Map<String, String> componentVersion : componentVersions) {
                    properties.putAll(componentVersion);
                }
                properties.store(writer, "deployment information for " + deploymentName);
            }
        } catch (IOException ex) {
            Utils.getStackTrace(ex);
        }
    }

    public static void main(String args[]) {
        TestHelper.initApplication(args);
        Configuration configuration = TestHelper.getConfiguration();
        XmlSuite suite = createTestSuite();
        if (suite != null) {

// we will do this in script
//            if (!StringUtils.isEmpty(configuration.getCdsServer().getBaseUrl()) && (!StringUtils.isEmpty(configuration.getCdsServer().getPassword())) && (!StringUtils.isEmpty(configuration.getCdsServer().getPassword()))) {
//                getDeploymentInfo(configuration);
//            }

            if (TestHelper.getConfiguration().isParallel()) {
                suite.setParallel(XmlSuite.PARALLEL_CLASSES);
            }
            for (int i = 0; i < TestHelper.getConfiguration().getTestRounds(); i++) {
                runTestSuite(suite);
            }
        }
//        SpringApplication.exit(configuration, new ExitCodeGenerator() {
//            @Override
//            public int getExitCode() {
//                return 0;
//            }
//        });
        ConfigurableApplicationContext context = TestHelper.getContext();
        context.close();
        context.registerShutdownHook();
    }
}
