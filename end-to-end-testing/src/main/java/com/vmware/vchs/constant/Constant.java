package com.vmware.vchs.constant;

/**
 * The Constant enum to contains all Constant values.
 */
public enum Constant {

    MANUAL("manual"),
    AUTO("auto");

    private String value;

    Constant(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }

}
