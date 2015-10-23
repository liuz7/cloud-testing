package com.vmware.vchs.constant;

/**
 * Created by georgeliu on 15/4/11.
 */
public enum NodeStatus {

    FREE_IN_MAINTENANCE("FreeButInMaintenance"),
    PROVISIONED_IN_MAINTENANCE("ProvisionedButInMaintenance"),
    PROVISIONED("Provisioned"),
    FREE("Free");

    private String code;

    NodeStatus(String message) {
        this.code = message;
    }

    public String value() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
