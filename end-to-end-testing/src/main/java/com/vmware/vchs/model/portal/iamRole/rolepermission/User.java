package com.vmware.vchs.model.portal.iamRole.rolepermission;

import java.util.List;

/**
 * Created by fanz on 5/14/15.
 */

public class User extends UserBase {
    private List<Role> roles;

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
