package com.vmware.vchs.backup;

import com.vmware.vchs.InstanceBaseTest;
import com.vmware.vchs.common.utils.exception.RestException;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by georgeliu on 14/11/4.
 */
@Test(groups = {"backup"}, /*dependsOnGroups = {"instance", "alpha"},*/ alwaysRun = true)
public class BackupTest extends InstanceBaseTest {

    public void testProvisionDBWithBackupWithTrue() throws Exception {
        try {
            builder.setBackup(true).createInstanceWithRetry(getNamePrefix());
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    /*public void testProvisionDBWithBackup() throws Exception {
        this.createInstanceRequest.getPitrSettings().setEnabled(true);
        ResponseEntity<AsyncResponse> responseEntity = testClient.createDBInstanceEntity(this.createInstanceRequest);
        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpURLConnection.HTTP_OK);
        AsyncResponse createResponse = responseEntity.getBody();
        assertThat(createResponse).isNotNull();
        assertThat(createResponse.getId()).isNotNull();
        assertThat(createResponse.getStatus()).contains(StatusCode.CREATING.value());
        GetInstanceResponse instanceCreated = this.retryTask.execute(new GetActiveDBInstanceTask(createResponse.getId()));
        assertThat(instanceCreated.getStatus()).isEqualTo(StatusCode.AVAILABLE.value());
        getRestoreWindow(instanceCreated.getId());
        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
        jdbcClient.createSysDao().createDatabase(DB_TESTDB);
        assertThat(jdbcClient.createSysDao().isDatabaseExists(DB_TESTDB)).isTrue();
        dropDatabase(jdbcClient, DB_TESTDB);
        assertThat(jdbcClient.createSysDao().isDatabaseExists(DB_TESTDB)).isFalse();
    }

    @Test
    public void testProvisionDBFromInitialBackup() throws Exception {
        Employee testEmployee = testData.get(0);
        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
        insertData(jdbcClient, DB_TESTDB, testEmployee);
        assertThat(isDataExists(jdbcClient.createEmployeeDao(), DB_TESTDB, testEmployee)).isTrue();
        String[] firstRange = getRestoreWindow(instanceCreated.getId());
        String[] range = getRestoreWindowLeftEdgeAfterCurrentTime(jdbcClient, instanceCreated.getId(), firstRange);
        GetInstanceResponse activeInstanceFromBackup = createInstanceFromBackup(instanceCreated, range[0], masterPassword);
        MsSqlDaoFactory jdbcClientFromBackup = getDbConnection(activeInstanceFromBackup);
        assertThat(testDbConnectionWithRetry(jdbcClientFromBackup)).isTrue();
        assertThat(jdbcClientFromBackup.createSysDao().isDatabaseExists(DB_TESTDB)).isTrue();
        assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), DB_TESTDB, testEmployee)).isTrue();
    }

    public void testProvisionDBFromBackupWithNullPassword() throws Exception {
        Employee testEmployee = testData.get(0);
        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
        sleepBySeconds(5);
        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
        insertData(jdbcClient, DB_TESTDB, testEmployee);
        assertThat(isDataExists(jdbcClient.createEmployeeDao(), DB_TESTDB, testEmployee)).isTrue();
        String[] firstRange = getRestoreWindow(instanceCreated.getId());
        String[] range = getRestoreWindowRightEdgeAfterCurrentTime(jdbcClient, instanceCreated.getId(), firstRange);
        getCurrentTimeAfterRestoreTime(jdbcClient, range[1]);
        GetInstanceResponse activeInstanceFromBackup = createInstanceFromBackup(instanceCreated, range[1], null);
        MsSqlDaoFactory jdbcClientFromBackup = getDbConnection(activeInstanceFromBackup);
        assertThat(testDbConnectionWithRetry(jdbcClientFromBackup)).isTrue();
        assertThat(jdbcClientFromBackup.createSysDao().isDatabaseExists(DB_TESTDB)).isTrue();
        assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), DB_TESTDB, testEmployee)).isTrue();
    }

    public void testProvisionDBFromBackupWithNewPasswordAndDescription() throws Exception {
        Employee testEmployee = testData.get(0);
        String newPassword = "newPassword";
        String newDescription = "newDescription";
        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
        insertData(jdbcClient, DB_TESTDB, testEmployee);
        assertThat(isDataExists(jdbcClient.createEmployeeDao(), DB_TESTDB, testEmployee)).isTrue();
        String[] firstRange = getRestoreWindow(instanceCreated.getId());
        String[] range = getRestoreWindowRightEdgeAfterCurrentTime(jdbcClient, instanceCreated.getId(), firstRange);
        getCurrentTimeAfterRestoreTime(jdbcClient, range[1]);
        CreatePitrRequest createPitrRequest = buildPitrInstanceRequest();
        createPitrRequest.setMasterPassword(newPassword);
        createPitrRequest.setRestoreTime(range[1]);
        createPitrRequest.setDescription(newDescription);
        GetInstanceResponse activeInstanceFromBackup = createInstanceFromBackup(instanceCreated, createPitrRequest);
        assertThat(activeInstanceFromBackup.getDescription()).isEqualTo(newDescription);
        MsSqlDaoFactory jdbcClientFromBackup = getDbConnection(activeInstanceFromBackup, newPassword);
        assertThat(testDbConnectionWithRetry(jdbcClientFromBackup)).isTrue();
        assertThat(jdbcClientFromBackup.createSysDao().isDatabaseExists(DB_TESTDB)).isTrue();
        assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), DB_TESTDB, testEmployee)).isTrue();
    }

//    @Ignore(reasons = "disabled for plan large to small is not supported")
//    public void testProvisionDBFromBackupWithNewPlan() throws Exception {
//        Employee testEmployee = testData.get(0);
//        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
//        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
//        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
//        insertData(jdbcClient, DB_TESTDB, testEmployee);
//        assertThat(isDataExists(jdbcClient.createEmployeeDao(), DB_TESTDB, testEmployee)).isTrue();
//        String[] firstRange = getRestoreWindow(instanceCreated.getId());
//        String[] range = getRestoreWindowRightEdgeAfterCurrentTime(jdbcClient.createSysDao().getCurrentTime(), instanceCreated.getId(), firstRange);
//        getCurrentTimeAfterRestoreTime(jdbcClient, range[1]);
//        CreatePitrRequest createPitrRequest = buildPitrInstanceRequest();
//        createPitrRequest.setMasterPassword(masterPassword);
//        createPitrRequest.setRestoreTime(range[1]);
//        PlanModel planData = getPlanByName("Large");
//        Plan plan = planData.toPlan();
//        createPitrRequest.setPlan(plan);
//        try {
//            createInstanceFromBackup(instanceCreated, createPitrRequest);
//            failBecauseExceptionWasNotThrown(RestException.class);
//        } catch (RestException e) {
//            //TODO
//        }
//    }

    @Test(groups = {"sanity"}, priority = 3)
    public void testProvisionDBFromLastBackup() throws Exception {
        Employee testEmployee1 = testData.get(0);
        Employee testEmployee2 = testData.get(1);
        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
        String modifiedTime = instanceCreated.getUpdatedAt();
        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
        insertData(jdbcClient, DB_TESTDB, testEmployee1);
        assertThat(isDataExists(jdbcClient.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
        String[] firstRange = getRestoreWindow(instanceCreated.getId());
        String[] range = getRestoreWindowRightEdgeAfterCurrentTime(jdbcClient, instanceCreated.getId(), firstRange);
        getCurrentTimeAfterRestoreTime(jdbcClient, range[1]);
        insertData(jdbcClient, DB_TESTDB, testEmployee2);
        GetInstanceResponse activeInstanceFromBackup = createInstanceFromBackup(instanceCreated, range[1], masterPassword);
        MsSqlDaoFactory jdbcClientFromBackup = getDbConnection(activeInstanceFromBackup);
        //pitr-restore should not change source instance info's modifiedTime
        assertThat(instanceCreated.getUpdatedAt()).isEqualToIgnoringCase(modifiedTime);
        assertThat(testDbConnectionWithRetry(jdbcClientFromBackup)).isTrue();
        assertThat(jdbcClientFromBackup.createSysDao().isDatabaseExists(DB_TESTDB)).isTrue();
        assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
        assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), DB_TESTDB, testEmployee2)).isFalse();
    }

    @Test(groups = "snsonly")
    public void testProvisionDBFromLastBackupWithNonDefaultPort() throws Exception {
        if (this.configuration.getSns().equalsIgnoreCase("false")) {
            throw new SkipException("skip test case because sns not enabled");
        }
        try {
            int nonDefaultPort = 1785;
            int newPort = 1985;
            this.createInstanceRequest.getConnections().getDataPath().setDestPort(nonDefaultPort);
            Employee testEmployee1 = testData.get(0);
            Employee testEmployee2 = testData.get(1);
            GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
            String modifiedTime = instanceCreated.getUpdatedAt();
            MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
            assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
            insertData(jdbcClient, DB_TESTDB, testEmployee1);
            assertThat(isDataExists(jdbcClient.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
            String[] firstRange = getRestoreWindow(instanceCreated.getId());
            String[] range = getRestoreWindowRightEdgeAfterCurrentTime(jdbcClient, instanceCreated.getId(), firstRange);
            getCurrentTimeAfterRestoreTime(jdbcClient, range[1]);
            insertData(jdbcClient, DB_TESTDB, testEmployee2);
            CreatePitrRequest createPitrRequest = buildPitrInstanceRequest();
            createPitrRequest.setRestoreTime(range[1]);
            createPitrRequest.getConnections().getDataPath().setDestPort(newPort);
            GetInstanceResponse activeInstanceFromBackup = createInstanceFromBackup(instanceCreated, createPitrRequest);
            assertThat(activeInstanceFromBackup.getConnections().getDataPath().getDestPort()).isEqualTo(newPort);
            MsSqlDaoFactory jdbcClientFromBackup = getDbConnection(activeInstanceFromBackup);
            //pitr-restore should not change source instance info's modifiedTime
            assertThat(instanceCreated.getUpdatedAt()).isEqualToIgnoringCase(modifiedTime);
            assertThat(testDbConnectionWithRetry(jdbcClientFromBackup)).isTrue();
            assertThat(jdbcClientFromBackup.createSysDao().isDatabaseExists(DB_TESTDB)).isTrue();
            assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
            assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), DB_TESTDB, testEmployee2)).isFalse();
        } finally {
            this.createInstanceRequest.getConnections().getDataPath().setDestPort(DEFAULT_PORT);
        }
    }

    @Test
    public void testProvisionDBFromMiddleBackup() throws Exception {
        Employee testEmployee1 = testData.get(0);
        Employee testEmployee2 = testData.get(1);
        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
        insertData(jdbcClient, DB_TESTDB, testEmployee1);
        assertThat(isDataExists(jdbcClient.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
        String[] firstRange = getRestoreWindow(instanceCreated.getId());
        String[] range = getRestoreWindowMiddleTimeAfterCurrentTime(jdbcClient, instanceCreated.getId(), firstRange);
        String middleDateTime = TimeUtils.getMiddleDataTime(range);
        getCurrentTimeAfterRestoreTime(jdbcClient, middleDateTime);
        insertData(jdbcClient, DB_TESTDB, testEmployee2);
        GetInstanceResponse activeInstanceFromBackup = createInstanceFromBackup(instanceCreated, middleDateTime, masterPassword);
        MsSqlDaoFactory jdbcClientFromBackup = getDbConnection(activeInstanceFromBackup);
        assertThat(testDbConnectionWithRetry(jdbcClientFromBackup)).isTrue();
        assertThat(jdbcClientFromBackup.createSysDao().isDatabaseExists(DB_TESTDB)).isTrue();
        assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
        assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), DB_TESTDB, testEmployee2)).isFalse();
    }

    @Test
    public void testProvisionMultipleDBInstanceFromBackup() throws Exception {
        String db1 = DB_TESTDB + "1";
        String db2 = DB_TESTDB + "2";
        String db3 = DB_TESTDB + "3";
        Employee testEmployee1 = testData.get(0);
        Employee testEmployee2 = testData.get(1);
        Employee testEmployee3 = testData.get(2);
        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
        insertData(jdbcClient, db1, testEmployee1);
        insertData(jdbcClient, db1, testEmployee2);
        insertData(jdbcClient, db2, testEmployee2);
        insertData(jdbcClient, db2, testEmployee3);
        String[] firstRange = getRestoreWindow(instanceCreated.getId());
        String[] range = getRestoreWindowRightEdgeAfterCurrentTime(jdbcClient, instanceCreated.getId(), firstRange);
        getCurrentTimeAfterRestoreTime(jdbcClient, range[1]);
        insertData(jdbcClient, db3, testEmployee1);
        insertData(jdbcClient, db3, testEmployee3);
        GetInstanceResponse activeInstanceFromBackup = createInstanceFromBackup(instanceCreated, range[1], masterPassword);
        MsSqlDaoFactory jdbcClientFromBackup = getDbConnection(activeInstanceFromBackup);
        assertThat(testDbConnectionWithRetry(jdbcClientFromBackup)).isTrue();
        assertThat(jdbcClientFromBackup.createSysDao().isDatabaseExists(db1)).isTrue();
        assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), db1, testEmployee1)).isTrue();
        assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), db1, testEmployee2)).isTrue();
        assertThat(jdbcClientFromBackup.createSysDao().isDatabaseExists(db2)).isTrue();
        assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), db2, testEmployee2)).isTrue();
        assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), db2, testEmployee3)).isTrue();
        assertThat(jdbcClientFromBackup.createSysDao().isDatabaseExists(db3)).isFalse();
    }

    @Test
    public void testProvisionFromBackupWithMetaDataChange() throws Exception {
        Employee testEmployee1 = testData.get(0);
        Employee testEmployee2 = testData.get(1);
        String newPassword = "newPassowrd";
        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
        insertData(jdbcClient, DB_TESTDB, testEmployee1);
        String[] firstRange = getRestoreWindow(instanceCreated.getId());
        String[] range = getRestoreWindowMiddleTimeAfterCurrentTime(jdbcClient, instanceCreated.getId(), firstRange);
        String middleDateTime = TimeUtils.getMiddleDataTime(range);
        //getCurrentTimeAfterRestoreTime(jdbcClient, middleDateTime);
        insertData(jdbcClient, DB_TESTDB, testEmployee2);
        String userName = instanceCreated.getMasterUsername();
        //String oldPassword = ((InstanceResource) instanceCreated.getInstanceDetails()).getInstanceSettings().getMasterPassword();
        jdbcClient.createSysDao().changePassword(userName, newPassword);
        GetInstanceResponse activeInstanceFromBackup = createInstanceFromBackup(instanceCreated, middleDateTime, masterPassword);
        MsSqlDaoFactory jdbcClientFromBackup = getDbConnection(activeInstanceFromBackup);
        //assertThat(((InstanceResource) activeInstanceFromBackup.getInstanceDetails()).getInstanceSettings().getMasterPassword()).isEqualTo(oldPassword);
        assertThat(testDbConnectionWithRetry(jdbcClientFromBackup)).isTrue();
    }

    @Test
    public void testProvisionFromMetaDataBackupWithMultipleBackups() throws Exception {
        Employee testEmployee1 = testData.get(0);
        Employee testEmployee2 = testData.get(1);
        String newPassword = "newPassowrd";

        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
        insertData(jdbcClient, DB_TESTDB, testEmployee1);

        String[] firstRange = getRestoreWindow(instanceCreated.getId());
        String[] secondRange = getRestoreWindowMiddleTimeAfterCurrentTime(jdbcClient, instanceCreated.getId(), firstRange);
        String middleDateTime = TimeUtils.getMiddleDataTime(secondRange);
        getCurrentTimeAfterRestoreTime(jdbcClient, middleDateTime);

        insertData(jdbcClient, DB_TESTDB, testEmployee2);

        String userName = instanceCreated.getMasterUsername();
        jdbcClient.createSysDao().changePassword(userName, newPassword);
        GetInstanceResponse activeInstanceFromBackup = createInstanceFromBackup(instanceCreated, middleDateTime, masterPassword);
        MsSqlDaoFactory jdbcClientFromFirstBackup = getDbConnection(activeInstanceFromBackup);
        assertThat(testDbConnectionWithRetry(jdbcClientFromFirstBackup)).isTrue();
        assertThat(isDataExists(jdbcClientFromFirstBackup.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();

        deleteInstanceWithRetry(instanceCreated.getId());

        firstRange = getRestoreWindow(activeInstanceFromBackup.getId());
        String[] thirdRange = getRestoreWindowMiddleTimeAfterCurrentTime(jdbcClientFromFirstBackup, activeInstanceFromBackup.getId(), firstRange);
        middleDateTime = TimeUtils.getMiddleDataTime(thirdRange);

        GetInstanceResponse secondActiveInstanceFromBackup = createInstanceFromBackup(activeInstanceFromBackup, middleDateTime, newPassword);
        MsSqlDaoFactory jdbcClientFromSecondBackup = getDbConnection(secondActiveInstanceFromBackup, newPassword);
        assertThat(testDbConnectionWithRetry(jdbcClientFromSecondBackup)).isTrue();
    }

    @Test
    public void testProvisionFromDataBackupWithMultipleBackups() throws Exception {
        Employee testEmployee1 = testData.get(0);
        Employee testEmployee2 = testData.get(1);
        String db2 = DB_TESTDB + "2";

        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
        insertData(jdbcClient, DB_TESTDB, testEmployee1);

        String[] firstRange = getRestoreWindow(instanceCreated.getId());
        String[] secondRange = getRestoreWindowMiddleTimeAfterCurrentTime(jdbcClient, instanceCreated.getId(), firstRange);
        String middleDateTime = TimeUtils.getMiddleDataTime(secondRange);
        getCurrentTimeAfterRestoreTime(jdbcClient, middleDateTime);

        insertData(jdbcClient, db2, testEmployee2);

        GetInstanceResponse activeInstanceFromBackup = createInstanceFromBackup(instanceCreated, middleDateTime, masterPassword);
        MsSqlDaoFactory jdbcClientFromFirstBackup = getDbConnection(activeInstanceFromBackup);
        assertThat(testDbConnectionWithRetry(jdbcClientFromFirstBackup)).isTrue();
        assertThat(isDataExists(jdbcClientFromFirstBackup.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
        assertThat(isDataExists(jdbcClientFromFirstBackup.createEmployeeDao(), db2, testEmployee2)).isFalse();
        insertData(jdbcClientFromFirstBackup, db2, testEmployee2);

        deleteInstanceWithRetry(instanceCreated.getId());

        firstRange = getRestoreWindow(activeInstanceFromBackup.getId());
        String[] thirdRange = getRestoreWindowMiddleTimeAfterCurrentTime(jdbcClientFromFirstBackup, activeInstanceFromBackup.getId(), firstRange);
        middleDateTime = TimeUtils.getMiddleDataTime(thirdRange);

        GetInstanceResponse secondActiveInstanceFromBackup = createInstanceFromBackup(activeInstanceFromBackup, middleDateTime, masterPassword);
        MsSqlDaoFactory jdbcClientFromSecondBackup = getDbConnection(secondActiveInstanceFromBackup);
        assertThat(testDbConnectionWithRetry(jdbcClientFromSecondBackup)).isTrue();
        assertThat(jdbcClientFromSecondBackup.createSysDao().isDatabaseExists(DB_TESTDB)).isTrue();
        assertThat(jdbcClientFromSecondBackup.createSysDao().isDatabaseExists(db2)).isTrue();
        assertThat(isDataExists(jdbcClientFromSecondBackup.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
        assertThat(isDataExists(jdbcClientFromSecondBackup.createEmployeeDao(), db2, testEmployee2)).isTrue();
    }

    @Test
    public void testProvisionDBFromBackupWithInvalidLeftRestoreTime() throws Exception {
        Employee testEmployee1 = testData.get(0);
        Employee testEmployee2 = testData.get(1);
        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
        String[] firstRange = getRestoreWindow(instanceCreated.getId());
        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
        insertData(jdbcClient, DB_TESTDB, testEmployee1);
        assertThat(isDataExists(jdbcClient.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
        insertData(jdbcClient, DB_TESTDB, testEmployee2);
        String invalidRestoreTime = TimeUtils.getDateTime(TimeUtils.parseDate(firstRange[0]).minusSeconds(1));
        try {
            createInstanceFromBackup(instanceCreated, invalidRestoreTime, masterPassword);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
//            assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.RESOURCE_INACCESSIBLE.getTitle());
//            assertThat(((PortalError) e.getError()).getMessage()).containsIgnoringCase(Error.INCORRECT_PITR_TIME.value());
        }
    }

    @Test
    public void testProvisionDBFromBackupWithInvalidRightRestoreTime() throws Exception {
        Employee testEmployee1 = testData.get(0);
        Employee testEmployee2 = testData.get(1);
        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
        String[] firstRange = getRestoreWindow(instanceCreated.getId());
        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
        insertData(jdbcClient, DB_TESTDB, testEmployee1);
        assertThat(isDataExists(jdbcClient.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
        insertData(jdbcClient, DB_TESTDB, testEmployee2);
        String invalidRestoreTime = TimeUtils.getDateTime(TimeUtils.parseDate(firstRange[1]).plusHours(1));
        try {
            createInstanceFromBackup(instanceCreated, invalidRestoreTime, masterPassword);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
//            assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.RESOURCE_INACCESSIBLE.getTitle());
//            assertThat(((PortalError) e.getError()).getMessage()).containsIgnoringCase(Error.INCORRECT_PITR_TIME.value());
        }
    }

    @Test
    public void testRetentionWindow() throws Exception {
        String oldRetentionWindow = this.createInstanceRequest.getDebugProperties().getBackupRetention();
        try {
            String newRetentionWindow = "00:00:05:00";
            long retentionWindowInSeconds = getCycleAsSeconds(newRetentionWindow);
            this.createInstanceRequest.getDebugProperties().setBackupRetention(newRetentionWindow);
            GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
            MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
            assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
            String[] firstRange = getRestoreWindow(instanceCreated.getId());
            Duration firstDuration = TimeUtils.getDuration(firstRange);
            assertThat(firstDuration.getStandardSeconds()).isLessThan(retentionWindowInSeconds);
            int count = 4;
            String[] tempRange = firstRange;
            while (count != 0) {
                String[] range = getDifferentLeftEdgeRestoreWindow(instanceCreated.getId(), tempRange);
                Duration secondDuration = TimeUtils.getDuration(range);
                assertThat(secondDuration.getStandardSeconds()).isCloseTo(retentionWindowInSeconds, within(retentionWindowInSeconds * 3 / 2));
                tempRange = range;
                count--;
            }
        } finally {
            this.createInstanceRequest.getDebugProperties().setBackupRetention(oldRetentionWindow);
        }
    }*/

}
