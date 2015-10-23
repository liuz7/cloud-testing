/*
 *
 *  * ******************************************************
 *  * Copyright VMware, Inc. 2014.   All Rights Reserved.
 *  * ******************************************************
 *
 */

package com.vmware.vchs.load.generator.result.path;

/**
 * The Link enum to contains all link values.
 */
public enum Path {


    SEARCH("_search"),
    SERIES("series"),
    DB("db"),
    DBAAS("dbaas");

    private String link;

    Path(String link) {
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
