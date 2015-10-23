package com.vmware.vchs.instance;

import com.vmware.vchs.base.BaseDataProvider;
import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.common.utils.exception.RestException;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.model.portal.instance.UpdateInstanceRequest;
import com.vmware.vchs.test.client.db.model.Employee;
import com.vmware.vchs.utils.RandomEmployee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.HttpURLConnection;

import static com.vmware.vchs.test.client.db.SQLStatements.DB_TESTDB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * Created by georgeliu on 14/11/4.
 */
public class UpdateInstanceWithPreCreateInstanceTest extends InstanceTest {

    private static GetInstanceResponse getInstanceResponse;
    private static final Logger logger = LoggerFactory.getLogger(UpdateInstanceWithPreCreateInstanceTest.class);
    protected DbaasInstance instance;
    protected Employee testEmployee1 = RandomEmployee.getRandomEmployee();

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod(Method method) throws Exception {
        logger.info("Setup for update instance test method...");
        super.setUpMethod(method);
        preCreateInstance();
    }

    protected void preCreateInstance() throws Exception {
        if (this.instance != null) {
            if (instance.checkAvailability()) {
                return;
            }
        }
        this.instance = builder.createInstanceWithRetry(getNamePrefix());
        if (this.instance == null) {
            throw new Exception("create instance for snapshot failed");
        }
        instance.insertData(DB_TESTDB, testEmployee1);
    }

    @Test(groups = {"sanity", "alpha"}, dataProvider = "can be updated", dataProviderClass = BaseDataProvider.class)
    public void testUpdatePropertiesCanBeUpdated(UpdateInstanceRequest updateInstanceRequest) throws Exception {
        GetInstanceResponse instanceUpdated = instance.updateInstanceWithRetry(updateInstanceRequest);
        dbaasApi.verifyUpdatedInstanceWithData(instanceUpdated, updateInstanceRequest, true);
        if (instanceUpdated != null) {
            String instanceId = instanceUpdated.getId();
            dbaasApi.deleteForDBInstance(instanceId);
            dbaasApi.waitInstanceDeleted(instanceId);
        }
    }

    /*@Test
    public void testUpdatePitrPropertiesToTrue() throws Exception {
        Employee testEmployee1 = testData.get(0);
        Employee testEmployee2 = testData.get(1);
        UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
        PitrSettings pitrSettings = new PitrSettings();
        pitrSettings.setEnabled(true);
        pitrSettings.setRetention(2);
        updateInstanceRequest.setPitrSettings(pitrSettings);
        String instanceId = this.getInstanceResponse.getId();
        GetInstanceResponse instanceUpdated = updateInstanceWithRetry(instanceId, updateInstanceRequest);
        verifyUpdatedInstanceWithData(instanceUpdated, updateInstanceRequest, true);
        MsSqlDaoFactory jdbcClient = getDbConnection(this.getInstanceResponse);
        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
        insertData(jdbcClient, DB_TESTDB, testEmployee1);
        assertThat(isDataExists(jdbcClient.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
        String[] firstRange = getRestoreWindow(instanceId);
        String[] range = getRestoreWindowRightEdgeAfterCurrentTime(jdbcClient, instanceId, firstRange);
        getCurrentTimeAfterRestoreTime(jdbcClient, range[1]);
        insertData(jdbcClient, DB_TESTDB, testEmployee2);
        GetInstanceResponse activeInstanceFromBackup = createInstanceFromBackup(this.getInstanceResponse, range[1], masterPassword);
        MsSqlDaoFactory jdbcClientFromBackup = getDbConnection(activeInstanceFromBackup);
        assertThat(testDbConnectionWithRetry(jdbcClientFromBackup)).isTrue();
        assertThat(jdbcClientFromBackup.createSysDao().isDatabaseExists(DB_TESTDB)).isTrue();
        assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
        assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), DB_TESTDB, testEmployee2)).isFalse();
    }

    @Test
    public void testUpdatePitrPropertiesToFalse() throws Exception {
        try {
            this.createInstanceRequest.getPitrSettings().setEnabled(true);
            GetInstanceResponse getInstanceResponseWithBackup = createInstanceWithRetry(this.createInstanceRequest);
            String[] firstRange = getRestoreWindow(getInstanceResponseWithBackup.getId());
            assertThat(firstRange).isNotNull();
            UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
            PitrSettings pitrSettings = new PitrSettings();
            pitrSettings.setEnabled(false);
            pitrSettings.setRetention(2);
            updateInstanceRequest.setPitrSettings(pitrSettings);
            String instanceId = getInstanceResponseWithBackup.getId();
            GetInstanceResponse instanceUpdated = updateInstanceWithRetry(instanceId, updateInstanceRequest);
            verifyUpdatedInstanceWithData(instanceUpdated, updateInstanceRequest, true);
            assertThat(instanceUpdated.getAvailableRestoreWindows()).isNull();
        } finally {
            this.createInstanceRequest.getPitrSettings().setEnabled(false);
        }
    }*/

    @Test(groups = {"sanity", "alpha"}, dataProvider = "can not be updated", dataProviderClass = BaseDataProvider.class)
    public void testUpdatePropertiesCanNotBeUpdated(UpdateInstanceRequest updateInstanceRequest) throws Exception {
        try {
            instance.updateInstanceWithRetry(updateInstanceRequest);
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
//            assertThat(((PortalError) e.getError()).getCode()).isIn(PortalErrorMap.PortalStatus.INVALID_ARGUMENT.getTitle());
        }
    }

//    @Test(groups = {"alpha"}, dataProvider = "disk can be updated", dataProviderClass = BaseDataProvider.class)
//    public void testUpdateDiskDuringProvisionInstance(UpdateInstanceRequest updateInstanceRequest) throws Exception {
//        clearInstances();
//        AsyncResponse createResponse = testClient.createDBInstance(this.createInstanceRequest);
//        String instanceId = createResponse.getId();
//        GetInstanceResponse instanceUpdated = null;
//        try {
//            instanceUpdated = updateInstanceWithRetry(instanceId, updateInstanceRequest);
//        } catch (RestException e) {
//            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
//            assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.INVALID_ARGUMENT.getCode());
//        }
//        assertThat(instanceUpdated).isNull();
//    }
//
//    @Test(groups = {"alpha"}, dataProvider = "disk can be updated", dataProviderClass = BaseDataProvider.class)
//    public void testUpdateDiskDuringUnProvisionInstance(UpdateInstanceRequest updateInstanceRequest) throws Exception {
//        clearInstances();
//        GetInstanceResponse createResponse = createInstanceWithRetry(this.createInstanceRequest);
//        String instanceId = createResponse.getId();
//        testClient.deleteDBInstance(instanceId);
//        GetInstanceResponse instanceUpdated = null;
//        try {
//            instanceUpdated = updateInstanceWithRetry(instanceId, updateInstanceRequest);
//        } catch (RestException e) {
//            assertThat(e.getStatusCode()).isIn(HttpURLConnection.HTTP_BAD_REQUEST, HttpURLConnection.HTTP_NOT_FOUND);
//            assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.INVALID_ARGUMENT.getCode());
//        }
//        assertThat(instanceUpdated).isNull();
//    }


    @Test(groups = {"alpha"}, dataProvider = "can be updated", dataProviderClass = BaseDataProvider.class)
    public void testUpdateDuringDeleteInstance(UpdateInstanceRequest updateInstanceRequest) throws Exception {
        instance.delete();
        try {
            instance.updateInstance(updateInstanceRequest);
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isIn(HttpURLConnection.HTTP_BAD_REQUEST, HttpURLConnection.HTTP_NOT_FOUND);
//            assertThat(((PortalError) e.getError()).getCode()).isIn("GW" + PortalErrorMap.PortalStatus.INVALID_ARGUMENT.getCode(), PortalErrorMap.PortalStatus.RESOURCE_NOT_FOUND.getCode());
        }
        instance.waitInstanceDeleted();
        instance = null;
    }

    /*@Test(dataProvider = "can be updated", dataProviderClass = BaseDataProvider.class)//todo disable due to pitr is disabled
    public void testUpdateWithBackup(UpdateInstanceRequest updateInstanceRequest) throws Exception {
        clearInstances();
        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
        String instanceId = instanceCreated.getId();
        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
        Assertions.assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
        String[] firstRange = getRestoreWindow(instanceCreated.getId());
        String[] range = getRestoreWindowRightEdgeAfterCurrentTime(jdbcClient, instanceCreated.getId(), firstRange);
        GetInstanceResponse instanceUpdated = updateInstanceWithRetry(instanceId, updateInstanceRequest);
        verifyUpdatedInstanceWithData(instanceUpdated, updateInstanceRequest, true);
        GetInstanceResponse activeInstanceFromBackup = createInstanceFromBackup(instanceCreated, range[1], null);
        MsSqlDaoFactory jdbcClientFromBackup = getDbConnection(activeInstanceFromBackup);
        Assertions.assertThat(testDbConnectionWithRetry(jdbcClientFromBackup)).isTrue();
        verifyUpdatedInstanceWithData(testClient.getDBInstance(activeInstanceFromBackup.getId()), updateInstanceRequest, false);
    }*/

    /*@Test(dataProvider = "can be updated during backup", dataProviderClass = BaseDataProvider.class)
    public void testUpdateWithSnapshot(UpdateInstanceRequest updateInstanceRequest) throws Exception {
        String newPassword = "newPassword";
        clearInstances();
        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
        CreateSnapshotRequest createSnapshotRequest = buildSnapshotRequest(instanceCreated.getId());
        try {
            GetSnapshotResponse snapshotResponse = createSnapshotWithRetry(createSnapshotRequest);
            String instanceId = instanceCreated.getId();
            GetInstanceResponse instanceUpdated = updateInstanceWithRetry(instanceId, updateInstanceRequest);
            verifyUpdatedInstanceWithData(instanceUpdated, updateInstanceRequest, true);
            deleteInstanceWithRetry(instanceCreated.getId());
            RestoreFromSnapshotRequest requestInstanceFromSnapshot = buildInstanceRequestFromSnapshot(this.createInstanceRequest, snapshotResponse, newPassword);
            GetInstanceResponse activeInstanceFromSnapshot = restoreFromSnapshotWithRetry(requestInstanceFromSnapshot);
            MsSqlDaoFactory jdbcClientFromSnapshot = getDbConnection(activeInstanceFromSnapshot, newPassword);
            Assertions.assertThat(testDbConnectionWithRetry(jdbcClientFromSnapshot)).isTrue();
            verifyUpdatedInstanceWithData(activeInstanceFromSnapshot, updateInstanceRequest, false);
        } finally {
            clearSnapshots();
        }
    }*/


    /*@Test(dataProvider = "can be updated during backup", dataProviderClass = BaseDataProvider.class)//todo disable due to pitr is disabled
    public void testUpdateDuringBackup(UpdateInstanceRequest updateInstanceRequest) throws Exception {
        Employee testEmployee1 = this.testData.get(0);
        clearInstances();
        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
        Assertions.assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
        insertData(jdbcClient, DB_TESTDB, testEmployee1);
        Assertions.assertThat(isDataExists(jdbcClient.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
        String instanceId = instanceCreated.getId();
        GetInstanceResponse instanceUpdated = getRestoreWindowWhenUpdatingInstance(instanceId, updateInstanceRequest);
        verifyUpdatedInstanceWithData(instanceUpdated, updateInstanceRequest, true);
    }*/
}
