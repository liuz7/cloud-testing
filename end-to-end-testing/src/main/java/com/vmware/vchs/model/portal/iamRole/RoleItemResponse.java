package com.vmware.vchs.model.portal.iamRole;

import java.util.List;

/**
 * Created by fanz on 6/1/15.
 */
public class RoleItemResponse {

    private String name;
    private String description;
    private List<Permission> permissions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
