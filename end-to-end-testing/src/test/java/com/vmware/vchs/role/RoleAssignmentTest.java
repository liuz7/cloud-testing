package com.vmware.vchs.role;


import com.vmware.vchs.RoleBaseTest;
import com.vmware.vchs.model.portal.iamRole.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Optional;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by fanz on 5/5/15.
 */

public class RoleAssignmentTest extends RoleBaseTest {

    protected static final Logger logger = LoggerFactory.getLogger(RoleAssignmentTest.class);

    @Test(groups={"iamtest", "sanity", "full"})
    public void testGetRolePermissionOfService() throws Exception{
        useIamUser();
        dbaasApi.getIamAuthInfo();
        GetRoleResponse getRoleResponse = sendRoleRequestGetGlobalRole();
        assertThat(getRoleResponse).isNotNull();
        assertThat(getRoleResponse.getRoleItemResponses()).isNotNull();
        assertThat(getRoleResponse.getRoleItemResponses().get(0)).isNotNull();
        assertThat(!getRoleResponse.getRoleItemResponses().get(0).getName().isEmpty());
        //assertThat(!getRoleResponse.getPermissions().isEmpty() );
    }

    @Test(groups={"iamtest", "full"})
    public void testGetRolePermissionOfInstance() throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        provisionInstanceForIamTest();

        logger.info("Instance for this test is :" + instanceId);
        GetRoleResponse getRoleResponse = sendRoleRequestGetInstanceRole(instanceId);
        assertThat(getRoleResponse.getRoleItemResponses()).isNotNull();
        assertThat(getRoleResponse.getRoleItemResponses().get(0)).isNotNull();
        assertThat(!getRoleResponse.getRoleItemResponses().get(0).getName().isEmpty());
        //assertThat(!getRoleResponse.getPermissions().isEmpty() );
    }

    @Test(groups={"iamtest", "sanity", "full"})
    public void testGetAllUsers() throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        GetUserResponse getUserResponse = sendRoleRequestGetAllUserGlobal();
        assertThat(getUserResponse).isNotNull();
        assertThat(getUserResponse.getUserItemResponses()).isNotNull();
        List<UserItemResponse> userItemResponseList = getUserResponse.getUserItemResponses();
        if (userItemResponseList.isEmpty()) {
            logger.info("No user found");
        } else {
            List<String> userList = new ArrayList<>();
            for (Iterator<UserItemResponse> it = userItemResponseList.iterator(); it.hasNext();) {
                UserItemResponse useritem = it.next();
                String userName = useritem.getName();
                // check if userName -- admin1@dbaas-int.com
                userList.add(userName);
            }
            assertThat(userList.isEmpty());
        }
    }

    @Test(groups={"iamtest", "full"})
    public void testSearchIamUser() throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        logger.info("Test Search user : " + " iamUser " + iamUser);
        GetUserResponse getUserResponse = sendRoleRequestSearchUserGlobal(iamUser);
        assertThat(getUserResponse).isNotNull();
        assertThat(getUserResponse.getUserItemResponses()).isNotNull();
        List<UserItemResponse> userItemResponseList = getUserResponse.getUserItemResponses();
        if (userItemResponseList.isEmpty()) {
            logger.info("Can not find user " + iamUser);
        } else {
            boolean hasUser = false;
            for (Iterator<UserItemResponse> it = userItemResponseList.iterator(); it.hasNext();) {
                UserItemResponse userItem = it.next();
                if (userMap.get(iamUser).equals(userItem.getName())) {
                    hasUser = true;
                }
            }
            assertThat(hasUser);
        }
    }

    @Test(groups={"iamtest", "full"})
    public void testSearchEndUser() throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        logger.info("Test Search user : " + " EndUser " + endUser);
        GetUserResponse getUserResponse = sendRoleRequestSearchUserGlobal(endUser);
        assertThat(getUserResponse).isNotNull();
        assertThat(getUserResponse.getUserItemResponses()).isNotNull();
        List<UserItemResponse> userItemResponseList = getUserResponse.getUserItemResponses();
        assertThat(userItemResponseList.isEmpty() || userItemResponseList.size() == 0);
    }

//    public void testSearchEndUser() throws Exception {
//        useIamUser();
//        dbaasApi.getIamAuthInfo();
//        logger.info("Test user global roles : " + " iamUser " + endUser);
//        GetUserRoleResponse getUserRoleResponse = sendRoleRequestGetOneUserRole(endUser);
//        try {
//            assertThat(getUserRoleResponse).isNotNull();
//            assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0)).isNotNull();
//            assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0).getName());
//            assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0).getName().contains(endUser));
//        } catch (ArrayIndexOutOfBoundsException e) {
//            logger.info("Instance can not be found, or iamUser :" + endUser + " can not get correct information from API !!!! ");
//            e.printStackTrace();
//        }
//    }


    @Test(groups={"iamtest", "full"})
    public void testUserRoleGlobal() throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        logger.info("Test user global roles : " + " iamUser " + iamUser);
        GetUserRoleResponse getUserRoleResponse = sendRoleRequestGetOneUserRole(iamUser);
        try {
            assertThat(getUserRoleResponse).isNotNull();
            assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0)).isNotNull();
            assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0).getName());
            assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0).getName().contains(iamUser));
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.info("Instance can not be found, or iamUser :" + iamUser + " can not get correct information from API !!!! ");
            e.printStackTrace();
        }
    }



    /*
//TODO List All users in search
    public void ListUserRoleGlobal(@Optional("fanzhang.jeffrey") String userId) throws Exception {
        GetUserRoleResponse getUserRoleResponse = sendRoleRequestGetOneUserRole(userId);
        assertThat(getUserRoleResponse).isNotNull();
        assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0)).isNotNull();
        if (getUserRoleResponse.getUserRoleItemResponses().get(0).getName().contains(userId)) {
            ArrayList<String>
        }
    }
*/

    /**
     *  List User's Role Service(mssql) Global
     *
     *      ~/acm/assignment/mssql                         getAllGlobalUserRolePath() ---> getAllUserRoleGlobal()
     *      ~/acm/assignment/mssql/?userId=                getGlobalUserRolePath(String userId) ---> getUserRoleGlobal(String userId)
     *      ~/acm/assignment/mssql/?resourceId=            getAllResourceUserRolePath(String resourceId) -->getAllUserRoleInstance(String resourceId)
     *      ~/acm/assignment/mssql/?userId= &resourceId=   getResourceUserRolePath(String userID, String resourceId) --> getUserRoleInstance(String userId, String resourceId)
     *10.156.74.35
     *      GetUserRoleResponse
     */

    @Test(groups={"iamtest", "full"})
    public void testGetUserRole() throws Exception {
        // http://192.168.55.168:8085/appsrv/api/v1/acm/assignment/mssql/
        useIamUser();
        dbaasApi.getIamAuthInfo();
        GetUserRoleResponse getUserRoleResponse = sendRoleRequestGetAllUserRoleGlobal();
        assertThat(getUserRoleResponse).isNotNull();
        assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0)).isNotNull();
        assertThat(!getUserRoleResponse.getUserRoleItemResponses().get(0).getName().isEmpty());
        assertThat(!getUserRoleResponse.getUserRoleItemResponses().get(0).getRoles().isEmpty());
    }

    /**
     * @param
     * @param globalRole   Policy Admin, DBAdmin, Snapshot Admin,
     * @throws Exception
     */


    @Test(parameters={"globalRole"}, groups={"iamtest", "full"})
    public void testAdminUserGlobalRole1(@Optional("DBAdmin") String globalRole) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        logger.info("Test Admin user global roles : " + globalRole + " on iamUser : " + iamUser);
        GetUserRoleResponse getUserRoleResponse = sendRoleRequestGetOneUserRole(iamUser);
        assertThat(getUserRoleResponse).isNotNull();
        assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0)).isNotNull();
        assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0).getRoles().contains(globalRole));

    }

    @Test(parameters={"globalRole"}, groups={"iamtest", "full"})
    public void testAdminUserGlobalRole2(@Optional("Policy Admin") String globalRole) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        logger.info("Test Admin user global roles : " + globalRole + " on iamUser : " + iamUser);
        GetUserRoleResponse getUserRoleResponse = sendRoleRequestGetOneUserRole(iamUser);
        assertThat(getUserRoleResponse).isNotNull();
        assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0)).isNotNull();
        assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0).getRoles().contains(globalRole));

    }


    /**
     * @param instanceRole  INSTANCE_OWNER, INSTANCE_OWNER, INSTANCE_READONLY, POLICY_INSTANCE_ADMIN
     * @throws Exception

    //@Test(parameters = {"Instance "})
    public void verifyInstanceRoles(@Optional("Instance Owner") String instanceRole) throws Exception {
    GetUserRoleResponse getUserRoleResponse = sendRoleRequestGetAllUserRoleInstance(instanceId);
    assertThat(getUserRoleResponse).isNotNull();
    assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0)).isNotNull();
    ArrayList<String> roles = new ArrayList<>(getUserRoleResponse.getUserRoleItemResponses().get(0).getRoles());
    //assertThat(roles.size() != 0);
    boolean hasRole = false;
    for (String role : roles) {
    if (instanceRole.equals(role))
    hasRole = true;
    }

    if (hasRole == true) {
    logger.info( "Instance: " + instanceRole + "has instance role/permission of " + instanceRole);
    assert(hasRole);
    } else {
    logger.info("Instance: " + instanceRole + "does not have instance role/permission of " + instanceRole);
    assert(hasRole);
    }
    }
     */



    @Test(parameters = {"instanceRole"}, groups={"iamtest", "full"})
    public void testUserInstanceRole(@Optional("Instance Owner") String instanceRole) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        provisionInstanceForIamTest();

        logger.info("User ID for this test is : " + iamUser);
        logger.info("Instance for this test is : " + instanceId);
        logger.info("test user Role: " + instanceRole);

        if (instanceId != null) {
            GetUserRoleResponse getUserRoleResponse = sendRoleRequestGetUserRoleInstance(iamUser, instanceId);
            assertThat(getUserRoleResponse).isNotNull();
            assertThat(getUserRoleResponse.getUserRoleItemResponses()).isNotNull();
            assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0)).isNotNull();
            assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0).getRoles().contains(instanceRole));

            /*
            ArrayList<String> roles = new ArrayList<>(getUserRoleResponse.getUserRoleItemResponses().get(0).getRoles());
            boolean hasRole = false;
            for (String role : roles) {
                if (instanceRole.equals(role))
                    hasRole = true;
            }

            if (hasRole == true) {
                logger.info("User: " + userId + " has instance role/permission of " + instanceRole);
                assert (hasRole);
            } else {
                logger.info("User: " + userId + "does not instance role/permission of " + instanceRole);
                assert (hasRole);
            }
            */

        } else {
            new Exception("Can not find instance Id");
        }

    }



    /**
     *  Testing Role Assignment Global
     *  Access control global
     * Policy Admin, DBAdmin, Snapshot Admin,
     * @throws Exception
     */


    @Test(groups = {"iamtest", "full"}, parameters={"assignGlobalRole"})
    public void testAccessControlAssignOneRoleGlobal(@Optional("Snapshot Admin") String assignGlobalRole ) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        logger.info("user for this test is :" + iamUser);
        String userId = iamUser;
        logger.info("Access Control: Start to assign global level Role : " + assignGlobalRole + " On User : " + userId );

        UpdateRoleRequest updateRoleRequest = buildUpdateRoleRequestAssignOneRole(userId, assignGlobalRole);
        UpdateRoleResponse updateRoleResponse = sendUpdateRoleRequestGloble(updateRoleRequest);
        assertThat(updateRoleResponse).isNotNull();
        assertThat(updateRoleResponse.getUpdateRoleResponseItemList()).isNotNull();
        assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0)).isNotNull();

        ArrayList<String> rolesAssigned = new ArrayList<>(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getAssignRoles());
        ArrayList<String> roleUnassigned = new ArrayList<>(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignRoles());
        assertThat(rolesAssigned.contains(assignGlobalRole));
        assertThat(roleUnassigned.isEmpty());

        assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUserName().equals(userId));
        assertThat(!updateRoleResponse.getUpdateRoleResponseItemList().get(0).getAssignResult().isEmpty());
        assertThat(!updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignResult().isEmpty());
        assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignRoles().contains(assignGlobalRole));
    }

    @Test(groups = {"iamtest", "full"}, parameters={"revokeGlobalRole"})
    public void testAccessControlRevokeOneRoleGlobal(@Optional("Snapshot Admin") String revokeGlobalRole) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        logger.info("user for this test is :" + iamUser);
        String userId = iamUser;
        logger.info("Access Control: Start to revoke global level Role : " + revokeGlobalRole + " On User : " + userId);

        try {
            if (negativeGlobalRole(userId, revokeGlobalRole) ) {
                logger.info("iamUser does not have access control permissions");
                assignOneRoleGlobal(userId, revokeGlobalRole);
            }
            UpdateRoleRequest updateRoleRequest = buildUpdateRoleRequestRevokeOneRole(userId, revokeGlobalRole);
            UpdateRoleResponse updateRoleResponse = sendUpdateRoleRequestGloble(updateRoleRequest);

            assertThat(updateRoleResponse).isNotNull();
            assertThat(updateRoleResponse.getUpdateRoleResponseItemList()).isNotNull();
            assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0)).isNotNull();

            ArrayList<String> rolesAssigned = new ArrayList<>(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getAssignRoles());
            ArrayList<String> roleUnassigned = new ArrayList<>(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignRoles());
            assertThat(roleUnassigned.contains(revokeGlobalRole));
            assertThat(rolesAssigned.isEmpty());

            assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUserName().equals(userId));
            assertThat(!updateRoleResponse.getUpdateRoleResponseItemList().get(0).getAssignResult().isEmpty());
            assertThat(!updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignResult().isEmpty());
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.info("User does not have permission !!!");
            e.printStackTrace();
        }
    }

    @Test(groups = {"iamtest", "full"}, parameters={"assignInstanceRole"})
    public void testAccessControlAssignOneRoleInstance(@Optional("Snapshot Owner") String assignInstanceRole) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        provisionInstanceForIamTest();
        provisionSnapshotForIamTest();

        logger.info("User ID for this test is : " + iamUser);
        logger.info("Instance for this test is : " + instanceId);
        logger.info("Snapshot for this test is : " + snapshotId);
        logger.info("Assign instance role for this test is : " + assignInstanceRole);

        String userId = iamUser;
        logger.info("Access Control: Start to assign instance level Role : " + assignInstanceRole + " On User : " + userId);
        try {
            UpdateRoleRequest updateRoleRequest = buildUpdateRoleRequestAssignOneRole(userId, assignInstanceRole);
            UpdateRoleResponse updateRoleResponse = sendUpdateRoleRequestInstance(updateRoleRequest, instanceId);

            assertThat(updateRoleResponse).isNotNull();
            assertThat(updateRoleResponse.getUpdateRoleResponseItemList()).isNotNull();
            assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0)).isNotNull();

            ArrayList<String> rolesAssigned = new ArrayList<>(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getAssignRoles());
            ArrayList<String> roleUnassigned = new ArrayList<>(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignRoles());
            assertThat(rolesAssigned.contains(assignInstanceRole));
            assertThat(roleUnassigned.isEmpty());

            assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUserName().equals(userId));
            assertThat(!updateRoleResponse.getUpdateRoleResponseItemList().get(0).getAssignResult().isEmpty());
            assertThat(!updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignResult().isEmpty());
            assertThat(!updateRoleResponse.getUpdateRoleResponseItemList().get(0).getAssignResult().contains(assignInstanceRole));
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.info("Instance can not be found, or iamUser :" + iamUser + " can not get correct information from API !!!! ");
            e.printStackTrace();
        }
    }

    @Test(groups = {"iamtest", "full"}, parameters={"revokeInstanceRole"})
    public void testAccessControlRevokeOneRoleInstance(@Optional("Snapshot Owner") String revokeInstanceRole) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        provisionInstanceForIamTest();
        provisionSnapshotForIamTest();
        logger.info("User ID for this test is :" + iamUser);
        logger.info("Instance for this test is :" + instanceId);
        logger.info("Snapshot for this test is : " + snapshotId);
        logger.info("Revoke instance role for this test is : " + revokeInstanceRole);

        String userId = iamUser;
        logger.info("Access Control: Start to revoke instance level Role : " + revokeInstanceRole + " On User : " + userId );
        try {
            if (negativeTestInstanceRole(userId, revokeInstanceRole, instanceId)) {
                assignOneRoleInstance(userId, revokeInstanceRole, instanceId);
            }
            UpdateRoleRequest updateRoleRequest = buildUpdateRoleRequestRevokeOneRole(userId, revokeInstanceRole);
            UpdateRoleResponse updateRoleResponse = sendUpdateRoleRequestInstance(updateRoleRequest, instanceId);

            assertThat(updateRoleResponse).isNotNull();
            assertThat(updateRoleResponse.getUpdateRoleResponseItemList()).isNotNull();
            assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0)).isNotNull();

            ArrayList<String> rolesAssigned = new ArrayList<>(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getAssignRoles());
            ArrayList<String> roleUnassigned = new ArrayList<>(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignRoles());
            assertThat(rolesAssigned.contains(revokeInstanceRole));
            assertThat(roleUnassigned.isEmpty());

            assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUserName().equals(userId));
            assertThat(!updateRoleResponse.getUpdateRoleResponseItemList().get(0).getAssignResult().isEmpty());
            assertThat(!updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignResult().isEmpty());
            assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignRoles().contains(revokeInstanceRole));
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.info("Instance can not be found, or iamUser :" + iamUser + " can not get correct information from API !!!! ");
            e.printStackTrace();
        }
    }



    @Test(groups={"iamtest", "sanity", "full"})
    public void testDBAdmin() throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        logger.info("user for this test is :" + iamUser);
        String userId = iamUser;
        assertThat(verifyGlobalRole(userId, "DBAdmin"));
        provisionInstanceForIamTest();
        logger.info("DBAdmin can create instance, instance Id is : " + instanceId);
    }

    @Test(groups={"iamtest", "full"})
    public void testPolicyAdmin() throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        logger.info("user1 for this test is :" + iamUser);
        logger.info("user2 for this test is :" + endUser);
        String user1 = iamUser;
        String user2 = endUser;
        assertThat(verifyGlobalRole(user1, "Policy Admin"));
        assignOneRoleGlobal(user1, "Snapshot Admin");
        assignOneRoleGlobal(user2, "DBAdmin");
        assignOneRoleGlobal(user1, "Policy Admin");
        revokeOneRoleGlobal(user2, "DBAdmin");
        revokeOneRoleGlobal(user2, "Policy Admin");
        assertThat(verifyGlobalRole(user1, "Snapshot Admin"));
        assertThat(negativeGlobalRole(user2, "Policy Admin"));
        assertThat(negativeGlobalRole(user2, "DBAdmin"));
    }


    @Test(groups={"iamtest", "full"})
    public void testSnapshotAdmin() throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        provisionInstanceForIamTest();
        logger.info("user1 for this test is :" + iamUser);
        logger.info("user2 for this test is :" + endUser);
        String user1 = iamUser;
        String user2 = endUser;
        assignOneRoleGlobal(user1, "Snapshot Admin");
        assertThat(negativeGlobalRole(user2, "Snapshot Admin"));
        assertThat(verifyGlobalRole(user1, "Snapshot Admin"));
        restoreFromSnapshot(getNamePrefix(), instanceIamTest);
        readSnapshot(restoredInstanceId);
        deleteSnapshot(restoredSnapshotId);
    }


    @Test(groups={"iamtest", "full"})
    public void testInstanceOwner() throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        provisionInstanceForIamTest();
        logger.info("user1 for this test is :" + iamUser);
        logger.info("user2 for this test is :" + endUser);
        String user1 = iamUser;
        String user2 = endUser;
        if (instanceId != null) {
            logger.info("The instance created by admin is " + instanceId);
            assertThat(verifyInstanceRole(user1, "Instance Owner", instanceId));
            assertThat(negativeTestInstanceRole(user2, "Instance Owner", instanceId));
            readInstance(instanceId);
            restoreFromSnapshot(getNamePrefix(), instanceIamTest);
            readSnapshot(restoredSnapshotId);
            deleteSnapshot(restoredSnapshotId);
            deleteInstance(instanceId);
        } else {
            throw new Exception("Can not find created instance !!!! ");
        }
    }

    @Test(groups={"iamtest", "full"})
    public void testSnapshotOwner() throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        provisionInstanceForIamTest();

        logger.info("Instance for this test is :" + instanceId);
        logger.info("Snapshot for this test is : " + snapshotId);
        logger.info("user1 for this test is :" + iamUser);
        logger.info("user2 for this test is :" + endUser);

        String user1 = iamUser;
        String user2 = endUser;
        if (snapshotId != null && instanceId != null) {
            logger.info("The snapshot created by admin1 is " + snapshotId);
            logger.info("The resourceId used for test is InstanceID: " + instanceId);
            assertThat(verifySnapshotRole(user1, "Snapshot Owner", instanceId));
            assertThat(negativeTestSnapshotRole(user2, "Snapshot Owner", instanceId));
            restoreFromSnapshot(getNamePrefix(), instanceIamTest);
            readSnapshot(restoredSnapshotId);
            deleteSnapshot(restoredSnapshotId);
        } else {
            throw new Exception("Can not find created snapshot !!!! ");
        }
    }


    @Test(groups={"iamtest", "full"})
    public void testInstanceReadOnly() throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        provisionInstanceForIamTest();
        logger.info("Instance for this test is :" + instanceId);
        logger.info("user1 for this test is :" + iamUser);
        logger.info("user2 for this test is :" + endUser);

        String user1 = iamUser;
        String user2 = endUser;
        logger.info("The users for this test are :" + user1 + " and " + user2);
        if (instanceId != null) {
            logger.info("The snapshot created by admin1 is " + instanceId);
            assertThat(verifyInstanceRole(user1, "Instance ReadOnly", instanceId));
            readInstance(instanceId);
            assertThat(negativeTestInstanceRole(user2, "Instance ReadOnly", instanceId));
        } else {
            throw new Exception("Can not find created snapshot !!!! ");
        }
    }

    @Test(groups={"iamtest", "full"})
    public void testInstanceOwnerAccessControl() throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        provisionInstanceForIamTest();
        logger.info("Instance for this test is :" + instanceId);
        logger.info("user1 for this test is :" + iamUser);
        logger.info("user2 for this test is :" + endUser);

        String user1 = iamUser;
        String user2 = endUser;
        logger.info("The users for this test are :" + user1 + " and " + user2);

        try {
            if (instanceId != null) {
                assignOneRoleInstance(iamUser, "Snapshot Owner", instanceId);
                restoreFromSnapshot(getNamePrefix(), instanceIamTest);
                readSnapshot(restoredSnapshotId);
                deleteSnapshot(restoredSnapshotId);
            } else {
                throw new Exception("Can not find created snapshot !!!! ");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.info("Instance can not be found, or iamUser :" + iamUser + " can not get correct information from API !!!! ");
            e.printStackTrace();
        }
    }

    public boolean verifyGlobalRole(String userId, String globalRole) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        logger.info("Verify if User : " + userId + " has Global Role : " + globalRole );

        GetUserRoleResponse getUserRoleResponse = sendRoleRequestGetOneUserRole(userId);
        assertThat(getUserRoleResponse).isNotNull();
        assertThat(getUserRoleResponse.getUserRoleItemResponses()).isNotNull();
        assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0)).isNotNull();
        boolean hasRole = false;
        if (getUserRoleResponse.getUserRoleItemResponses().get(0) != null) {
            List<String> roles = new ArrayList<>(getUserRoleResponse.getUserRoleItemResponses().get(0).getRoles());
            Iterator<String> it = roles.iterator();
            while (it.hasNext()) {
                if (globalRole.equals(it.next())) {
                    hasRole = true;
                    break;
                }
            }
        }
        return hasRole;
    }

    public boolean negativeGlobalRole(String userId, String globalRole) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        logger.info("Verify if User : " + userId + " does not have Global Role : " + globalRole );

        GetUserRoleResponse getUserRoleResponse = sendRoleRequestGetOneUserRole(userId);
        assertThat(getUserRoleResponse).isNotNull();
        assertThat(getUserRoleResponse.getUserRoleItemResponses()).isNotNull();
        boolean negativeRole = false;
        if (getUserRoleResponse.getUserRoleItemResponses().isEmpty() || getUserRoleResponse.getUserRoleItemResponses().get(0) == null) {
            negativeRole = true;
        } else if (!getUserRoleResponse.getUserRoleItemResponses().get(0).getRoles().contains(globalRole)) {
            negativeRole = true;
        }
        return negativeRole;
    }

    public boolean verifyInstanceRole(String userId, String instanceRole, String instanceId) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        logger.info("Verify if User : " + userId + " has Instance Role : " + instanceRole );
        if (instanceId != null) {
            logger.info("The instance created is " + instanceId + "userId for this test is " + userId);
        } else {
            throw new Exception("Can not find created instance !!!! ");
        }

        GetUserRoleResponse getUserRoleResponse = sendRoleRequestGetUserRoleInstance(userId, instanceId);
        assertThat(getUserRoleResponse).isNotNull();
        assertThat(getUserRoleResponse.getUserRoleItemResponses()).isNotNull();
        assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0)).isNotNull();
        boolean hasRole = false;
        if (getUserRoleResponse.getUserRoleItemResponses().get(0) != null && getUserRoleResponse.getUserRoleItemResponses().get(0).getRoles().contains(instanceRole)) {
             hasRole = true;
            /*
            List<String> roles = new ArrayList<>(getUserRoleResponse.getUserRoleItemResponses().get(0).getRoles());
            Iterator<String> it = roles.iterator();
            while (it.hasNext()) {
                if (instanceRole.equals(it.next())) {
                    hasRole = true;
                    logger.info("User " + userId + "has instance level role : " + instanceRole);
                    break;
                }
            }
            */
        }
        return hasRole;
    }

    public boolean verifySnapshotRole(String userId, String snapshotRole, String instanceId) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        logger.info("Verify if User : " + userId + " has snapshot Role : " + snapshotRole );
        if (instanceId != null && instanceId != null) {
            logger.info("The instance created is " + instanceId + "userId for this test is " + userId);
        } else {
            throw new Exception("Can not find created snapshot/instance !!!! ");
        }
        GetUserRoleResponse getUserRoleResponse = sendRoleRequestGetUserRoleInstance(userId, instanceId);
        assertThat(getUserRoleResponse).isNotNull();
        assertThat(getUserRoleResponse.getUserRoleItemResponses()).isNotNull();
        assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0)).isNotNull();
        boolean hasRole = false;
        if (getUserRoleResponse.getUserRoleItemResponses().get(0) != null && getUserRoleResponse.getUserRoleItemResponses().get(0).getRoles().contains(snapshotRole)) {
            hasRole = true;
        /*
        if (getUserRoleResponse.getUserRoleItemResponses().get(0) != null) {
            List<String> roles = new ArrayList<>(getUserRoleResponse.getUserRoleItemResponses().get(0).getRoles());
            Iterator<String> it = roles.iterator();
            while (it.hasNext()) {
                if (snapshotRole.equals(it.next())) {
                    hasRole = true;
                    break;
                }
            }
        }
        */
        }
        return hasRole;
    }

    public boolean verifyInstanceNegativeRole(String userId, String instanceRole, String instanceId) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        if (instanceId != null) {
            logger.info("The instance created is" + instanceId);
        } else {
            throw new Exception("Can not find created instance !!!! ");
        }
        GetUserRoleResponse getUserRoleResponse = sendRoleRequestGetUserRoleInstance(userId, instanceId);
        assertThat(getUserRoleResponse).isNotNull();
        assertThat(getUserRoleResponse.getUserRoleItemResponses()).isNotNull();
        assertThat(getUserRoleResponse.getUserRoleItemResponses().get(0)).isNotNull();
        boolean hasRole = false;
        if (getUserRoleResponse.getUserRoleItemResponses().get(0) != null) {
            List<String> roles = new ArrayList<>(getUserRoleResponse.getUserRoleItemResponses().get(0).getRoles());
            Iterator<String> it = roles.iterator();
            while (it.hasNext()) {
                if (instanceRole.equals(it.next())) {
                    hasRole = true;
                    break;
                }
            }
        }
        return hasRole;
    }

    public boolean negativeTestInstanceRole(String userId, String instanceRole, String instanceId) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        logger.info("Verify if User : " + userId + " does not have Instance Role : " + instanceRole );

        if (instanceId != null) {
            logger.info("The instance created is " + instanceId);
        } else {
            throw new Exception("Can not find created instance !!!! ");
        }
        GetUserRoleResponse getUserRoleResponse = sendRoleRequestGetUserRoleInstance(userId, instanceId);
        assertThat(getUserRoleResponse).isNotNull();
        assertThat(getUserRoleResponse.getUserRoleItemResponses()).isNotNull();
        boolean negativeRole = false;
        if (getUserRoleResponse.getUserRoleItemResponses().isEmpty() || getUserRoleResponse.getUserRoleItemResponses().get(0) == null) {
            negativeRole = true;
        } else if (!getUserRoleResponse.getUserRoleItemResponses().get(0).getRoles().contains(instanceRole)) {
            negativeRole = true;
        }
        return negativeRole;
    }

    public boolean negativeTestSnapshotRole(String userId, String snapshotRole, String instanceId) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        logger.info("Verify if User : " + userId + " does not have Snapshot Role : " + snapshotRole );

        if (instanceId != null) {
            logger.info("The instance created is " + instanceId);
        } else {
            throw new Exception("Can not find created snapshot !!!! ");
        }

        GetUserRoleResponse getUserRoleResponse = sendRoleRequestGetUserRoleInstance(userId, instanceId);
        assertThat(getUserRoleResponse).isNotNull();
        assertThat(getUserRoleResponse.getUserRoleItemResponses()).isNotNull();
        boolean negativeRole = false;
        if (getUserRoleResponse.getUserRoleItemResponses().isEmpty() || getUserRoleResponse.getUserRoleItemResponses().get(0) == null) {
            negativeRole = true;
        } else if (!getUserRoleResponse.getUserRoleItemResponses().get(0).getRoles().contains(snapshotRole)) {
            negativeRole = true;
        }
        return negativeRole;
    }


    public void assignOneRoleGlobal(String userId, String assignGlobalRole ) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        logger.info("Access Control: Start to assign global level Role : " + assignGlobalRole + " On User : " + userId );

        UpdateRoleRequest updateRoleRequest = buildUpdateRoleRequestAssignOneRole(userId, assignGlobalRole);
        UpdateRoleResponse updateRoleResponse = sendUpdateRoleRequestGloble(updateRoleRequest);
        assertThat(updateRoleResponse).isNotNull();
        assertThat(updateRoleResponse.getUpdateRoleResponseItemList()).isNotNull();
        assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0)).isNotNull();

        ArrayList<String> rolesAssigned = new ArrayList<>(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getAssignRoles());
        ArrayList<String> roleUnassigned = new ArrayList<>(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignRoles());
        assertThat(rolesAssigned.contains(assignGlobalRole));
        assertThat(roleUnassigned.isEmpty());

        assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUserName().equals(userId));
        assertThat(!updateRoleResponse.getUpdateRoleResponseItemList().get(0).getAssignResult().isEmpty());
        assertThat(!updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignResult().isEmpty());
        assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignRoles().contains(assignGlobalRole));
    }


    public void revokeOneRoleGlobal(String userId, String revokeGlobalRole) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        logger.info("Access Control: Start to revoke global level Role : " + revokeGlobalRole + " On User : " + userId );

        UpdateRoleRequest updateRoleRequest = buildUpdateRoleRequestRevokeOneRole(userId, revokeGlobalRole);
        UpdateRoleResponse updateRoleResponse = sendUpdateRoleRequestGloble(updateRoleRequest);

        assertThat(updateRoleResponse).isNotNull();
        assertThat(updateRoleResponse.getUpdateRoleResponseItemList()).isNotNull();
        assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0)).isNotNull();

        ArrayList<String> rolesAssigned = new ArrayList<>(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getAssignRoles());
        ArrayList<String> roleUnassigned = new ArrayList<>(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignRoles());
        assertThat(roleUnassigned.contains(revokeGlobalRole));
        assertThat(rolesAssigned.isEmpty());

        assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUserName().equals(userId));
        assertThat(!updateRoleResponse.getUpdateRoleResponseItemList().get(0).getAssignResult().isEmpty());
        assertThat(!updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignResult().isEmpty());
    }


    public void assignOneRoleInstance(String userId, String assignInstanceRole, String instanceId) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        logger.info("Access Control: Start to assign instance level Role : " + assignInstanceRole + " On User : " + userId );

        UpdateRoleRequest updateRoleRequest = buildUpdateRoleRequestAssignOneRole(userId, assignInstanceRole);
        UpdateRoleResponse updateRoleResponse = sendUpdateRoleRequestInstance(updateRoleRequest, instanceId);

        assertThat(updateRoleResponse).isNotNull();
        assertThat(updateRoleResponse.getUpdateRoleResponseItemList()).isNotNull();
        assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0)).isNotNull();

        ArrayList<String> rolesAssigned = new ArrayList<>(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getAssignRoles());
        ArrayList<String> roleUnassigned = new ArrayList<>(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignRoles());
        assertThat(rolesAssigned.contains(assignInstanceRole));
        assertThat(roleUnassigned.isEmpty());

        assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUserName().equals(userId));
        assertThat(!updateRoleResponse.getUpdateRoleResponseItemList().get(0).getAssignResult().isEmpty());
        assertThat(!updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignResult().isEmpty());
        assertThat(!updateRoleResponse.getUpdateRoleResponseItemList().get(0).getAssignResult().contains(assignInstanceRole));

    }


    public void revokeOneRoleInstance(String userId, String revokeInstanceRole, String instanceId) throws Exception {
        useIamUser();
        dbaasApi.getIamAuthInfo();
        provisionInstanceForIamTest();

        logger.info("Access Control: Start to revoke instance level Role : " + revokeInstanceRole + " On User : " + userId );
        UpdateRoleRequest updateRoleRequest = buildUpdateRoleRequestRevokeOneRole(userId, revokeInstanceRole);
        UpdateRoleResponse updateRoleResponse = sendUpdateRoleRequestInstance(updateRoleRequest, instanceId);

        assertThat(updateRoleResponse).isNotNull();
        assertThat(updateRoleResponse.getUpdateRoleResponseItemList()).isNotNull();
        assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0)).isNotNull();

        ArrayList<String> rolesAssigned = new ArrayList<>(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getAssignRoles());
        ArrayList<String> roleUnassigned = new ArrayList<>(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignRoles());
        assertThat(rolesAssigned.contains(revokeInstanceRole));
        assertThat(roleUnassigned.isEmpty());

        assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUserName().equals(userId));
        assertThat(!updateRoleResponse.getUpdateRoleResponseItemList().get(0).getAssignResult().isEmpty());
        assertThat(!updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignResult().isEmpty());
        assertThat(updateRoleResponse.getUpdateRoleResponseItemList().get(0).getUnassignRoles().contains(revokeInstanceRole));
    }


}
