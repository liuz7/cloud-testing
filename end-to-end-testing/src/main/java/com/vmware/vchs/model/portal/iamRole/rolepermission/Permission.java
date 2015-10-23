package com.vmware.vchs.model.portal.iamRole.rolepermission;

/**
 * Created by fanz on 5/5/15.
 */
public enum Permission {


    CREATE_INSTANCE("CREATE_INSTANCE"),
    CREATE_INSTANCE_FROM_SNAPSHOT("CREATE_INSTANCE_FROM_SNAPSHOT"),
    CREATE_INSTANCE_AS_PITR("CREATE_INSTANCE_AS_PITR"),
    READ_INSTANCE("READ_INSTANCE"),
    UPDATE_INSTANCE("UPDATE_INSTANCE"),
    DELETE_INSTANCE("DELETE_INSTANCE"),
    CREATE_SNAPSHOT("CREATE_SNAPSHOT"),
    READ_SNAPSHOT("READ_SNAPSHOT"),
    DELETE_SNAPSHOT("DELETE_SNAPSHOT"),
    CREATE_SERVICE_PLAN("CREATE_SERVICE_PLAN"),
    READ_SERVICE_PLAN("READ_SERVICE_PLAN"),
    UPDATE_SERVICE_PLAN("UPDATE_SERVICE_PLAN"),
    DELETE_SERVICE_PLAN("DELETE_SERVICE_PLAN"),
    LIST_SERVICE_PLANS("LIST_SERVICE_PLANS"),
    READ_USAGE_INFO("READ_USAGE_INFO"),
    PROCESS_EVENTS_LOGS_METRICS("PROCESS_EVENTS_LOGS_METRICS"),
    UPDATE_ACCESS_CONTROL("UPDATE_ACCESS_CONTROL"),
    READ_ACCESS_CONTROL("READ_ACCESS_CONTROL");


    private String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String value() {
        return this.permission;
    }

    @Override
    public String toString() {
        return this.permission;
    }

}
