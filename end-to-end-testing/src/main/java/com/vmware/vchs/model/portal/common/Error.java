package com.vmware.vchs.model.portal.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by georgeliu on 14/10/31.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Error {

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
