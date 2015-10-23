package com.vmware.vchs.testng;

import com.vmware.vchs.test.client.etcd.BasicEtcdClient;
import com.vmware.vchs.test.client.model.etcd.MSSqlNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.List;

/**
 * Created by liuda on 9/29/15.
 */
public class NodeListener extends BaseTestListener implements ITestListener {

    public static final int FREE_NODE = 3;
    public static final int DEFAULT_TIMEOUT_IN_MILLIS = 1000 * 60 * 30;
    protected static final Logger logger = LoggerFactory.getLogger(NodeListener.class);

    public void onTestStart(ITestResult testResult) {
        waitUntilFreeNodesAvailableOrExit();
    }

    public void onTestSuccess(ITestResult var1) {
    }

    public void onTestFailure(ITestResult var1) {
    }

    public void onTestSkipped(ITestResult var1) {
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult var1) {
    }

    public void onStart(ITestContext var1) {
    }

    public void onFinish(ITestContext var1) {
    }

    public void waitUntilFreeNodesAvailableOrExit() {
        logger.info("start to wait node available");
        BasicEtcdClient client = new BasicEtcdClient(configuration.getEtcd().getBaseUrl());
        long endTime = System.currentTimeMillis() + DEFAULT_TIMEOUT_IN_MILLIS;
        while (System.currentTimeMillis() < endTime) {
            List<MSSqlNode> nodes = client.getMSSqlNodes();
            if (nodes.stream().filter(this::isAvailableNode).count() >= FREE_NODE) {
                return;
            }
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                logger.info("wait node interruptted, exit now");
                System.exit(1);
            }
        }
        logger.info("wait node time out, exit now");
        System.exit(1);
    }


    private String getMsSqlVersion(String dbEngine) {
        return dbEngine.substring(6);
    }

    private boolean isAvailableNode(MSSqlNode node) {
        int capacity = Integer.valueOf(node.getCapacity());
        String etcd_engine = node.getTags()[0];
        String etcd_edition = node.getTags()[1];
        String etcd_licenseType = node.getTags()[2];
        String sqlVersion = getMsSqlVersion(configuration.getDbEngineVersion());
        if (capacity > 0 && etcd_engine.equalsIgnoreCase(sqlVersion) && etcd_edition.equalsIgnoreCase(configuration.getEdition()) && etcd_licenseType.equalsIgnoreCase(configuration.getLicenseType())) {
            return true;
        }
        return false;
    }

}
