package com.vmware.vchs.common.utils.exception;

import com.google.common.base.MoreObjects;

/**
 * The Error object for mapping the error from SC.
 */
public class PortalError {

    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("Code", code)
                .add("Message", message)
                .toString();
    }
}
