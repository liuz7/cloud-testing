package com.vmware.vchs.constant;

/**
 * Created by georgeliu on 14/11/6.
 */
public enum StatusCode {

    UPDATING("updating"),
    FAILED("failed"),
    RUNNING("running"),
    AVAILABLE("available"),
    SNAPSHOTTING("snapshotting"),
    CREATING("creating"),
    DELETING("deleting"),
    DELETED("deleted"),
    PROVISIONED("provisioned"),
    UNDEFINED("undefined");

    private String code;

    StatusCode(String message) {
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
