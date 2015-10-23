/*
 *
 *  * ******************************************************
 *  * Copyright VMware, Inc. 2014.   All Rights Reserved.
 *  * ******************************************************
 *
 */

package com.vmware.vchs.link;

import com.vmware.vchs.test.client.rest.PathBuilder;

/**
 * The Link builder to generate all required links.
 */
public class LinkBuilder {

    public static String getInstancePath() {
        return PathBuilder.newPath().path(Link.SERVICE.value()).path(Link.VERSION.value()).path(Link.INSTANCE.value()).build();
    }

    public static String getInstanceIdPath(String instanceId) {
        return PathBuilder.newPath().path(Link.SERVICE.value()).path(Link.VERSION.value()).path(Link.INSTANCE.value()).path(instanceId).build();
    }

    public static String getSnapshotPath() {
        return PathBuilder.newPath().path(Link.SERVICE.value()).path(Link.VERSION.value()).path(Link.SNAPSHOT.value()).build();
    }

    public static String getSnapshotIdPath(String snapshotId) {
        return PathBuilder.newPath().path(Link.SERVICE.value()).path(Link.VERSION.value()).path(Link.SNAPSHOT.value()).path(snapshotId).build();
    }

    public static String getInstanceConnectionPath(String instanceId) {
        return PathBuilder.newPath().path(Link.SERVICE.value()).path(Link.VERSION.value()).path(Link.INSTANCE.value()).path(instanceId).path(Link.CONNECTION.value()).build();
    }

    public static String getInstanceDataPath(String instanceId) {
        return PathBuilder.newPath().path(Link.SERVICE.value()).path(Link.VERSION.value()).path(Link.INSTANCE.value()).path(instanceId).path(Link.DATA_PATH.value()).build();
    }

    public static String getVdcIPsPath() {
        return PathBuilder.newPath().path(Link.API.value()).path(Link.VERSION.value()).path(Link.VDC_IPS.value()).build();
    }

    public static String launchPitrPath(String instanceId) {
        return PathBuilder.newPath().path(Link.SERVICE.value()).path(Link.VERSION.value()).path(Link.INSTANCE.value()).path(instanceId).path(Link.ACTIONS.value()).path(Link.PITR.value()).build();
    }

    public static String restoreFromSnapshotPath(String instanceId) {
        return PathBuilder.newPath().path(Link.SERVICE.value()).path(Link.VERSION.value()).path(Link.INSTANCE.value()).path(Link.ACTIONS.value()).path(Link.RESTOREFROMSNAPSHOT.value()).build();
    }

    public static String getServicesPath() {
        return PathBuilder.newPath().path(Link.API.value()).path(Link.VERSION.value()).path(Link.SERVICES.value()).path(Link.SERVICE.value()).build();
    }


}
