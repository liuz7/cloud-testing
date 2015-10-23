package com.vmware.vchs.test.config;

/**
 * Created by georgeliu on 15/2/12.
 */
public class PraxisServerConnection {

    private String praxisConnectUrl;
    private String praxisOrg1;
    private String dbadminUsername;
    private String dbadminPassword;
    private String instanceOwner;
    private String instanceOwnerPassword;
    private String endUsername;
    private String endUserPasswd;
    private String iamUserName;
    private String iamUserPasswd;

    public String getPraxisConnectUrl() {
        return praxisConnectUrl;
    }

    public void setPraxisConnectUrl(String praxisConnectUrl) {
        this.praxisConnectUrl = praxisConnectUrl;
    }

    public String getPraxisOrg1() {
        return praxisOrg1;
    }

    public void setPraxisOrg1(String praxisOrg1) {
        this.praxisOrg1 = praxisOrg1;
    }

    public String getDbadminUsername() {
        return dbadminUsername;
    }

    public void setDbadminUsername(String dbadminUsername) {
        this.dbadminUsername = dbadminUsername;
    }

    public String getDbadminPassword() {
        return dbadminPassword;
    }

    public void setDbadminPassword(String dbadminPassword) {
        this.dbadminPassword = dbadminPassword;
    }

    public String getInstanceOwner() {
        return instanceOwner;
    }

    public void setInstanceOwner(String instanceOwner) {
        this.instanceOwner = instanceOwner;
    }

    public String getInstanceOwnerPassword() {
        return instanceOwnerPassword;
    }

    public void setInstanceOwnerPassword(String instanceOwnerPassword) {
        this.instanceOwnerPassword = instanceOwnerPassword;
    }

    public String getEndUsername() {
        return endUsername;
    }

    public void setEndUsername(String endUsername) {
        this.endUsername = endUsername;
    }

    public String getEndUserPasswd() {
        return endUserPasswd;
    }

    public void setEndUserPasswd(String endUserPasswd) {
        this.endUserPasswd = endUserPasswd;
    }

    public String getIamUserName() {
        return iamUserName;
    }

    public void setIamUserName(String iamUserName) {
        this.iamUserName = iamUserName;
    }

    public String getIamUserPasswd() {
        return iamUserPasswd;
    }

    public void setIamUserPasswd(String iamUserPasswd) {
        this.iamUserPasswd = iamUserPasswd;
    }
}
