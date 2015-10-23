package com.vmware.vchs.test.config;

/**
 * Created by georgeliu on 15/10/12.
 */
public class Vdc {

    private String orgUrl;
    private String orgName;
    private String adminVdcName;
    private String password;

    public String getOrgUrl() {
        return orgUrl;
    }

    public void setOrgUrl(String orgUrl) {
        this.orgUrl = orgUrl;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getAdminVdcName() {
        return adminVdcName;
    }

    public void setAdminVdcName(String adminVdcName) {
        this.adminVdcName = adminVdcName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
