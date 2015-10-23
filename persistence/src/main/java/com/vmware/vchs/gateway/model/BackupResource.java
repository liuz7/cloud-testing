/*
 * *****************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * *****************************************************
 */

package com.vmware.vchs.gateway.model;

import com.google.common.base.MoreObjects;
import com.vmware.vchs.gateway.model.converter.DateToDateTimeConverter;
import org.hibernate.annotations.DynamicUpdate;
import org.joda.time.DateTime;

import javax.persistence.*;

@Entity
@Table(name = "backups")
@DynamicUpdate
public class BackupResource extends BaseModel implements Resource {
    private String instanceId;
    private String backupType;
    private String name;
    private String dbId;
    private long firstLsn;
    private long lastLsn;
    private DateTime backupStartTime;
    private DateTime timeStamp;
    private String resourceUri;
    private int referenceCount;
    private long size;
    private InstanceResource instance;
    private SnapshotResource snapshot;


    @Column(name = "instance_guid")
    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    @Column(name = "backup_type")
    public String getBackupType() {
        return backupType;
    }

    public void setBackupType(String backupType) {
        this.backupType = backupType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "db_id")
    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    @Column(name = "first_lsn")
    public long getFirstLsn() {
        return firstLsn;
    }

    public void setFirstLsn(long firstLsn) {
        this.firstLsn = firstLsn;
    }

    @Column(name = "last_lsn")
    public long getLastLsn() {
        return lastLsn;
    }

    public void setLastLsn(long lastLsn) {
        this.lastLsn = lastLsn;
    }

    @Convert(converter = DateToDateTimeConverter.class)
    @Column(name = "start_time")
    public DateTime getBackupStartTime() {
        return backupStartTime;
    }

    public void setBackupStartTime(DateTime backupStartTime) {
        this.backupStartTime = backupStartTime;
    }

    @Convert(converter = DateToDateTimeConverter.class)
    @Column(name = "time_stamp")
    public DateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(DateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Column(name = "resource_uri", columnDefinition = "TEXT")
    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    @Column(name = "reference_count")
    public int getReferenceCount() {
        return referenceCount;
    }

    public void setReferenceCount(int referenceCount) {
        this.referenceCount = referenceCount;
    }

    public void addReferenceCount() {
        if (referenceCount > 0) {
            referenceCount++;
        }
    }

    public void decreaseReferenceCount() {
        if (referenceCount > 0) {
            referenceCount--;
        }
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "instance_backups",
            joinColumns = @JoinColumn(name = "backup_id"),
            inverseJoinColumns = @JoinColumn(name = "instance_id")
    )
    public InstanceResource getInstance() {
        return instance;
    }

    public void setInstance(InstanceResource instance) {
        this.instance = instance;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(
            name = "snapshot_backups",
            joinColumns = @JoinColumn(name = "backup_id"),
            inverseJoinColumns = @JoinColumn(name = "snapshot_id", unique = true)
    )
    public SnapshotResource getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(SnapshotResource snapshot) {
        this.snapshot = snapshot;
    }


    @Override
    public boolean equals(Object object) {
        if (object == this)
            return true;
        if ((object == null) || !(object instanceof BackupResource))
            return false;

        final BackupResource a = (BackupResource) object;

        return id == a.getId() && guid.equals(a.getGuid());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("id:", this.guid)
                .add("status:", this.status)
                .add("created at:", this.createTime)
                .add("updated at:", this.modifyTime)
                .add("instance belongs to:", this.instanceId)
                .add("backup type:", this.backupType)
                .add("db name:", this.dbId)
                .add("lsn start:", this.firstLsn)
                .add("lsn end:", this.lastLsn)
                .add("backup start time:", this.backupStartTime)
                .toString();
    }
}
