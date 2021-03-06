package com.vmware.vchs.test.config;

import com.google.common.base.MoreObjects;

/**
 * The Jira configuration POJO.
 */
public class CdsServer {

    private String baseUrl;

    private String user;

    private String password;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("CdsServerUrl", baseUrl)
                .toString();
    }
}
