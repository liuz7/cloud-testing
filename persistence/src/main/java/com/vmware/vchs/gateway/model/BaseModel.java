/*
 * ******************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ******************************************************
 */

package com.vmware.vchs.gateway.model;

import com.google.common.base.MoreObjects;

import com.vmware.vchs.gateway.model.converter.DateToDateTimeConverter;
import org.joda.time.DateTime;

import javax.persistence.*;

@MappedSuperclass
public abstract class BaseModel implements Resource {
    protected int id;
    protected String guid;
    protected DateTime createTime;
    protected DateTime modifyTime;
    protected Resource.State status;

    @PrePersist
    public void prePersist() {
        DateTime now = DateTime.now();
        this.createTime = now;
        this.modifyTime = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.modifyTime = DateTime.now();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }
    private void setId(int id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }
    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Column(name = "created_at")
    @Convert(converter = DateToDateTimeConverter.class)
    public DateTime getCreateTime() {
        return createTime;
    }
    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    @Column(name = "modified_at")
    @Convert(converter = DateToDateTimeConverter.class)
    public DateTime getModifyTime() {
        return modifyTime;
    }
    public void setModifyTime(DateTime modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Enumerated(EnumType.STRING)
    public Resource.State getStatus() {
        return status;
    }
    public void setStatus(Resource.State status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("guid:", this.guid)
                .add("status:", this.status.name())
                .add("createTime:", this.createTime)
                .add("modifyTime:", this.modifyTime)
                .toString();
    }
}
