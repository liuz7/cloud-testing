/*
 *
 *  * ******************************************************
 *  * Copyright VMware, Inc. 2014.   All Rights Reserved.
 *  * ******************************************************
 *
 */

package com.vmware.vchs.test.client.rest;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The path builder to generate the path.
 */
public class PathBuilder {

    private static final String PATH_SEPARATOR = "/";
    private final Path path;

    public static PathBuilder newPath() {
        return new PathBuilder();
    }

    private PathBuilder() {
        path = new Path();
    }

    public String build() {
        return path.toString();
    }

    public PathBuilder path(String pathValue) {
        checkNotNull(pathValue);
        if (path.getPath() == null) {
            path.setPath(PATH_SEPARATOR + pathValue);
        } else {
            path.setPath(path.getPath() + PATH_SEPARATOR + pathValue);
        }
        return this;
    }

    public PathBuilder addSeparator() {
        if (path.getPath() != null ) {
            path.setPath(path.getPath() + PATH_SEPARATOR);
        } else {
            path.setPath(PATH_SEPARATOR);
        }
        return this;
    }

}
