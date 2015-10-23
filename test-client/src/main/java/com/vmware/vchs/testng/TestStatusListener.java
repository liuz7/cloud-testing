package com.vmware.vchs.testng;

import com.google.api.client.repackaged.com.google.common.base.Throwables;
import com.vmware.vchs.test.client.etcd.BasicEtcdClient;
import com.vmware.vchs.test.config.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by georgeliu on 15/5/14.
 */
public class TestStatusListener extends TestLogListener implements ITestListener, ISuiteListener, IConfigurationListener2 {

    protected static final Logger logger = LoggerFactory.getLogger(TestStatusListener.class);

    private AtomicInteger successCount = new AtomicInteger(0);
    private AtomicInteger failCount = new AtomicInteger(0);
    private AtomicInteger skipCount = new AtomicInteger(0);
    private ConcurrentHashMap<String, Long> caseTimeCache = new ConcurrentHashMap();

    public static final String TEST_CASE_DELIMIT = "*";

    protected static Configuration configuration;

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static void setConfiguration(Configuration configuration) {
        TestStatusListener.configuration = configuration;
    }

    @Override
    public void onStart(ISuite suite) {
        successCount.set(0);
        failCount.set(0);
        skipCount.set(0);
        logger.info("About to begin executing Suite " + suite.getName());
    }

    @Override
    public void onFinish(ISuite suite) {
        logger.info("About to end executing Suite " + suite.getName());
        Iterator<Map.Entry<String, Long>> iterator = caseTimeCache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            logger.info(entry.getKey() + " takes " + entry.getValue() + " seconds");
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            logger.info(e.toString());
        }
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread thread : threadSet) {
            if (!thread.isDaemon()) {
                logger.info(thread.toString());
                logger.info(Arrays.toString(thread.getStackTrace()));
            }
        }
        int totalTests = successCount.get() + failCount.get() + skipCount.get();
        if (totalTests != 0) {
            double percentage = (double) successCount.get() / totalTests;
            BigDecimal bigDecimalPercentage = new BigDecimal(percentage);
            BigDecimal roundedWithScalePercentage = bigDecimalPercentage.setScale(2, BigDecimal.ROUND_HALF_UP);
            logger.info("Pass rate percentage:" + (int) (roundedWithScalePercentage.doubleValue() * 100) + "%");
        }
    }

    @Override
    public void onStart(ITestContext context) {
    }

    @Override
    public void onFinish(ITestContext context) {
    }

    public void onTestStart(ITestResult testResult) {
        super.onTestStart(testResult);
        ITestNGMethod testNGMethod = testResult.getMethod();
        if (testNGMethod != null) {
            String testMethod = testNGMethod.getMethodName();
            logger.info(StringUtils.repeat(TEST_CASE_DELIMIT, 20));
            logger.info("Start test: " + testMethod);
            logger.info(StringUtils.repeat(TEST_CASE_DELIMIT, 20));
        }
    }

    public void onTestSuccess(ITestResult testResult) {
        processTestResult(testResult);
        super.onTestSuccess(testResult);
    }

    public void onTestFailure(ITestResult testResult) {
        logStackTrace(testResult);

        dumpDBaasInfoIfNeeded(testResult.getThrowable());
        processTestResult(testResult);

        if (configuration.isExitOnFail()) {
            System.exit(1);
        }
        super.onTestFailure(testResult);
    }

    public void onTestSkipped(ITestResult testResult) {
        processTestResult(testResult);
        super.onTestSkipped(testResult);
    }

    private void logStackTrace(ITestResult testResult) {
        Throwable throwable = testResult.getThrowable();
        if (throwable != null) {
            logger.error(Throwables.getStackTraceAsString(throwable));
        }
    }

    private void processTestResult(ITestResult testResult) {
        long duration = (testResult.getEndMillis() - testResult.getStartMillis()) / 1000;
        if (duration < 60) {
            logger.info("Test duration: " + duration + " seconds.");
        } else {
            logger.info("Test duration: " + (duration / 60) + " minutes.");
        }
        caseTimeCache.put(testResult.getMethod().getMethodName(), duration);
        String status = null;
        switch (testResult.getStatus()) {
            case ITestResult.SUCCESS:
                status = "Pass";
                this.successCount.getAndIncrement();
                break;
            case ITestResult.FAILURE:
                status = "Failed";
                this.failCount.getAndIncrement();
                break;
            case ITestResult.SKIP:
                this.skipCount.getAndIncrement();
                status = "Skipped";
        }
        logger.info("Test " + testResult.getMethod().getMethodName() + " " + status);
        logger.info("All Pass:" + this.successCount.get());
        logger.info("All Fail:" + this.failCount.get());
        logger.info("All Skip:" + this.skipCount.get());
    }

    @Override
    public void onConfigurationFailure(ITestResult testResult) {
        logStackTrace(testResult);
        if (configuration.isExitOnFail()) {
            System.exit(1);
        }
        super.onConfigurationFailure(testResult);
    }

    private void dumpDBaasInfoIfNeeded(Throwable throwable) {
        // dump etcd mssql info when we cannot get instance ro available node
        if (throwable != null) {
            String message = throwable.toString();
            if (message.contains("PortalError")
                    && (message.contains("Cannot find available node") || message.contains("Can't find instance"))) {
                dumpEtcd();
            }
//            else if (message.contains("No route to host") || message.contains("Could not get JDBC Connection")) {
//                //dumpEdgeGateway();
//                //TODO: edge gateway should use vcloud host to access
//            }
        }
    }

    private void dumpEtcd() {
        try {
            BasicEtcdClient client = new BasicEtcdClient(configuration.getEtcd().getBaseUrl());
            logger.info("Dump etcd " + BasicEtcdClient.PATH_NODES_MSSQL);
            client.listRaw(BasicEtcdClient.PATH_NODES_MSSQL).forEach((String json) -> logger.info(json));
            logger.info("Dump etcd " + BasicEtcdClient.PATH_LOCKS_MSSQL);
            client.listRaw(BasicEtcdClient.PATH_LOCKS_MSSQL).forEach((String json) -> logger.info(json));
        } catch (Exception e) {
            logger.warn("Dump etcd failed.");
        }
    }

//    private void dumpEdgeGateway() {
//        try {
//            final String orgUrl = "https://" + this.configuration.getCdsServer().getBaseUrl();
//            final String orgName = "dbaas";
//            final String adminVdcName = "dbaas_dev_" + this.configuration.getVdcNumber();
//            final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "dev_" + this.configuration.getVdcNumber(), "password");
//            loginAdapter.login();
//            final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);
//            final EdgeGatewayAdapter edgeGatewayAdapter = new EdgeGatewayAdapter(null, loginAdapter.getVCloudClient(), vdc);
//            edgeGatewayAdapter.showEdgeGatewayDetails();
//            loginAdapter.logout();
//        } catch (VCloudException e) {
//            logger.info(Utils.getStackTrace(e));
//        }
//    }
}
