/*
 * ****************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ****************************************************
 */

package com.vmware.vchs.gateway.model;

import org.joda.time.DateTime;

public interface Resource {
    public String getGuid();

    public State getStatus();

    public DateTime getCreateTime();

    public void setCreateTime(DateTime createTime);

    public DateTime getModifyTime();

    public void setModifyTime(DateTime modifytime);

    public enum State {
        available,
        creating,
        updating,
        deleting,
        deleted,
        failed,
        snapshotting,
        backingup,
        maintaining
    }
}
