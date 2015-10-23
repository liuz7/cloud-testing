package com.vmware.vchs.snapshot;

import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.common.utils.exception.RestException;
import com.vmware.vchs.constant.Constant;
import com.vmware.vchs.constant.StatusCode;
import com.vmware.vchs.model.constant.SnapshotType;
import com.vmware.vchs.model.portal.common.AsyncResponse;
import com.vmware.vchs.model.portal.common.Data;
import com.vmware.vchs.model.portal.instance.DebugProperties;
import com.vmware.vchs.model.portal.instance.SnapshotSettings;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import com.vmware.vchs.test.client.db.SQLStatements;
import com.vmware.vchs.test.client.db.model.Employee;
import com.vmware.vchs.utils.RandomEmployee;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.vmware.vchs.test.client.db.SQLStatements.DB_TESTDB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * Created by georgeliu on 14/12/18.
 */
public class CreateSnapshotWithInstanceTest extends SnapshotTest {

    @Test
    public void testManualCreateSnapshotInstance() throws Exception {
        ResponseEntity<AsyncResponse> responseEntity = instance.createSnapshotEntity();
        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpURLConnection.HTTP_OK);
        AsyncResponse createResponse = responseEntity.getBody();
        assertThat(createResponse).isNotNull();
        assertThat(createResponse.getStartTime()).isNotNull();
        assertThat(createResponse.getId()).isNotNull();
        assertThat(createResponse.getStatus()).contains(StatusCode.CREATING.value());
        GetSnapshotResponse snapshotCreated = dbaasApi.waitAndGetAvailableSnapshot(instance.getSnapshotid());
//        GetSnapshotResponse snapshotCreated = this.retryTask.execute(new GetActiveSnapshotTask(createResponse.getId()));
        assertThat(snapshotCreated.getStatus()).isEqualTo(StatusCode.AVAILABLE.value());
        assertThat(snapshotCreated.getId()).isNotNull();
        assertThat(snapshotCreated.getName()).isNotNull();
        assertThat(snapshotCreated.getDescription()).isNotNull();
        assertThat(snapshotCreated.getType()).isEqualTo(SnapshotType.manual.toString());

        assertThat(snapshotCreated.getCreatedAt()).isNotNull();
        assertThat(snapshotCreated.getUpdatedAt()).isNotNull();
//        assertThat(snapshotCreated.getPermissions()).isNotNull();
//        assertThat(snapshotCreated.getPermissions().get(0)).isEqualTo(CREATEINSTANCEFROMSNAPSHOT);
//        assertThat(snapshotCreated.getCreatorEmail()).isNotNull();//
        assertThat(snapshotCreated.getSourceInstance().getId()).isNotNull();
        assertThat(snapshotCreated.getSourceInstance().getName().equalsIgnoreCase(instance.getCreateInstanceRequest().getName()));
        assertThat(snapshotCreated.getSourceInstance().getDiskSize() == instance.getCreateInstanceRequest().getDiskSize());
        assertThat(snapshotCreated.getSourceInstance().getDescription().equalsIgnoreCase(instance.getCreateInstanceRequest().getDescription()));//TODO bug
        assertThat(snapshotCreated.getSourceInstance().getStatus()).isNotNull();
        assertThat(snapshotCreated.getSourceInstance().getIpAddress()).isNotNull();
        assertThat(snapshotCreated.getSourceInstance().getMasterUsername().equalsIgnoreCase(instance.getCreateInstanceRequest().getMasterUsername()));
        assertThat(snapshotCreated.getSourceInstance().getVersion().equalsIgnoreCase(instance.getCreateInstanceRequest().getVersion()));
        assertThat(snapshotCreated.getSourceInstance().getEdition().equalsIgnoreCase(instance.getCreateInstanceRequest().getEdition()));
        assertThat(snapshotCreated.getSourceInstance().getPlan().equals(instance.getCreateInstanceRequest().getPlan()));
    }


    /*@Test//todo disable due to pitr is disabled
    public void testManualCreateSnapshotInstanceWithBackup() throws Exception {
        try {
            instance.getCreateInstanceRequest().getPitrSettings().setEnabled(true);
            GetInstanceResponse getInstanceResponse2 = createInstanceWithRetry(this.createInstanceRequest);
            CreateSnapshotRequest tmpSnapshotRequest = buildSnapshotRequest(getInstanceResponse2.getId());
            ResponseEntity<AsyncResponse> responseEntity = testClient.createSnapshotEntity(tmpSnapshotRequest);
            assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpURLConnection.HTTP_OK);
            AsyncResponse createResponse = responseEntity.getBody();
            assertThat(createResponse).isNotNull();
            assertThat(createResponse.getId()).isNotNull();
            assertThat(createResponse.getStatus()).contains(StatusCode.CREATING.value());
            GetSnapshotResponse snapshotCreated = this.retryTask.execute(new GetActiveSnapshotTask(createResponse.getId()));
            assertThat(snapshotCreated.getStatus()).isEqualTo(StatusCode.AVAILABLE.value());
            deleteInstanceWithRetry(getInstanceResponse2.getId());
        } finally {
            instance.getCreateInstanceRequest().getPitrSettings().setEnabled(false);
        }
    }*/

    @Test(groups = {"sanity"})
    public void testProvisionInstanceFromSnapshot() throws Exception {
        Employee testEmployee2 = RandomEmployee.getRandomEmployee();
        GetSnapshotResponse snapshotResponse = instance.createSnapshotWithRetry();
        instance.insertData(DB_TESTDB, testEmployee2);
        DbaasInstance restoredInstance = builder.restoreFromSnapshot(getNamePrefix(), snapshotResponse.getId());
        assertThat(restoredInstance.isDatabaseExists(DB_TESTDB)).isTrue();
        assertThat(restoredInstance.isDataExists(DB_TESTDB, testEmployee1)).isTrue();
        assertThat(restoredInstance.isDataExists(DB_TESTDB, testEmployee2)).isFalse();
        assertThat(restoredInstance.getInstanceResponse().getConnections().getDataPath().getDestPort()).isEqualTo(instance.getCreateInstanceRequest().getConnections().getDataPath().getDestPort());
        assertThat(restoredInstance.getInstanceResponse().getPitrSettings().isEnabled()).isEqualTo(instance.getCreateInstanceRequest().getPitrSettings().isEnabled());
        assertThat(restoredInstance.getInstanceResponse().getPlan().equals(instance.getCreateInstanceRequest().getPlan()));
    }

    @Test
    public void testProvisionInstanceFromSnapshotWithNewPassword() throws Exception {
        String newPassword = "newpassword";
        Employee testEmployee2 = RandomEmployee.getRandomEmployee();
        GetSnapshotResponse snapshotResponse = instance.createSnapshotWithRetry();
        instance.insertData(DB_TESTDB, testEmployee2);
        DbaasInstance restoredInstance = builder.restoreFromSnapshot(getNamePrefix(), snapshotResponse.getId(), newPassword);
        assertThat(restoredInstance.isDataExists(DB_TESTDB, testEmployee1)).isTrue();
        assertThat(restoredInstance.isDataExists(DB_TESTDB, testEmployee2)).isFalse();
        assertThat(restoredInstance.getInstanceResponse().getConnections().getDataPath().getDestPort()).isEqualTo(instance.getCreateInstanceRequest().getConnections().getDataPath().getDestPort());
        assertThat(restoredInstance.getInstanceResponse().getPitrSettings().isEnabled()).isEqualTo(instance.getCreateInstanceRequest().getPitrSettings().isEnabled());
        assertThat(restoredInstance.getInstanceResponse().getPlan().equals(instance.getCreateInstanceRequest().getPlan()));
    }


    /*@Test//todo disable due to pitr is disabled
    public void testProvisionInstanceFromSnapshotWithBackup() throws Exception {
        Employee testEmployee2 = RandomEmployee.getRandomEmployee();
        GetSnapshotResponse snapshotResponse = createSnapshotWithRetry(this.createSnapshotRequest);
        MsSqlDaoFactory jdbcClient = getDbConnection(this.getInstanceResponse);
        String username = this.getInstanceResponse.getMasterUsername();
        insertData(jdbcClient, DB_TESTDB, testEmployee2);
        RestoreFromSnapshotRequest requestInstanceFromSnapshot = buildInstanceRequestFromSnapshot(this.createInstanceRequest, snapshotResponse);

        requestInstanceFromSnapshot.getPitrSettings().setEnabled(true);
        GetInstanceResponse activeInstanceFromSnapshot = restoreFromSnapshotWithRetry(requestInstanceFromSnapshot);
        MsSqlDaoFactory jdbcClientFromSnapshot = getDbConnection(activeInstanceFromSnapshot, requestInstanceFromSnapshot.getMasterPassword());
        assertThat(testDbConnectionWithRetry(jdbcClientFromSnapshot)).isTrue();
        assertThat(jdbcClientFromSnapshot.createSysDao().isDatabaseExists(DB_TESTDB)).isTrue();
        jdbcClientFromSnapshot.createEmployeeDao().useDatabase(DB_TESTDB);
        assertThat(isDataExists(jdbcClientFromSnapshot.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
        assertThat(isDataExists(jdbcClientFromSnapshot.createEmployeeDao(), DB_TESTDB, testEmployee2)).isFalse();
        assertThat(activeInstanceFromSnapshot.getConnections().getDataPath().getDestPort()).isEqualTo(instance.getCreateInstanceRequest().getConnections().getDataPath().getDestPort());
        assertThat(activeInstanceFromSnapshot.getPitrSettings().isEnabled()).isEqualTo(true);

        getRestoreWindow(activeInstanceFromSnapshot.getId());
        jdbcClient = getDbConnection(activeInstanceFromSnapshot, requestInstanceFromSnapshot.getMasterPassword());
        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
        jdbcClient.createSysDao().createDatabase(DB_TESTDB2);
        assertThat(jdbcClient.createSysDao().isDatabaseExists(DB_TESTDB2)).isTrue();
        dropDatabase(jdbcClient, DB_TESTDB2);
        assertThat(jdbcClient.createSysDao().isDatabaseExists(DB_TESTDB2)).isFalse();


        deleteInstanceWithRetry(activeInstanceFromSnapshot.getId());
        assertThat(activeInstanceFromSnapshot.getPlan().equals(instance.getCreateInstanceRequest().getPlan()));
    }*/

//    @Test
//    @Ignore(reasons = "provision from new plan is not supported")
//    public void testProvisionInstanceFromSnapshotWithDifferentPlan() throws Exception {
//        Employee testEmployee2 = testData.get(1);
//        GetSnapshotResponse snapshotResponse = createSnapshotWithRetry(this.createSnapshotRequest);
//        MsSqlDaoFactory jdbcClient = getDbConnection(this.getInstanceResponse);
//        String username = this.getInstanceResponse.getMasterUsername();
//        insertData(jdbcClient, DB_TESTDB, testEmployee2);
//        PlanModel planData=getPlanWithDifferentId(instance.getCreateInstanceRequest().getPlan().getId());
//        RestoreFromSnapshotRequest requestInstanceFromSnapshot = buildInstanceRequestFromSnapshot(this.createInstanceRequest, snapshotResponse, planData);
//        GetInstanceResponse activeInstanceFromSnapshot = restoreFromSnapshotWithRetry(requestInstanceFromSnapshot);
//        MsSqlDaoFactory jdbcClientFromSnapshot = getDbConnection(activeInstanceFromSnapshot, requestInstanceFromSnapshot.getMasterPassword());
//        assertThat(testDbConnectionWithRetry(jdbcClientFromSnapshot)).isTrue();
//        assertThat(jdbcClientFromSnapshot.createSysDao().isDatabaseExists(DB_TESTDB)).isTrue();
//        jdbcClientFromSnapshot.createEmployeeDao().useDatabase(DB_TESTDB);
//        assertThat(isDataExists(jdbcClientFromSnapshot.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
//        assertThat(isDataExists(jdbcClientFromSnapshot.createEmployeeDao(), DB_TESTDB, testEmployee2)).isFalse();
//        deleteInstanceWithRetry(activeInstanceFromSnapshot.getId());
//    }





//    @Test
//    public void testDeleteInstanceDuringAutoCreateSnapshotting() throws Exception {
//        String instanceId = null;
//        try {
//            DebugProperties properties = new DebugProperties();
//            properties.setSnapshotCycle("00:00:02:00");
//            instance.getCreateInstanceRequest().setDebugProperties(properties);
//            SnapshotSettings snapshotSettings = instance.getCreateInstanceRequest().getSnapshotSettings();
//            snapshotSettings.setEnabled(true);
//            GetInstanceResponse getInstanceResponse = createInstanceWithRetry(this.createInstanceRequest);
//            instanceId = getInstanceResponse.getId();
//
//            RetryTask<GetInstanceResponse> retryTask = new RetryTask<>(new InstanceHelper.GetInstanceWithSpecificStatusTask(instanceId, StatusCode.SNAPSHOTTING));
//            GetInstanceResponse instanceCreated = retryTask.call();
//
//            sendDeleteInstanceRequest(instanceCreated.getId());
//            failBecauseExceptionWasNotThrown(RestException.class);
//        } catch (RestException e) {
//            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
//        } finally {
//            if (instanceId != null) {
//                recursiveDeleteInstanceWithRetry(instanceId);
//            }
//            instance.getCreateInstanceRequest().getSnapshotSettings().setEnabled(false);
//        }
//    }


    private String getHourAndMinute(Date time) {
        StringBuilder sb = new StringBuilder();
        int hour = time.getHours();
        if (hour < 10) {
            sb.append("0");
        }
        sb.append(hour);
        int minute = time.getMinutes();
        sb.append(":");
        if (minute < 10) {
            sb.append("0");
        }
        sb.append(minute);
        return sb.toString();
    }

    @Test(groups = {"sanity"})
    public void testAutoCreateSnapshotPreferredTime() throws Exception {
        DebugProperties properties = new DebugProperties();
        Date time = instance.getCurrentTime();
        checkNotNull(time);
        Date afterAddingFourMins = new Date(time.getYear(), time.getMonth(), time.getDate(), time.getHours(), time.getMinutes() + 4, time.getSeconds());
        String preferredStartTime = getHourAndMinute(afterAddingFourMins);
        logger.info("Preferred start time is: " + preferredStartTime);
        SnapshotSettings snapshotSettings = dbaasApi.generateSnapshotSettings(true);
        snapshotSettings.setPreferredStartTime(preferredStartTime);
        snapshotSettings.setEnabled(true);
        Instant start = Instant.now();
        DbaasInstance autoSnapshotInstance = builder.setAutoSnapshot(true).setSnapshotSettings(snapshotSettings).setDebugProperties(properties).createInstanceWithRetry(getNamePrefix());

        long totalSeconds = 120;
        Duration between;
        while (totalSeconds > 0) {
            sleepBySeconds(20);
            List<Data> snapshots = dbaasApi.listAvailableAndAutoSnapshots(autoSnapshotInstance.getInstanceid());
            between = Duration.between(start, Instant.now());
            logger.info("start seconds is: " + start.toEpochMilli());
            logger.info("now seconds is: " + Instant.now().toEpochMilli());
            logger.info("between seconds is: " + between.getSeconds());
            totalSeconds = totalSeconds - (between.getSeconds());
            start = Instant.now();
            logger.info("total seconds is: " + totalSeconds);
            if (totalSeconds > 0) {
                assertThat(snapshots.size()).isEqualTo(0);
            }
        }
        int limit = snapshotSettings.getLimit();
        autoSnapshotInstance.waitAutoSnapshotReachLimit();
        int count = 6;
        while (count != 0) {
            sleepBySeconds(30);
            assertThat(dbaasApi.listAvailableAndAutoSnapshots(autoSnapshotInstance.getInstanceid()).size()).isLessThanOrEqualTo(limit);
            count--;
        }
        dbaasApi.listAvailableAndAutoSnapshots(autoSnapshotInstance.getInstanceid()).stream().forEach(e -> assertThat(e.getType()).isEqualToIgnoringCase(Constant.AUTO.value()));
    }







    //@Ignore(reasons = "https://vchs-eng.atlassian.net/browse/DBAAS-649")
//    @Test
//    public void testConcurrentlyCreateDeleteSnapshotsOnOneDBInstance() throws Exception {
//        int threadSize = 4;
//        for (int i = 0; i < threadSize; i++) {
//            this.pool.submit(new CreateSnapshotTask(this.createSnapshotRequest));
//        }
//        for (int i = 0; i < threadSize; i++) {
//            try {
//                AsyncResponse createResponse = (AsyncResponse) this.pool.take().get();
//                RetryTask<GetSnapshotResponse> retryTask = new RetryTask<>(new GetActiveSnapshotTask(createResponse.getId()));
//                GetSnapshotResponse snapshotCreated = retryTask.call();
//                assertThat(snapshotCreated.getStatus()).isEqualTo(StatusCode.AVAILABLE.value());
//            } catch (ExecutionException e) {
//                assertThat(((RestException) e.getCause()).getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
//                //assertThat(((PortalError) ((RestException) e.getCause()).getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.RESOURCE_BUSY.getCode());
//            }
//        }
//        for (Data snapshotResponse : listSnapshots(this.getInstanceResponse.getId())) {
//            this.pool.submit(new CreateSnapshotTask(this.createSnapshotRequest));
//            this.pool.submit(new DeleteSnapshotTask(snapshotResponse.getId()));
//        }
//        for (int i = 0; i < threadSize * 2; i++) {
//            Object resultTaken = null;
//            try {
//                resultTaken = this.pool.take().get();//TODO error payload is inconsistent between apihead and portal backend.
//            } catch (Exception e) {
//                logger.info(e.getMessage());
//            }
//            if (resultTaken != null) {
//                if (resultTaken instanceof SnapshotResponse) {
//                    RetryTask<ResponseEntity<ListSnapshotResponse>> retryTask = new RetryTask<>(new GetDeletedSnapshotEntityTask(((SnapshotResponse) resultTaken).getId()));
//                    try {
//                        retryTask.call();
//                        failBecauseExceptionWasNotThrown(RestException.class);
//                    } catch (RestException e) {
//                        assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
//                        //assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.RESOURCE_NOT_FOUND.getCode());
////                        assertThat(((PortalError) e.getError()).getMessage()).isEqualTo(Error.SNAPSHOT_NOT_EXIST.value());
//                    }
//                } else if (resultTaken instanceof AsyncResponse) {
//                    RetryTask<GetSnapshotResponse> retryTask = new RetryTask<>(new GetActiveSnapshotTask(((AsyncResponse) resultTaken).getId()));
//                    GetSnapshotResponse snapshotCreated = retryTask.call();
//                    assertThat(snapshotCreated.getStatus()).isEqualTo(StatusCode.AVAILABLE.value());
//                }
//            }
//        }
//        assertThat(listSnapshots(this.getInstanceResponse.getId()).size()).isEqualTo(threadSize);
//    }


    @Test
    public void testInvalidManualCreateSnapshotRequest() throws Exception {
        try {
            instance.createSnapshotFromNonExistInstance();
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            //sns-disable will result in 404
            assertThat(e.getStatusCode()).isIn(HttpURLConnection.HTTP_INTERNAL_ERROR, HttpURLConnection.HTTP_NOT_FOUND);
//            assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.INVALID_ARGUMENT.getCode());
        }
    }

//    @Test
//    public void testCreateSnapshotDuringBackup() throws Exception {
//        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
//        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
//        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
//        insertData(jdbcClient, DB_TESTDB, testEmployee1);
//        assertThat(isDataExists(jdbcClient.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
//        Map<Integer, List<String>> result = getRestoreWindowWhenCreatingSnapshot(instanceCreated.getId());
//        int size = 0;
//        Iterator iterator = result.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry pairs = (Map.Entry) iterator.next();
//            size = (int) pairs.getKey();
//        }
//        assertThat(listAvailableSnapshots(instanceCreated.getId()).size()).isEqualTo(size);
//        deleteInstanceWithRetry(instanceCreated.getId());
//    }



//    @Test
//    public void testTheLimitOfAutoCreateSnapshot() throws Exception {
//        int createSize = 3;
//        int actualCreateSize = 0;
//        int deleteSize = 2;
//        try {
//            SnapshotSettings snapshotSettings = instance.getCreateInstanceRequest().getSnapshotSettings();
//            snapshotSettings.setEnabled(true);
//            DebugProperties properties = new DebugProperties();
//            properties.setSnapshotCycle("00:00:00:10");
//            instance.getCreateInstanceRequest().setDebugProperties(properties);
//            long cycleInSeconds = 10;
//            GetInstanceResponse getInstanceResponse = createInstanceWithRetry(this.createInstanceRequest);
//            int limit = snapshotSettings.getLimit();
//            String instanceId = getInstanceResponse.getId();
//            RetryTask<List<Data>> retryTask = new RetryTask<>(new GetLimitedSnapshotsTask(limit, instanceId));
//            retryTask.call();
//            for (int i = 0; i < createSize; i++) {
//                try {
//                    createSnapshotWithRetry(buildSnapshotRequest(instanceId));
//                    actualCreateSize++;
//                } catch (Exception e) {
//
//                }
//            }
//            List<Data> snapshotResponseList = listSnapshots(instanceId);
//            List<Data> manualSnapshotResponseList = snapshotResponseList.stream().filter(e -> e.getType().equals(SnapshotType.auto.toString())).collect(Collectors.toList());
//            for (int i = 0; i < deleteSize; i++) {
//                deleteSnapshotWithRetry(manualSnapshotResponseList.get(i).getId());
//            }
//            recursiveDeleteInstanceWithRetry(listAvailableAndAutoSnapshots(instanceId).get(0).getId());
//            sleepBySeconds(cycleInSeconds * 2);
//            assertThat(listSnapshots(instanceId).size()).isEqualTo(limit + actualCreateSize - deleteSize);
//        } finally {
//            instance.getCreateInstanceRequest().getSnapshotSettings().setEnabled(false);
//        }
//    }

    @Test
    public void testRecursiveProvisionInstanceFromSnapshots() throws Exception {
        Employee testEmployee2 = RandomEmployee.getRandomEmployee();
        GetSnapshotResponse snapshotResponse = instance.createSnapshotWithRetry();
        instance.insertData(DB_TESTDB, testEmployee2);
        DbaasInstance activeInstanceFromSnapshot = builder.restoreFromSnapshot(getNamePrefix(), snapshotResponse.getId());
        assertThat(activeInstanceFromSnapshot.isDatabaseExists(DB_TESTDB)).isTrue();
        assertThat(activeInstanceFromSnapshot.isDataExists(DB_TESTDB, testEmployee1)).isTrue();
        assertThat(activeInstanceFromSnapshot.isDataExists(DB_TESTDB, testEmployee2)).isFalse();
        assertThat(activeInstanceFromSnapshot.getInstanceResponse().getConnections().getDataPath().getDestPort()).isEqualTo(instance.getInstanceResponse().getConnections().getDataPath().getDestPort());
        assertThat(activeInstanceFromSnapshot.getInstanceResponse().getPitrSettings().isEnabled()).isEqualTo(instance.getInstanceResponse().getPitrSettings().isEnabled());

        activeInstanceFromSnapshot.insertData(DB_TESTDB, testEmployee2);
        Employee testEmployee3 = RandomEmployee.getRandomEmployee();

        GetSnapshotResponse snapshotResponse2 = instance.createSnapshotWithRetry();
        activeInstanceFromSnapshot.insertData(DB_TESTDB, testEmployee3);
        activeInstanceFromSnapshot.deleteWithRetry();

        DbaasInstance activeInstanceFromSnapshot2 = builder.restoreFromSnapshot(getNamePrefix(), snapshotResponse2.getId());
        assertThat(activeInstanceFromSnapshot2.isDatabaseExists(DB_TESTDB)).isTrue();
        assertThat(activeInstanceFromSnapshot2.isDataExists(DB_TESTDB, testEmployee1)).isTrue();
        assertThat(activeInstanceFromSnapshot2.isDataExists(DB_TESTDB, testEmployee2)).isTrue();
        assertThat(activeInstanceFromSnapshot2.isDataExists(DB_TESTDB, testEmployee3)).isFalse();

        assertThat(activeInstanceFromSnapshot2.getInstanceResponse().getConnections().getDataPath().getDestPort()).isEqualTo(instance.getInstanceResponse().getConnections().getDataPath().getDestPort());
        assertThat(activeInstanceFromSnapshot2.getInstanceResponse().getPitrSettings().isEnabled()).isEqualTo(instance.getInstanceResponse().getPitrSettings().isEnabled());
    }


//    @Test
//    public void testCreateAutoSnapshotWhileCreatAutoSnapshot() throws Exception {
//        final int MINUTES_TO_WAIT_FOR_AUTO_SNAPSHOT = 2;
//        DebugProperties properties = new DebugProperties();
//        properties.setSnapshotCycle("00:00:05:00");
//        instance.getCreateInstanceRequest().setDebugProperties(properties);
//        SnapshotSettings snapshotSettings = instance.getCreateInstanceRequest().getSnapshotSettings();
//        snapshotSettings.setEnabled(true);
//        String instanceId = null;
//        try {
//            GetInstanceResponse getInstanceResponse = createInstanceWithRetry(this.createInstanceRequest);
//            instanceId = getInstanceResponse.getId();
//
//            DateTime snapshotTime = getAutoSnapshotTime(instanceId);
//
//            logger.info("Write 300M data table." + Instant.now());
//            DbaasInstance instance = new DbaasInstance(getInstanceResponse, instance.getCreateInstanceRequest().getMasterPassword());
//            instance.loadDataBySize("INDB1", "INTable1", 300 * 1024);
//            logger.info("Write 300M data complete." + Instant.now());
//
//
//            UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
//            properties.setSnapshotCycle("00:00:01:00");
//            updateInstanceRequest.setDebugProperties(properties);
//
//
//            DateTime current = getCurrentTime(getInstanceResponse);
//
//            long diffInMillis = snapshotTime.getMillis() - current.getMillis() + 8000;
//            logger.info("time diff add 8 seconds in milliseconds is " + diffInMillis);
//            Thread.sleep(diffInMillis);
//
//
//            updateInstanceWithRetry(instanceId, updateInstanceRequest);
//            Thread.sleep(MINUTES_TO_WAIT_FOR_AUTO_SNAPSHOT);
//
//            assertThat(listAvailableAndAutoSnapshots(instanceId).size()).isEqualTo(0);
//        } finally {
//            if (instanceId != null) {
//                recursiveDeleteInstanceWithRetry(instanceId);
//            }
//            instance.getCreateInstanceRequest().getSnapshotSettings().setEnabled(false);
//        }
//    }

    @Test
    public void testConcurrentlyCreateSnapshotsOnOneDBInstance() throws Exception {
        int threadSize = getThreadSizeForInstance();
        List<String> snapshotId = new ArrayList<>();
        for (int i = 0; i < threadSize; i++) {
            try {
                snapshotId.add(dbaasApi.createSnapshot(dbaasApi.buildSnapshotRequest(instance.getInstanceid())).getId());
            }
            catch (Exception e){
                logger.info("Create snapshot failed when it is already snapshotting");
            }
        }
        assertThat(snapshotId.size()).isEqualTo(1);
        assertThat(snapshotId).isNotNull();
        dbaasApi.waitAndGetAvailableSnapshot(snapshotId.get(0)); 
        assertThat(dbaasApi.listSnapshots(instance.getInstanceid()).size()).isGreaterThan(0);
        assertThat(dbaasApi.listAvailableSnapshots(instance.getInstanceid())).hasSize(1);
    }
}
