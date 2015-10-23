/*
 * ****************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ****************************************************
 */
package com.vmware.vchs.model.constant;

public enum ResourceState {
    available,
    creating,
    updating,
    deleting,
    deleted,
    failed,
    snapshotting,
    backingup,
    maintaining,
}
