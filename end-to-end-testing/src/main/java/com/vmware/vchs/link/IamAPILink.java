package com.vmware.vchs.link;

/**
 * Created by fanz on 5/4/15.
 */
public enum IamAPILink {


    /**
     *  Base_URL : 10.156.74.35:8085/appsrv/api/v1/
     *
     *  Base_URL    <API_HEAD Ext IP>:<PORT>/<APPSRV>/<API>/<VERSION>/
     *
     *
     *  GET   <API_HEAD Ext IP>:<PORT>/<APPSRV>/<API>/<VERSION>/<>/<SERVICE>/<>
     */


    /**
     *  BASE_URL
     */
    APPSRV("appsrv"),
    API("api"),
    VERSION("v1"),

    INSTANCE("instances"),
    SNAPSHOT("snapshots"),
    NETWORK("networks"),
    PITR("pitr"),
    RESTOREFROMSNAPSHOT("restore-from-snapshot"),
    SERVICES("services"),
    ACTIONS("actions"),

    /**
     *  Roles and Assignment
     */
    ACM("acm"),
    ROLES("roles"),
    ASSIGNMENT("assignment"),


    /**
     *  :Service
     */
    SERVICE("mssql"),

    USERS("users"),
    USERID("userId"),
    RESOURCEID("resourceId");


    private String link;

    IamAPILink(String link) {
        this.link = link;
    }

    public String value() {
        return this.link;
    }

    @Override
    public String toString() {
        return this.link;
    }


}
