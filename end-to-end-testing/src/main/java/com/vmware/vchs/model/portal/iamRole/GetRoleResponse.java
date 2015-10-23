package com.vmware.vchs.model.portal.iamRole;


import java.util.List;

/**
 * Created by fanz on 5/5/15.
 */
public class GetRoleResponse {

    private List<RoleItemResponse> roleItemResponses;

    public List<RoleItemResponse> getRoleItemResponses() {
        return roleItemResponses;
    }

    public void setRoleItemResponses(List<RoleItemResponse> roleItemResponses) {
        this.roleItemResponses = roleItemResponses;
    }
}
