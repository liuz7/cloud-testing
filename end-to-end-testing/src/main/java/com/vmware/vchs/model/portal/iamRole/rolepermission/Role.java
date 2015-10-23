package com.vmware.vchs.model.portal.iamRole.rolepermission;

/**
 * Created by fanz on 5/5/15.
 */
public enum  Role {


    POLICY_ADMIN("POLICY_ADMIN"),
    DBADMIN("DBADMIN"),
    SNAPSHOT_ADMIN("SNAPSHOT_ADMIN"),

    INSTANCE_OWNER("INSTANCE_OWNER"),
    SNAPSHOT_OWNER("SNAPSHOT_OWNER"),
    POLICY_INSTANCE_ADMIN("POLICY_INSTANCE_ADMIN"),

    READONLY("READONLY"),
    INSTANCE_READONLY("INSTANCE_READONLY");


    private String role;

    Role(String role) { this.role = role;}

    public String value() {
        return this.role;
    }

    @Override
    public String toString() {
        return this.role;
    }

}
