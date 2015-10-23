package com.vmware.vchs.model.portal.iamRole;

import java.util.List;

/**
 * Created by fanz on 5/7/15.

 *
 {
 "name": "enduser1@dbaas-int.com",
 "firstName": "",
 "lastName": "",
 "email": "enduser1@dbaas-int.com",
 "roles": [
    "READONLY"
 ]
 }
 *
 */


public class GetUserRoleResponse {

    private List<UserRoleItemResponse> userRoleItemResponses;

    public List<UserRoleItemResponse> getUserRoleItemResponses() {
        return userRoleItemResponses;
    }

    public void setUserRoleItemResponses(List<UserRoleItemResponse> userRoleItemResponses) {
        this.userRoleItemResponses = userRoleItemResponses;
    }
}


