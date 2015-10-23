/*
 *
 *  * ******************************************************
 *  * Copyright VMware, Inc. 2014.   All Rights Reserved.
 *  * ******************************************************
 *
 */

package com.vmware.vchs.load.generator.result.path;

import com.vmware.vchs.link.Link;
import com.vmware.vchs.test.client.rest.PathBuilder;

/**
 * The Link builder to generate all required links.
 */
public class PerformanceLinkBuilder {

    public static String getTypeSearchPath(String time) {
        return PathBuilder.newPath().path(time).path(Path.SEARCH.value()).build();
    }

    public static String getInfluxDbPath() {
        return PathBuilder.newPath().path(Path.DB.value()).path(Path.DBAAS.value()).path(Path.SERIES.value()).build();
    }



}
