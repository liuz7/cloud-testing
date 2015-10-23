package com.vmware.vchs.misc;

import com.google.common.collect.Lists;
import com.vmware.vchs.SnapshotBaseTest;
import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.common.utils.exception.PortalError;
import com.vmware.vchs.common.utils.exception.RestException;
import com.vmware.vchs.common.utils.exception.RetryException;
import com.vmware.vchs.constant.NodeStatus;
import com.vmware.vchs.constant.PortalErrorMap;
import com.vmware.vchs.constant.StatusCode;
import com.vmware.vchs.model.nats.Version;
import com.vmware.vchs.model.portal.common.ListResponse;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.model.portal.instance.SnapshotSettings;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import com.vmware.vchs.test.client.db.MsSqlDaoFactory;
import com.vmware.vchs.test.client.db.model.Employee;
import com.vmware.vchs.utils.EtcdMssqlClient;
import com.vmware.vchs.utils.TMUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static com.vmware.vchs.test.client.db.SQLStatements.DB_TESTDB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * Created by georgeliu on 15/3/30.
 */
@Test(groups = {"maintenance"})
public class MaintenanceTimeTest extends SnapshotBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceTimeTest.class);

    @Test
    public void testGetMaintenanceTime() throws Exception {
        DbaasInstance instance =builder.setMaintenanceTime("Sun:10:00").createInstanceWithRetry(getNamePrefix());
        assertThat(instance.getInstanceResponse().getMaintenanceTime()).isEqualTo("Sun:10:00");
    }

    @Test
    public void testSetNullMaintenanceTime() throws Exception {
        try {
            builder.setMaintenanceTime(null).createInstanceWithRetry(getNamePrefix());
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
            assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.INVALID_ARGUMENT.getCode());
        }
    }

    @Test
    public void testInstanceCURDAfterMaintenance() throws Exception {
        startAndCompleteMaintenance(new EtcdMssqlClient(configuration.getEtcd().getBaseUrl()).getAllNodeId());
        DbaasInstance instance =builder.createInstanceWithRetry(getNamePrefix());
        ListResponse listResponse = dbaasApi.listDBInstances();
        assertThat(listResponse).isNotNull();
        assertThat(instance).isNotNull();
        instance.deleteWithRetry();
        //TODO add update instance operation after update is refactored
    }

//    @Ignore(reasons = "https://vchs-eng.atlassian.net/browse/DBAAS-681")
//    @Test
//    public void testInstanceCURDDuringMaintenance() throws Exception {
//        List<String> allNodes = new EtcdMssqlClient(configuration.getEtcd().getBaseUrl()).getAllNodeId();
//        startMaintenance(allNodes);
//        try {
//            testClient.createDBInstance(this.createInstanceRequest);
//            failBecauseExceptionWasNotThrown(RestException.class);
//        } catch (RestException e) {
//            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_INTERNAL_ERROR);
//            assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.UNABLE_TO_ALLOCATE_RESOURCE.getCode());
//        }
//        startMaintenance(allNodes);
//        try {
//            deleteInstanceWithRetry(this.getInstanceResponse.getId());
//            failBecauseExceptionWasNotThrown(RestException.class);
//        } catch (RestException e) {
//            //TODO need to check the error code
//        }
//        GetInstanceResponse getInstanceResponse = testClient.getDBInstance(this.getInstanceResponse.getId());
//        ListResponse listResponse = testClient.listDBInstances();
//        assertThat(listResponse).isNotNull();
//        assertThat(getInstanceResponse).isNotNull();
//    }

    @Test
    public void testUpgradeAfterMaintenance() throws Exception {
        List<String> allNodes = new EtcdMssqlClient(configuration.getEtcd().getBaseUrl()).getAllNodeId();
        for (String nodeId : allNodes) {
            Version previousVersion = TMUtils.getVersion(nodeId);
            String result = startMaintenanceTask(nodeId).get();
            assertThat(result).containsIgnoringCase(StatusCode.AVAILABLE.value());
            String etcdStatus = this.retryTask.execute(new GetFreeOrProvisionedStatusTask(nodeId));
            assertThat(etcdStatus).isIn(NodeStatus.FREE.value(), NodeStatus.PROVISIONED.value());
            Version afterVersion = TMUtils.getVersion(nodeId);
            assertThat(afterVersion).isNotEqualTo(previousVersion);
        }
    }

    @Test
    public void testSnapshotAfterMaintenance() throws Exception {
        startAndCompleteMaintenance(new EtcdMssqlClient(configuration.getEtcd().getBaseUrl()).getAllNodeId());
        DbaasInstance instance =builder.createInstanceWithRetry(getNamePrefix());
        GetSnapshotResponse snapshotCreated = instance.createSnapshotWithRetry();
        assertThat(snapshotCreated.getStatus()).isEqualTo(StatusCode.AVAILABLE.value());
    }

    @Test
    public void testSnapshotDuringMaintenance() throws Exception {
        DbaasInstance instance =builder.setAutoSnapshot(true).createInstanceWithRetry(getNamePrefix());
        try {
            startMaintenance(new EtcdMssqlClient(configuration.getEtcd().getBaseUrl()).getAllNodeId());
            instance.createSnapshotWithRetry();
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            //TODO need to check the error code
        }
        assertThat(instance.listSnapshots().size()).isZero();
    }

//    @Test
//    public void testBackupAfterMaintenance() throws Exception {
//        startAndCompleteMaintenance(new EtcdMssqlClient(configuration.getEtcd().getBaseUrl()).getAllNodeId());
//        Employee testEmployee1 = testData.get(0);
//        Employee testEmployee2 = testData.get(1);
//        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
//        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
//        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
//        insertData(jdbcClient, DB_TESTDB, testEmployee1);
//        assertThat(isDataExists(jdbcClient.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
//        String[] firstRange = getRestoreWindow(instanceCreated.getId());
//        String[] range = getRestoreWindowRightEdgeAfterCurrentTime(jdbcClient, instanceCreated.getId(), firstRange);
//        getCurrentTimeAfterRestoreTime(jdbcClient, range[1]);
//        insertData(jdbcClient, DB_TESTDB, testEmployee2);
//        GetInstanceResponse activeInstanceFromBackup = createInstanceFromBackup(instanceCreated, range[1], masterPassword);
//        MsSqlDaoFactory jdbcClientFromBackup = getDbConnection(activeInstanceFromBackup);
//        assertThat(testDbConnectionWithRetry(jdbcClientFromBackup)).isTrue();
//        assertThat(jdbcClientFromBackup.createSysDao().isDatabaseExists(DB_TESTDB)).isTrue();
//        assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
//        assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), DB_TESTDB, testEmployee2)).isFalse();
//    }
//
//    @Test
//    public void testBackupDuringMaintenance() throws Exception {
//        startMaintenance(new EtcdMssqlClient(configuration.getEtcd().getBaseUrl()).getAllNodeId());
//        Employee testEmployee1 = testData.get(0);
//        Employee testEmployee2 = testData.get(1);
//        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
//        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
//        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
//        insertData(jdbcClient, DB_TESTDB, testEmployee1);
//        assertThat(isDataExists(jdbcClient.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
//        String[] firstRange = getRestoreWindow(instanceCreated.getId());
//        String[] range = getRestoreWindowRightEdgeAfterCurrentTime(jdbcClient, instanceCreated.getId(), firstRange);
//        getCurrentTimeAfterRestoreTime(jdbcClient, range[1]);
//        insertData(jdbcClient, DB_TESTDB, testEmployee2);
//        GetInstanceResponse activeInstanceFromBackup = createInstanceFromBackup(instanceCreated, range[1], masterPassword);
//        MsSqlDaoFactory jdbcClientFromBackup = getDbConnection(activeInstanceFromBackup);
//        assertThat(testDbConnectionWithRetry(jdbcClientFromBackup)).isTrue();
//        assertThat(jdbcClientFromBackup.createSysDao().isDatabaseExists(DB_TESTDB)).isTrue();
//        assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
//        assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), DB_TESTDB, testEmployee2)).isFalse();
//    }

    public class GetFreeOrProvisionedStatusTask implements Callable<String> {

        private String nodeId;

        public GetFreeOrProvisionedStatusTask(String nodeId) {
            this.nodeId = nodeId;
        }

        @Override
        public String call() throws Exception {
            return getNodeStatus(nodeId, NodeStatus.FREE.value(), NodeStatus.PROVISIONED.value());
        }
    }

    private List<String> startAndCompleteMaintenance(List<String> nodeIds) throws Exception {
        List<String> resultList = Lists.newArrayList();
        for (String nodeId : nodeIds) {
            String result = startMaintenanceTask(nodeId).get();
            resultList.add(result);
            assertThat(result).containsIgnoringCase(StatusCode.AVAILABLE.value());
            String etcdStatus = this.retryTask.execute(new GetFreeOrProvisionedStatusTask(nodeId));
            assertThat(etcdStatus).isIn(NodeStatus.FREE.value(), NodeStatus.PROVISIONED.value());
        }
        return resultList;
    }

    private List<Future<String>> startMaintenance(List<String> nodeIds) throws Exception {
        List<Future<String>> resultList = Lists.newArrayList();
        for (String nodeId : nodeIds) {
            Future<String> result = startMaintenanceTask(nodeId);
            resultList.add(result);
            //TODO check the maintenance status in task
            String etcdStatus = this.retryTask.execute(new GetDuringMaintenanceStatusTask(nodeId));
            assertThat(etcdStatus).isIn(NodeStatus.PROVISIONED_IN_MAINTENANCE.value(), NodeStatus.FREE_IN_MAINTENANCE.value());
        }
        return resultList;
    }

    public class MaintenanceTask implements Callable<String> {

        private String nodeId;

        public MaintenanceTask(String nodeId) {
            this.nodeId = nodeId;
        }

        @Override
        public String call() throws Exception {
            String result;
            try {
                result = TMUtils.startMaintenance(nodeId);
                logger.info(result);
            } catch (Exception e) {
                throw e;
            }
            return result;
        }
    }

    public Future<String> startMaintenanceTask(String nodeId) throws Exception {
        return this.threadTask.submitTask(new MaintenanceTask(nodeId));
    }

    public class GetDuringMaintenanceStatusTask implements Callable<String> {

        private String nodeId;

        public GetDuringMaintenanceStatusTask(String nodeId) {
            this.nodeId = nodeId;
        }

        @Override
        public String call() throws Exception {
            return getNodeStatus(nodeId, NodeStatus.FREE_IN_MAINTENANCE.value(), NodeStatus.PROVISIONED_IN_MAINTENANCE.value());
        }
    }

    private String getNodeStatus(String nodeId, String... nodeStatus) {
        String result;
        List<String> nodeStatusList = Lists.newArrayList(nodeStatus);
        String listString = String.join(", ", nodeStatusList);
        try {
            result = new EtcdMssqlClient(configuration.getEtcd().getBaseUrl()).getNodeStatusByNodeId(nodeId);
            if (nodeStatusList.contains(result)) {
                return result;
            } else {
                throw new RetryException("Failed to get " + listString + " Status.");
            }
        } catch (Exception e) {
            throw new RetryException("Failed to get " + listString + " Status.");
        }
    }

}
