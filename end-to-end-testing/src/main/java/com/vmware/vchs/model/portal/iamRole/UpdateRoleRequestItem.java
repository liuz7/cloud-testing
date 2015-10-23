package com.vmware.vchs.model.portal.iamRole;

import java.util.List;

/**
 * Created by fanz on 6/3/15.
 */
public class UpdateRoleRequestItem {


    private String userName;
    private List<String> assignRoles;
    private List<String> unassignRoles;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getAssignRoles() {
        return assignRoles;
    }

    public void setAssignRoles(List<String> assignRoles) {
        this.assignRoles = assignRoles;
    }

    public List<String> getUnassignRoles() {
        return unassignRoles;
    }

    public void setUnassignRoles(List<String> unassignRoles) {
        this.unassignRoles = unassignRoles;
    }
}
