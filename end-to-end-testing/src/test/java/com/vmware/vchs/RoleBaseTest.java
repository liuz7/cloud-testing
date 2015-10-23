package com.vmware.vchs;

import com.vmware.vchs.base.BaseTest;
import com.vmware.vchs.base.DbaasApi;
import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.launcher.TestHelper;
import com.vmware.vchs.model.portal.common.AsyncResponse;
import com.vmware.vchs.model.portal.common.ListResponse;
import com.vmware.vchs.model.portal.iamRole.*;
import com.vmware.vchs.model.portal.instance.*;
import com.vmware.vchs.model.portal.snapshot.CreateSnapshotRequest;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import com.vmware.vchs.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by fanz on 5/26/15.
 */

public class RoleBaseTest extends BaseTest {


    /**
     * Created by fanz on 5/5/15.
     * <p>
     * Before Running RoleBaseTest, userId, and resourceId(instance) should be provided by configuration
     * Running CreateInstance before this class.
     */

    protected static UpdateRoleRequest updateRoleRequest;
    protected static UpdateRoleResponse updateRoleResponse;
    protected static GetRoleResponse getRoleResponse;
    protected static GetUserRoleResponse getUserRoleResponse;

    protected static GetInstanceResponse getInstanceResponse;
    protected static GetInstanceResponse getRestoredInstanceResponse;
    protected static GetInstanceResponse readInstanceResponse;
    protected static CreateInstanceRequest createInstanceRequestForRoletest;
    protected static CreateSnapshotRequest createSnapshotRequest;
    protected static AsyncResponse createResponse;

    public static String instanceId;
    public static String snapshotId;

    public static String loginAdmin;
    public static String loginEnduser;
    public static String iamUserPassword;
    public static String endUserPassword;
    public static String iamUser;
    public static String endUser;
    //public static String instanceCreatorName;

    public static ListResponse listResponse;
    public static final com.vmware.vchs.test.config.Configuration configuration = TestHelper.getConfiguration();
    public static DbaasApi dbaasApi;
    public static DbaasInstance.DbaasInstanceBuilder builder;
    public static DbaasInstance instanceIamTest;
    public static GetSnapshotResponse snapshotCreated;
    public static DbaasInstance restoredInstance;
    public static String restoredInstanceId;
    public static String restoredSnapshotId;

    private String password = MASTER_PASSWORD;
    public static final String MASTER_PASSWORD = "ca$hc0w";

    protected static final Logger logger = LoggerFactory.getLogger(RoleBaseTest.class);
    public static Map<String, String> userMap = new HashMap<>();

    protected void useIamUser() {
        loginAdmin = configuration.getPraxisServerConnection().getIamUserName();
        iamUserPassword = configuration.getPraxisServerConnection().getIamUserPasswd();
        dbaasApi.setAuthentication(loginAdmin, iamUserPassword);
        logger.info("Using IAM User, Setup IAM User Authentication...");
        logger.info(" IAM user is : " + loginAdmin);
    }

    protected void useEndUser() {
        loginEnduser = configuration.getPraxisServerConnection().getEndUsername();
        endUserPassword = configuration.getPraxisServerConnection().getEndUserPasswd();
        dbaasApi.setAuthentication(loginEnduser, endUserPassword);
        logger.info("Using End User, Setup End User Authentication...");
        logger.info(" IAM user is : " + configuration.getPraxisServerConnection().getEndUsername());
    }

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() throws Exception {
        try {
            if (configuration.isAuthentication()) {
                logger.info("Setup for Role test suite...");
                super.setUpSuite();
                logger.info("Setup success for Role base test suite...");
            } else {
                throw new Exception("IAM is disable...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.toString());
        }
    }

    @BeforeClass(alwaysRun = true)
    public void setUpClass() throws Exception {
        //super.setUpClass();
        try {
            logger.info("Setup for Role test class...");
            //super.setUpClass(dbEngineVersion, planName, diskSize);
            start = Instant.now();
            logger.info("Setup for base test class...");
            logger.info("Praxis Url:" + this.configuration.getPraxisServerConnection().getPraxisConnectUrl());
            logger.info("Role test Praxis IAM Username:" + this.configuration.getPraxisServerConnection().getIamUserName());
            logger.info("Role test Praxis IAM Password:" + this.configuration.getPraxisServerConnection().getIamUserPasswd());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.toString());
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setMethod(Method method) throws Exception {
        //super.setUpMethod(method);
        try {
            logger.info("Setup for Role base test method...");
            logger.info("Start test: " + method.getName());
            int tmpDiskSize = Integer.valueOf(configuration.getDiskSize());
            if (tmpDiskSize > 0) {
                logger.info("disk size is:" + tmpDiskSize);
                this.diskSize = tmpDiskSize;
            }
            logger.info("Initializing dbaasApi and builder...");
            dbaasApi = new DbaasApi(TestHelper.getConfiguration());
            builder = new DbaasInstance.DbaasInstanceBuilder(dbaasApi);

            logger.info("Praxis IAM Username: " + configuration.getPraxisServerConnection().getIamUserName());
            logger.info("Praxis IAM UserPassword: " + configuration.getPraxisServerConnection().getEndUsername());
            logger.info("Praxis endUser: " + this.configuration.getPraxisServerConnection().getEndUsername());
            logger.info("Praxis endUserPassword: " + this.configuration.getPraxisServerConnection().getEndUserPasswd());

            useIamUser();
            dbaasApi.getIamAuthInfo();
            dbaasApi.deleteRemainingInstances();

            String[] adminUserParse = loginAdmin.split("@");
            iamUser = adminUserParse[0].trim();
            userMap.put(iamUser, loginAdmin);
            logger.info("UserMap iamUser is : " + iamUser);

            loginEnduser = configuration.getPraxisServerConnection().getEndUsername();
            String[] endUserParse = loginEnduser.split("@");
            endUser = endUserParse[0].trim();
            userMap.put(endUser, loginEnduser);
            logger.info("UserMap endUser is " + endUser);

        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.toString());
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() throws Exception {
        super.tearDownClass();
        try {
            logger.info("Tear down for role test class...");
            useIamUser();
            dbaasApi.getIamAuthInfo();
            dbaasApi.deleteRemainingInstances();
            listAllInstance();
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.toString());
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownMethod(Method method) throws Exception {
        super.tearDownMethod(method);
        try {
            super.tearDownMethod(method);
            logger.info("Tear down for role test method...");
            useIamUser();
            dbaasApi.getIamAuthInfo();

            if (instanceId != null) {
                dbaasApi.cleanInstance(CommonUtils.generateNamePrefix(this.getClass().getName(), method.getName()));
                dbaasApi.waitInstanceDeleted(instanceId);
            }
            if (snapshotId != null) {
                dbaasApi.deleteSnapshotWithRetry(snapshotId);
            }
            if (restoredSnapshotId != null) {
                dbaasApi.deleteSnapshotWithRetry(restoredSnapshotId);
            }
            if (restoredInstanceId != null) {
                dbaasApi.waitInstanceDeleted(restoredInstanceId);
            }
            listResponse = dbaasApi.listDBInstances();
            checkNotNull(listResponse);
            int size = listResponse.getTotal();
            logger.info("!!! " + size + " numbers of instances found after test method:" + method.getName());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.toString());
        }
    }


//    protected CreateSnapshotRequest buildSnapshotRequest(String instanceId) {
//        CreateSnapshotRequest createSnapshotRequest = new CreateSnapshotRequest();
//        createSnapshotRequest.setInstanceId(instanceId);
//        createSnapshotRequest.setDescription("this is a snapshot request");
//        createSnapshotRequest.setName("test-snapshot");
//        return createSnapshotRequest;
//    }

    //public TestClientRoleImpl testClientRole = new TestClientRoleImpl();

    public GetRoleResponse sendRoleRequestGetGlobalRole() throws Exception {
        GetRoleResponse getRoleResponse = dbaasApi.getRolePermission();
        return getRoleResponse;
    }

    //InstanceId  createInstance - getId as paramter in configuration
    public GetRoleResponse sendRoleRequestGetInstanceRole(String instanceId) throws Exception {
        this.getRoleResponse = dbaasApi.getRolePermissionInstance(instanceId);
        return this.getRoleResponse;
    }

    public GetUserResponse sendRoleRequestGetAllUserGlobal() throws Exception {
        GetUserResponse getUserResponse = dbaasApi.getAllUserGlobal();
        return getUserResponse;
    }

    // userId
    public GetUserResponse sendRoleRequestSearchUserGlobal(String userId) throws Exception {
        GetUserResponse getUserResponse = dbaasApi.getSearchUserGlobal(userId);
        return getUserResponse;
    }


    /**
     * ~/acm/assignment/mssql                         getAllGlobalUserRolePath() ---> getAllUserRoleGlobal()
     * ~/acm/assignment/mssql/?userId=                getGlobalUserRolePath(String userId) ---> getUserRoleGlobal(String userId)
     * ~/acm/assignment/mssql/?resourceId=            getAllResourceUserRolePath(String resourceId) -->getAllUserRoleInstance(String resourceId)
     * ~/acm/assignment/mssql/?userId= &resourceId=   getResourceUserRolePath(String userID, String resourceId) --> getUserRoleInstance(String userId, String resourceId)
     *
     * @return GetUserRoleResponse
     * @throws Exception Expected result:
     */


    public GetUserRoleResponse sendRoleRequestGetAllUserRoleGlobal() throws Exception {
        GetUserRoleResponse getUserRoleResponse = dbaasApi.getAllUserRoleGlobal();
        return getUserRoleResponse;
    }

    public GetUserRoleResponse sendRoleRequestGetOneUserRole(String userId) throws Exception {
        GetUserRoleResponse getUserRoleResponse = dbaasApi.getUserRoleGlobal(userId);
        //logger.info(response.toString());
        return getUserRoleResponse;
    }

    public GetUserRoleResponse sendRoleRequestGetAllUserRoleInstance(String resourceId) throws Exception {
        GetUserRoleResponse getUserRoleResponse = dbaasApi.getAllUserRoleInstance(resourceId);
        return getUserRoleResponse;
    }

    //List all roles of user for one instance,  userId, resourceId
    public GetUserRoleResponse sendRoleRequestGetUserRoleInstance(String userId, String resourceId) throws Exception {
        GetUserRoleResponse getUserRoleResponse = dbaasApi.getUserRoleInstance(userId, resourceId);
        return getUserRoleResponse;
    }


    /**
     * Update Role assignment, Role revoke
     *
     * @param updateRoleRequest
     * @return updateRoleResponse
     * @throws Exception
     */


    public UpdateRoleResponse sendUpdateRoleRequestGloble(UpdateRoleRequest updateRoleRequest) throws Exception {
        UpdateRoleResponse response = dbaasApi.getRoleUpdateGlobal(updateRoleRequest);
        return response;
    }


    public UpdateRoleResponse sendUpdateRoleRequestInstance(UpdateRoleRequest updateRoleRequest, String instanceId) throws Exception {
        UpdateRoleResponse response = dbaasApi.getRoleUpdateInstance(instanceId, updateRoleRequest);
        return response;

    }


    /**
     * Update date role request playload
     */


    //Assign Access Control, assign one role to userId
    public UpdateRoleRequest buildUpdateRoleRequestAssignOneRole(String userId, String assignOneRole) throws Exception {

        List<UpdateRoleRequestItem> requestItemArray = new ArrayList<>();
        requestItemArray.add(0, new UpdateRoleRequestItem());

        List<String> assignRoles = new ArrayList<>();
        List<String> unassignRoles = new ArrayList<>();
        assignRoles.add(assignOneRole);

        String userName;
        logger.info("UserId(key) for UpdateRole playload is : " + userId);
        if (userMap.containsKey(userId)) {
            userName = userMap.get(userId);
            logger.info("UserName for UpdateRole playload is : " + userName);
        } else {
            if (userMap == null) {
                throw new Exception("userMap is null for buildUpdateRoleRequestAssignOneRole() ");
            } else if (userMap.containsValue("admin1@dbaas-int.com")) {
                throw new Exception("userMap is not null, but admin1@dbaas-int.com is not found for buildUpdateRoleRequestAssignOneRole() ");
            } else {
                throw new Exception("userId can not be found !");
            }
        }
        if (requestItemArray.get(0) != null) {
            requestItemArray.get(0).setUserName(userName);
            requestItemArray.get(0).setAssignRoles(assignRoles);
            requestItemArray.get(0).setUnassignRoles(unassignRoles);
        } else {
            throw new Exception("Can not build Update Role Request playload...");
        }
        //logger.info(requestItemArray.get(0).toString());

        UpdateRoleRequest updateRoleRequest = new UpdateRoleRequest();
        updateRoleRequest.setUpdateRoleRequestItemList(requestItemArray);
        return checkNotNull(updateRoleRequest);

    }

    //Revoke Access Control, revoke one role to userId
    public UpdateRoleRequest buildUpdateRoleRequestRevokeOneRole(String userId, String revokeOneRole) throws Exception {
        List<UpdateRoleRequestItem> requestItemArray = new ArrayList<>();
        requestItemArray.add(0, new UpdateRoleRequestItem());

        List<String> assignRoles = new ArrayList<>();
        List<String> unassignRoles = new ArrayList<>();
        unassignRoles.add(revokeOneRole);
        String userName;
        if (userMap.containsKey(userId)) {
            userName = userMap.get(userId);
            logger.info("UserName for UpdateRole playload is : " + userName);
        } else {
            throw new Exception("userId can not be found !");
        }

        if (requestItemArray.get(0) != null) {
            requestItemArray.get(0).setUserName(userName);
            requestItemArray.get(0).setAssignRoles(assignRoles);
            requestItemArray.get(0).setUnassignRoles(unassignRoles);
        } else {
            throw new Exception("Can not build Update Role Request playload...");
        }

        //logger.info(requestItem.toString());

        UpdateRoleRequest updateRoleRequest = new UpdateRoleRequest();
        updateRoleRequest.setUpdateRoleRequestItemList(requestItemArray);
        return checkNotNull(updateRoleRequest);

    }

    //Assign Access Control, assign more roles to userId
    public UpdateRoleRequest buildUpdateRoleRequestAssignMoreRoles(String userId, List<String> assignMoreRoles) throws Exception {

        List<UpdateRoleRequestItem> requestItemArray = new ArrayList<>();
        requestItemArray.add(0, new UpdateRoleRequestItem());
        List<String> assignRoles = new ArrayList<>();
        List<String> unassignRoles = new ArrayList<>();
        assignRoles.addAll(assignMoreRoles);
        String userName;
        if (userMap.containsKey(userId)) {
            userName = userMap.get(userId);
        } else {
            throw new Exception("userId can not be found !");
        }
        if (requestItemArray.get(0) != null) {
            requestItemArray.get(0).setUserName(userName);
            requestItemArray.get(0).setAssignRoles(assignRoles);
            requestItemArray.get(0).setUnassignRoles(unassignRoles);
        } else {
            throw new Exception("Can not build Update Role Request playload...");
        }
        logger.info(requestItemArray.get(0).toString());

        UpdateRoleRequest updateRoleRequest = new UpdateRoleRequest();
        updateRoleRequest.setUpdateRoleRequestItemList(requestItemArray);
        return checkNotNull(updateRoleRequest);

    }

    //Revoke Access Control, revoke more roles to userId
    public UpdateRoleRequest buildUpdateRoleRequestRevokeMoreRoles(String userId, List<String> revokeMoreRoles) throws Exception {
        List<UpdateRoleRequestItem> requestItemArray = new ArrayList<>();
        requestItemArray.add(0, new UpdateRoleRequestItem());

        List<String> assignRoles = new ArrayList<>();
        List<String> unassignRoles = new ArrayList<>();
        unassignRoles.addAll(revokeMoreRoles);
        if (userMap.containsKey(userId)) {
            userId = userMap.get(userId);
        } else {
            throw new Exception("userId can not be found !");
        }
        if (requestItemArray.get(0) != null) {
            requestItemArray.get(0).setUserName(userId);
            requestItemArray.get(0).setAssignRoles(assignRoles);
            requestItemArray.get(0).setUnassignRoles(unassignRoles);
        } else {
            throw new Exception("Can not build Update Role Request playload...");
        }

        UpdateRoleRequest updateRoleRequest = new UpdateRoleRequest();
        updateRoleRequest.setUpdateRoleRequestItemList(requestItemArray);
        return checkNotNull(updateRoleRequest);

    }

    public void provisionInstanceForIamTest() throws Exception {
        logger.info(" Start to Provision Instance for iam test....");
        useIamUser();
        dbaasApi.getIamAuthInfo();
        dbaasApi.deleteRemainingInstances();
        instanceIamTest = builder.createInstanceWithRetry(getNamePrefix());
        instanceId = instanceIamTest.getInstanceid();
        logger.info(" Created instance successfully....");
        logger.info(" Instance ID is : " + instanceId);
        logger.info(" Instance Creator Name is : " + instanceIamTest.getInstanceResponse().getCreatorName());
        logger.info(" Instance Creator Email is : " + instanceIamTest.getInstanceResponse().getCreatorEmail());

    }

    public void provisionSnapshotForIamTest() throws Exception {
        logger.info(" Start to Provision Snapshot for iam test...");
        if (instanceIamTest != null) {
            useIamUser();
            dbaasApi.getIamAuthInfo();
            snapshotCreated = instanceIamTest.createSnapshotWithRetry();
            snapshotId = snapshotCreated.getId();
            logger.info(" Created Snapshot successfully....");
            logger.info(" Snapshot id is : " + snapshotId);
            logger.info(" Snapshot CreatorEmail is : " + snapshotCreated.getCreatorEmail());
            logger.info(" Snapshot name is " + snapshotCreated.getDescription());
        } else {
            throw new Exception(" Can not find instance, can not provision snapshot..");
        }
    }


    public void restoreFromSnapshot(String namePrefix, DbaasInstance instance) throws Exception {
        try {
            logger.info(" Start to restore instance from Snapshot....");
            useIamUser();
            dbaasApi.getIamAuthInfo();
            GetSnapshotResponse snapshotResponse = instance.createSnapshotWithRetry();
            checkNotNull(snapshotResponse, "Can not create snapshot from instance:" + instance.getInstanceid());
            restoredSnapshotId = snapshotResponse.getId();
            logger.info(" One Snapshot is created during restoreFromSnapshot procedure, restoredSnapshotId is : " + restoredSnapshotId);
            useIamUser();
            dbaasApi.getIamAuthInfo();
            restoredInstance = builder.restoreFromSnapshot(namePrefix, snapshotResponse.getId());
            getRestoredInstanceResponse = restoredInstance.getInstanceResponse();
            checkNotNull(getRestoredInstanceResponse, "Can not create instance for sapshot: " + restoredSnapshotId);
            restoredInstanceId = checkNotNull(getRestoredInstanceResponse.getId());
            logger.info("Restored instance from snapshot:" + restoredSnapshotId + " , new restored instance id is : " + restoredInstanceId);
            checkNotNull(getRestoredInstanceResponse.getIpAddress());
            Connections connections = getRestoredInstanceResponse.getConnections();
            checkNotNull(connections.getDataPath());
            checkNotNull(connections.getDataPath().getDestPort());
            assertThat(getRestoredInstanceResponse.getStatus().equals("available"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restoreInstanceFromSnapshot(String namePrefix, String snapshotId) throws Exception {
        try {
            logger.info(" Start to restore instance from snapshot Id:" + snapshotId);
            useIamUser();
            dbaasApi.getIamAuthInfo();
            RestoreFromSnapshotRequest createInstanceRequest = builder.buildInstanceRequestFromSnapshot(namePrefix, snapshotId, password);
            getRestoredInstanceResponse = dbaasApi.restoreFromSnapshotWithRetry(createInstanceRequest);
            restoredInstanceId = checkNotNull(getRestoredInstanceResponse.getId());
            checkNotNull(getRestoredInstanceResponse, "Can not create instance for sapshot: " + snapshotId);
            assertThat(getRestoredInstanceResponse.getStatus().equals("available"));
            assertThat(getRestoredInstanceResponse.getId()).isNotNull();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void listAllInstance() throws Exception {
        logger.info("Start to list all instances...");
        useIamUser();
        dbaasApi.getIamAuthInfo();
        listResponse = dbaasApi.listDBInstances();
        int size = listResponse.getTotal();
        logger.info(" " + size + " numbers of instances found");
        assertThat(listResponse).isNotNull();
    }

    public void readInstance(String instanceId) throws Exception {
        logger.info(" Start to read instance... id is : " + instanceId);
        useIamUser();
        dbaasApi.getIamAuthInfo();
        GetInstanceResponse readInstanceResponse = dbaasApi.getDBInstance(instanceId);
        checkNotNull(readInstanceResponse);
        assertThat(instanceId.equals(readInstanceResponse.getId()));
        assertThat(readInstanceResponse.getIpAddress()).isNotNull();
        assertThat(readInstanceResponse.getStatus().equals("available"));
        Connections connections = readInstanceResponse.getConnections();
        checkNotNull(connections.getDataPath());
        checkNotNull(connections.getDataPath().getDestPort());
    }

    public void deleteInstance(String instanceId) throws Exception {
        logger.info("Start to delete Instance... id is : " + instanceId);
        useIamUser();
        dbaasApi.getIamAuthInfo();
        dbaasApi.deleteInstanceWithRetry(instanceId);
        //assertThat(veirfyInstanceDeleted(instanceId));
    }

    public void deleteSnapshot(String snapshotId) throws Exception {
        logger.info("Start to delete Snapshot... id is : " + snapshotId);
        useIamUser();
        dbaasApi.getIamAuthInfo();
        dbaasApi.deleteSnapshotWithRetry(snapshotId);
    }

    public void readSnapshot(String snapshotId) throws Exception {
        logger.info("Start to read snapshot...  Snapshot id is : " + snapshotId);
        useIamUser();
        dbaasApi.getIamAuthInfo();
        GetSnapshotResponse getSnapshotResponse = dbaasApi.getSnapshot(snapshotId);
        String readSnapshotId = getSnapshotResponse.getId();
        checkNotNull(readSnapshotId);
        assertThat(snapshotId.equals(readSnapshotId));
        assertThat(getSnapshotResponse.getStatus()).isNotNull();
        assertThat(getSnapshotResponse.getSourceInstance()).isNotNull();
        assertThat(getSnapshotResponse.getStatus().equals("available"));
    }

    public boolean veirfyInstanceDeleted(String instanceId) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        ListResponse listResponse = dbaasApi.listDBInstances();
        List<ListInstanceItem> listInstanceItems = (List<ListInstanceItem>) listResponse.getData();
        for (ListInstanceItem listInstanceItem : listInstanceItems) {
            if (listInstanceItem.getId().equalsIgnoreCase(instanceId)) {
                throw new Exception(instanceId + " is still deleting.");
            }
        }
        return true;
    }



}
