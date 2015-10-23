package com.vmware.vchs.testng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.*;

/**
 * Test Log Listeners for Quality Insight
 * Created by sjun on 8/10/15.
 */
public class TestLogListener implements ITestListener, IConfigurationListener2 {
    private static final Logger logger = LoggerFactory.getLogger(TestLogListener.class);

    private String getMethodType(ITestNGMethod method) {
        if (method.isBeforeClassConfiguration()) {
            return "BeforeClass";
        } else if (method.isAfterClassConfiguration()) {
            return "AfterClass";
        } else if (method.isBeforeMethodConfiguration()) {
            return "BeforeMethod";
        } else if (method.isAfterMethodConfiguration()) {
            return "AfterMethod";
        } else if (method.isTest()) {
            return "Test";
        } else if (method.isBeforeTestConfiguration()) {
            return "BeforeTest";
        } else if (method.isAfterTestConfiguration()) {
            return "AfterTest";
        } else if (method.isBeforeGroupsConfiguration()) {
            return "BeforeGroups";
        } else if (method.isAfterGroupsConfiguration()) {
            return "AfterGroups";
        } else if (method.isBeforeSuiteConfiguration()) {
            return "BeforeSuite";
        } else if (method.isAfterSuiteConfiguration()) {
            return "AfterSuite";
        } else {
            return "Unknown";
        }
    }

    private void logResult(String label, ITestResult result) {
        // dump Label:Class:Type:MethodFullName
        ITestNGMethod method = result.getMethod();
        String type = getMethodType(method);
        String clazz = result.getTestClass().getName();
        String methodFullName = String.format("%s.%s", method.getRealClass().getName(), method.getMethodName());

        logger.info("{}:{}:{}:{}",
                label,
                clazz,
                type,
                methodFullName);
    }

    @Override
    public void beforeConfiguration(ITestResult result) {
        logResult("Begin", result);
    }

    @Override
    public void onConfigurationSuccess(ITestResult result) {
        logResult("End:PASS", result);
    }

    @Override
    public void onConfigurationFailure(ITestResult result) {
        logResult("End:FAIL", result);
    }

    @Override
    public void onConfigurationSkip(ITestResult result) {
        logResult("Begin", result);
        logResult("End:SKIP", result);
    }

    @Override
    public void onTestStart(ITestResult result) {
        MDC.put("testcase", result.getMethod().getMethodName());
        logResult("Begin", result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logResult("End:PASS", result);
        MDC.remove("testcase");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logResult("End:FAIL", result);
        MDC.remove("testcase");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logResult("End:SKIP", result);
        MDC.remove("testcase");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    @Override
    public void onStart(ITestContext context) {
    }

    @Override
    public void onFinish(ITestContext context) {
    }

}
