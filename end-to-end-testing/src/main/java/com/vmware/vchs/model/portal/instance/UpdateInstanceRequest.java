package com.vmware.vchs.model.portal.instance;

import com.fasterxml.jackson.annotation.JsonFilter;

/**
 * Created by georgeliu on 14/10/31.
 */
@JsonFilter("updateFilter")
public class UpdateInstanceRequest {

    private String masterPassword;
    private int diskSize;
    private PitrSettings pitrSettings;
    private SnapshotSettings snapshotSettings;
    private String maintenanceTime;
    private String masterUsername;
    private DebugProperties debugProperties;

    public String getMasterPassword() {
        return masterPassword;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public int getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(int diskSize) {
        this.diskSize = diskSize;
    }

    public PitrSettings getPitrSettings() {
        return pitrSettings;
    }

    public void setPitrSettings(PitrSettings pitrSettings) {
        this.pitrSettings = pitrSettings;
    }

    public SnapshotSettings getSnapshotSettings() {
        return snapshotSettings;
    }

    public void setSnapshotSettings(SnapshotSettings snapshotSettings) {
        this.snapshotSettings = snapshotSettings;
    }

    public String getMaintenanceTime() {
        return maintenanceTime;
    }

    public void setMaintenanceTime(String maintenanceTime) {
        this.maintenanceTime = maintenanceTime;
    }

    public String getMasterUsername() {
        return masterUsername;
    }

    public void setMasterUsername(String masterUsername) {
        this.masterUsername = masterUsername;
    }

    public DebugProperties getDebugProperties() {
        return debugProperties;
    }

    public void setDebugProperties(DebugProperties debugProperties) {
        this.debugProperties = debugProperties;
    }
}
