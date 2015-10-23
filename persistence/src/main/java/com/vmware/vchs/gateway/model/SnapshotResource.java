/*
 * *****************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * *****************************************************
 */

package com.vmware.vchs.gateway.model;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.vmware.vchs.gateway.model.converter.DateToDateTimeConverter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Proxy;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.List;

@Entity
@Proxy(lazy=false)
@Table(name = "snapshots")
//@DynamicUpdate
public class SnapshotResource extends BaseModel implements Resource {
    private String instanceId;
    private String isCompact;

    @Column(name = "instance_id")
    public String getInstanceId() {
        return instanceId;
    }
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    @Column(name = "is_compact")
    public String getIsCompact() {
        return isCompact;
    }
    public void setIsCompact(String isCompact) {
        this.isCompact = isCompact;
    }

    private String name;
    private String snapshotType;
    private String snapshotTime;
    private String diskSize;
    private String dbPort;
    private String masterUsername;
    private String licenseModel;
    private String description;
    private String creatorEmail;
    private String instanceName;
    private String instanceDescription;
    private String maintenanceTime;
    private String instanceVersion;
    private String instanceEdition;
    private String failedReason;
    private String optLock;
    private List<BackupResource> backups = Lists.newArrayList();
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Column(name = "snapshot_type")
    public String getSnapshotType() {
        return snapshotType;
    }

    public void setSnapshotType(String snapshotType) {
        this.snapshotType = snapshotType;
    }
    @Column(name = "snapshot_time")
    public String getSnapshotTime() {
        return snapshotTime;
    }

    public void setSnapshotTime(String snapshotTime) {
        this.snapshotTime = snapshotTime;
    }
    @Column(name = "disk_size")
    public String getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(String diskSize) {
        this.diskSize = diskSize;
    }
    @Column(name = "db_port")
    public String getDbPort() {
        return dbPort;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }
    @Column(name = "master_username")
    public String getMasterUsername() {
        return masterUsername;
    }

    public void setMasterUsername(String masterUsername) {
        this.masterUsername = masterUsername;
    }
    @Column(name = "license_model")
    public String getLicenseModel() {
        return licenseModel;
    }

    public void setLicenseModel(String licenseModel) {
        this.licenseModel = licenseModel;
    }
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    @Column(name = "creator_email")
    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }
    @Column(name = "instance_name")
    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }
    @Column(name = "instance_description")
    public String getInstanceDescription() {
        return instanceDescription;
    }

    public void setInstanceDescription(String instanceDescription) {
        this.instanceDescription = instanceDescription;
    }
    @Column(name = "maintenance_time")
    public String getMaintenanceTime() {
        return maintenanceTime;
    }

    public void setMaintenanceTime(String maintenanceTime) {
        this.maintenanceTime = maintenanceTime;
    }
    @Column(name = "instance_version")
    public String getInstanceVersion() {
        return instanceVersion;
    }

    public void setInstanceVersion(String instanceVersion) {
        this.instanceVersion = instanceVersion;
    }
    @Column(name = "instance_edition")
    public String getInstanceEdition() {
        return instanceEdition;
    }

    public void setInstanceEdition(String instanceEdition) {
        this.instanceEdition = instanceEdition;
    }
    @Column(name = "failed_reason")
    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }
    @Column(name = "opt_lock")
    public String getOptLock() {
        return optLock;
    }

    public void setOptLock(String optLock) {
        this.optLock = optLock;
    }

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="snapshot_backups",
            joinColumns = @JoinColumn( name="snapshot_id"),
            inverseJoinColumns = @JoinColumn( name="backup_id")
    )
    public List<BackupResource> getBackups() {
            return backups;
        }
    public void setBackups(List<BackupResource> backups) {
        this.backups = backups;
    }



    @Override
    public boolean equals(Object object) {
        if (object == this)
            return true;
        if ((object == null) || !(object instanceof SnapshotResource))
            return false;

        final SnapshotResource a = (SnapshotResource) object;

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
                .add("instanceId:", this.instanceId)
                .add("isCompact:", this.isCompact)
                .toString();
    }
}
