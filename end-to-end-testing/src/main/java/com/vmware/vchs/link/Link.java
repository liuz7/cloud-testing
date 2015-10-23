/*
 *
 *  * ******************************************************
 *  * Copyright VMware, Inc. 2014.   All Rights Reserved.
 *  * ******************************************************
 *
 */

package com.vmware.vchs.link;

/**
 * The Link enum to contains all link values.
 */
public enum Link {

    SERVICE("mssql"),
    VERSION("v1"),
    INSTANCE("instances"),
    SNAPSHOT("snapshots"),
    PITR("pitr"),
    RESTOREFROMSNAPSHOT("restore-from-snapshot"),
    SERVICES("services"),
    API("api"),
    ACTIONS("actions"),
    CONNECTION("connection"),
    DATA_PATH("dataPath"),
    VDC_IPS("vdcIPs");


    private String link;

    Link(String link) {
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
