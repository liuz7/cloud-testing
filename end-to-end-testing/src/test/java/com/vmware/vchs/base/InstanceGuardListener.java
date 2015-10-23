package com.vmware.vchs.base;

import com.vmware.vchs.launcher.TestHelper;
import com.vmware.vchs.model.portal.common.Data;
import com.vmware.vchs.model.portal.common.ListResponse;
import com.vmware.vchs.model.portal.instance.ListInstanceItem;
import com.vmware.vchs.model.portal.snapshot.ListSnapshotResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by sjun on 9/18/15.
 */
public class InstanceGuardListener implements ITestListener, IConfigurationListener2 {
    private static final Logger logger = LoggerFactory.getLogger(InstanceGuardListener.class);

    static class TestInfo {
        public static ThreadLocal<TestInfo> threadLocalValue = new ThreadLocal<>();

        private String testClassName;
        private String testMethodName;

        public TestInfo(String testClassName) {
            this(testClassName, null);
        }

        public TestInfo(String testClassName, String testMethodName) {
            this.testClassName = testClassName;
            this.testMethodName = testMethodName;
        }

        public String getTestClassName() {
            return testClassName;
        }

        public void setTestClassName(String testClassName) {
            this.testClassName = testClassName;
        }

        public String getTestMethodName() {
            return testMethodName;
        }

        public void setTestMethodName(String testMethodName) {
            this.testMethodName = testMethodName;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(testClassName) ^ Objects.hashCode(testMethodName);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof TestInfo)) {
                return false;
            }
            return Objects.equals(((TestInfo) obj).testClassName, testClassName)
                    && Objects.equals(((TestInfo) obj).testMethodName, testMethodName);
        }
    }

    protected static Set<TestInfo> runningTestInfos = new HashSet<>();

    private static DbaasApi dbaasApi = new DbaasApi(TestHelper.getConfiguration());
    public static void cleanUnusedInstances() {
        logger.info("Start to delete unused instances.");
        dbaasApi.tryCleanInstances(getUnusedInstanceIds());
        dbaasApi.tryCleanSnapshots(getUnusedSnapshotIds());
        logger.info("Completed to delete unused instances.");
    }

    private static List<String> getUnusedInstanceIds() {
        List<String> instanceIds = new ArrayList<>();
        ListResponse listResponse = dbaasApi.listDBInstances();
        List<ListInstanceItem> listInstanceItems = checkNotNull((List<ListInstanceItem>) listResponse.getData());

        synchronized (InstanceGuardListener.class) {
            for (ListInstanceItem listInstanceItem : listInstanceItems) {
                if (shouldDeleteInstance(listInstanceItem.getName())) {
                    instanceIds.add(listInstanceItem.getId());
                }
            }
        }

        return instanceIds;
    }

    private static List<String> getUnusedSnapshotIds() {
        List<String> snapshotIds = new ArrayList<>();
        ListSnapshotResponse listResponse = dbaasApi.listSnapshot();
        List<Data> listSnapshotItems = checkNotNull((List<Data>) listResponse.getData());

        synchronized (InstanceGuardListener.class) {
            for (Data listSnapshotItem : listSnapshotItems) {
                if (shouldDeleteInstance(listSnapshotItem.getSourceInstance().getName())) {
                    snapshotIds.add(listSnapshotItem.getId());
                }
            }
        }

        return snapshotIds;
    }

    public static void cleanAllInstances() {
        logger.info("Start to delete instances.");
        List<String> instanceIds = ((List<ListInstanceItem>)dbaasApi.listDBInstances().getData()).stream().map(item->item.getId()).collect(Collectors.toList());
        dbaasApi.tryCleanInstances(instanceIds);
        List<String> snapshotIds = ((List<Data>)dbaasApi.listSnapshot().getData()).stream().map(item->item.getId()).collect(Collectors.toList());
        dbaasApi.tryCleanSnapshots(snapshotIds);
        logger.info("Completed to delete instances.");
    }

    private static boolean shouldDeleteInstance(String instanceName) {
        Pattern pattern = Pattern.compile("(?<class>[a-zA-Z0-9.]+)(_(?<method>[a-zA-Z0-9.]+))?.*");

        Matcher m = pattern.matcher(instanceName);
        if (!m.matches()) {
            return true;
        }

        String className = m.group("class");
        String methodName = m.group("method");

        for (TestInfo testInfo: runningTestInfos) {
            if (className.equals(testInfo.getTestClassName())) {
                if (methodName == null) {
                    return false;
                }
                if (methodName.equals(testInfo.getTestMethodName())) {
                    return false;
                }
            }
        }
        return true;
    }

    private TestInfo getTestInfo(ITestResult result) {
        ITestNGMethod method = result.getMethod();
        if (method.isBeforeMethodConfiguration() || method.isAfterMethodConfiguration()) {
            if (result.getParameters().length > 0) {
                if(result.getParameters()[0] instanceof Method) {
                    return new TestInfo(result.getTestClass().getRealClass().getCanonicalName(), ((Method) result.getParameters()[0]).getName());
                }
            }
        } else if(method.isTest()) {
            return new TestInfo(result.getTestClass().getRealClass().getCanonicalName(), result.getMethod().getMethodName());
        }
        return new TestInfo(result.getTestClass().getRealClass().getCanonicalName());
    }

    private void process(ITestResult result) {
        TestInfo testInfo = getTestInfo(result);
        TestInfo prevTestInfo = TestInfo.threadLocalValue.get();
        TestInfo.threadLocalValue.set(testInfo);

        synchronized (InstanceGuardListener.class) {
            if (prevTestInfo != null) {
                runningTestInfos.remove(prevTestInfo);
            }
            runningTestInfos.add(testInfo);
        }

        if (prevTestInfo != null && prevTestInfo.getTestClassName() != null) {
            if (!prevTestInfo.getTestClassName().equals(testInfo.getTestClassName())) {
                cleanUnusedInstances();
            } else if (prevTestInfo.getTestMethodName() != null) {
                if (!prevTestInfo.getTestMethodName().equals(testInfo.getTestMethodName())) {
                    cleanUnusedInstances();
                }
            }
        }

    }

    @Override
    public void beforeConfiguration(ITestResult result) {
        process(result);
    }

    @Override
    public void onConfigurationSuccess(ITestResult result) {
        process(result);
    }

    @Override
    public void onConfigurationFailure(ITestResult result) {
        process(result);
    }

    @Override
    public void onConfigurationSkip(ITestResult result) {
        process(result);
    }

    @Override
    public void onTestStart(ITestResult result) {
        process(result);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        process(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        process(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        process(result);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    @Override
    public void onStart(ITestContext context) {
        cleanUnusedInstances();
    }

    @Override
    public void onFinish(ITestContext context) {
        cleanAllInstances();
    }
}
