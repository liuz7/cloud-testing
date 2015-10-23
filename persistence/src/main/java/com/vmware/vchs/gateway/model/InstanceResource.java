/*
 * *****************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * *****************************************************
 */

package com.vmware.vchs.gateway.model;

import com.google.common.collect.Lists;

import com.vmware.vchs.gateway.model.annotation.Updatable;
import com.vmware.vchs.gateway.model.converter.BooleanToStringConverter;
import com.vmware.vchs.gateway.model.converter.DateToDateTimeConverter;
import org.hibernate.annotations.DynamicUpdate;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.List;

import static com.vmware.vchs.gateway.model.annotation.UpdateType.LOCAL;
import static com.vmware.vchs.gateway.model.annotation.UpdateType.REMOTE;

@Entity
@Table(name = "instances")
@DynamicUpdate
public class InstanceResource extends BaseModel implements Resource, Cloneable {
    private boolean dREnabled;
    @Updatable(LOCAL) private String name;
    @Updatable(LOCAL) private String description;
    private String edition;
    private String vdckey;
    private String creatorEmail;
    private String nodeId;
    private String publicIP;
    private int port;
    private String privateIP;
    private String version;
    private String masterUsername;
    @Updatable(REMOTE) private String masterPassword;
    private String licenseModel;
    @Updatable(REMOTE) private String maintenanceTime;
    private String planId;
    @Updatable(REMOTE) private int vcpu;
    @Updatable(REMOTE) private int memory;
    @Updatable(REMOTE) private int diskSize;
    private int diskUsage;
    @Updatable(REMOTE) private int iops;
    @Updatable(REMOTE) private boolean snapshotEnabled;
    @Updatable(REMOTE) private String snapshotPreferredStartTime;
    @Updatable(REMOTE) private int snapshotCycle;
    @Updatable(LOCAL) private int snapshotLimit;
    @Updatable(LOCAL) private String snapshotNamePrefix;
    @Updatable(REMOTE) private boolean backupEnabled;
    @Updatable(LOCAL) private int backupRetentionWindow;
    @Updatable(REMOTE) private String debugSnapshotCycle;
    @Updatable(REMOTE) private String debugBackupCycle;
    @Updatable(REMOTE) private String debugBackupStrategy;
    @Updatable(REMOTE) private String debugBackupRetention;
    private DateTime restoreWindowStartTime;
    private DateTime restoreWindowEndTime;
    private List<BackupResource> backups = Lists.newArrayList();

    @Column(name = "node_id")
    public String getNodeId() {
        return nodeId;
    }
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    @Column(name = "dr_enabled", columnDefinition = "char(1)")
    @Convert(converter = BooleanToStringConverter.class)
    public boolean isdREnabled() {
        return dREnabled;
    }
    public void setdREnabled(boolean dREnabled) {
        this.dREnabled = dREnabled;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getEdition() {
        return edition;
    }
    public void setEdition(String edition) {
        this.edition = edition;
    }

    @Column(name = "vdc_key")
    public String getVdckey() {
        return vdckey;
    }
    public void setVdckey(String vdckey) {
        this.vdckey = vdckey;
    }

    @Column(name = "creator_email")
    public String getCreatorEmail() {
        return creatorEmail;
    }
    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    @Column(name = "private_ip")
    public String getPrivateIP() {
        return privateIP;
    }
    public void setPrivateIP(String privateIP) {
        this.privateIP = privateIP;
    }

    @Column(name = "db_engine_version")
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    @Column(name = "master_username", updatable = false)
    public String getMasterUsername() {
        return masterUsername;
    }
    public void setMasterUsername(String masterUsername) {
        this.masterUsername = masterUsername;
    }

    @Transient
    public String getMasterPassword() {
        return masterPassword;
    }
    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    @Column(name = "license_model")
    public String getLicenseModel() {
        return licenseModel;
    }
    public void setLicenseModel(String licenseModel) {
        this.licenseModel = licenseModel;
    }

    @Column(name = "maintenance_time")
    public String getMaintenanceTime() {
        return maintenanceTime;
    }
    public void setMaintenanceTime(String maintenanceTime) {
        this.maintenanceTime = maintenanceTime;
    }

    public int getVcpu() {
        return vcpu;
    }
    public void setVcpu(int vcpu) {
        this.vcpu = vcpu;
    }

    public int getMemory() {
        return memory;
    }
    public void setMemory(int memory) {
        this.memory = memory;
    }

    @Column(name = "disk_size")
    public int getDiskSize() {
        return diskSize;
    }
    public void setDiskSize(int diskSize) {
        this.diskSize = diskSize;
    }

    @Column(name = "disk_usage")
    public int getDiskUsage() {
        return diskUsage;
    }
    public void setDiskUsage(int diskUsage) {
        this.diskUsage = diskUsage;
    }

    public int getIops() {
        return iops;
    }
    public void setIops(int iops) {
        this.iops = iops;
    }

    @Column(name = "plan_id")
    public String getPlanId() {
        return planId;
    }
    public void setPlanId(String planId) {
        this.planId = planId;
    }

    @Column(name="snapshot_enabled", columnDefinition="char(1)")
    @Convert(converter = BooleanToStringConverter.class)
    public boolean isSnapshotEnabled() {
        return snapshotEnabled;
    }
    public void setSnapshotEnabled(boolean snapshotEnabled) {
        this.snapshotEnabled = snapshotEnabled;
    }

    @Column(name="snapshot_preferred_start_time")
    public String getSnapshotPreferredStartTime() {
        return snapshotPreferredStartTime;
    }
    public void setSnapshotPreferredStartTime(String snapshotPreferredStartTime) {
        this.snapshotPreferredStartTime = snapshotPreferredStartTime;
    }

    @Column(name="snapshot_cycle")
    public int getSnapshotCycle() {
        return snapshotCycle;
    }
    public void setSnapshotCycle(int snapshotCycle) {
        this.snapshotCycle = snapshotCycle;
    }

    @Column(name="snapshot_limit")
    public int getSnapshotLimit() {
        return snapshotLimit;
    }
    public void setSnapshotLimit(int snapshotLimit) {
        this.snapshotLimit = snapshotLimit;
    }

    @Column(name="snapshot_name_prefix")
    public String getSnapshotNamePrefix() {
        return snapshotNamePrefix;
    }
    public void setSnapshotNamePrefix(String snapshotNamePrefix) {
        this.snapshotNamePrefix = snapshotNamePrefix;
    }

    @Column(name="backup_enabled", columnDefinition="char(1)")
    @Convert(converter = BooleanToStringConverter.class)
    public boolean isBackupEnabled() {
        return backupEnabled;
    }
    public void setBackupEnabled(boolean backupEnabled) {
        this.backupEnabled = backupEnabled;
    }

    @Column(name="backup_retention_window")
    public int getBackupRetentionWindow() {
        return backupRetentionWindow;
    }
    public void setBackupRetentionWindow(int backupRetentionWindow) {
        this.backupRetentionWindow = backupRetentionWindow;
    }

    @Column(name = "public_ip")
    public String getPublicIP() {
        return publicIP;
    }
    public void setPublicIP(String publicIP) {
        this.publicIP = publicIP;
    }

    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }

    @Column(name = "debug_snapshot_cycle")
    public String getDebugSnapshotCycle() {
        return debugSnapshotCycle;
    }
    public void setDebugSnapshotCycle(String debugSnapshotCycle) {
        this.debugSnapshotCycle = debugSnapshotCycle;
    }

    @Column(name = "debug_backup_cycle")
    public String getDebugBackupCycle() {
        return debugBackupCycle;
    }
    public void setDebugBackupCycle(String debugBackupCycle) {
        this.debugBackupCycle = debugBackupCycle;
    }

    @Column(name = "debug_backup_strategy")
    public String getDebugBackupStrategy() {
        return debugBackupStrategy;
    }
    public void setDebugBackupStrategy(String debugBackupStrategy) {
        this.debugBackupStrategy = debugBackupStrategy;
    }

    @Column(name = "debug_backup_retention")
    public String getDebugBackupRetention() {
        return debugBackupRetention;
    }
    public void setDebugBackupRetention(String debugBackupRetention) {
        this.debugBackupRetention = debugBackupRetention;
    }

    @Column(name="restore_window_start_time")
    @Convert(converter = DateToDateTimeConverter.class)
    public DateTime getRestoreWindowStartTime() {
        return restoreWindowStartTime;
    }
    public void setRestoreWindowStartTime(DateTime restoreWindowStartTime) {
        this.restoreWindowStartTime = restoreWindowStartTime;
    }

    @Column(name="restore_window_end_time")
    @Convert(converter = DateToDateTimeConverter.class)
    public DateTime getRestoreWindowEndTime() {
        return restoreWindowEndTime;
    }
    public void setRestoreWindowEndTime(DateTime restoreWindowEndTime) {
        this.restoreWindowEndTime = restoreWindowEndTime;
    }

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="instance_backups",
            joinColumns = @JoinColumn( name="instance_id"),
            inverseJoinColumns = @JoinColumn( name="backup_id", unique=true)
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
        if ((object == null) || !(object instanceof InstanceResource))
            return false;

        final InstanceResource a = (InstanceResource) object;

        return id == a.getId() && guid.equals(a.getGuid());
    }

    @Override
    public InstanceResource clone() {
        try {
            return (InstanceResource) super.clone();
        } catch (Exception e) {
            return null;
        }
    }
}