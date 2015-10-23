package com.vmware.vchs.model.portal.iamRole;

import java.util.List;

/**
 * Created by fanz on 6/3/15.
 */
public class UpdateRoleResponseItem {


    private String userName;
    private List<String> assignRoles;
    private List<String> unassignRoles;
    private String assignResult;
    private String unassignResult;

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

    public String getAssignResult() {
        return assignResult;
    }

    public void setAssignResult(String assignResult) {
        this.assignResult = assignResult;
    }

    public String getUnassignResult() {
        return unassignResult;
    }

    public void setUnassignResult(String unassignResult) {
        this.unassignResult = unassignResult;
    }
}
