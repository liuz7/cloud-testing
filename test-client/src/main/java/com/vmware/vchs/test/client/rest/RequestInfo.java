package com.vmware.vchs.test.client.rest;

/**
 * Created by georgeliu on 14/12/3.
 */
public class RequestInfo {

    private String method;
    private String path;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
