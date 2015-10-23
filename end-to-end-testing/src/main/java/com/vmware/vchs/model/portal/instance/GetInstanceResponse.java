package com.vmware.vchs.model.portal.instance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by georgeliu on 14/10/31.
 */
@JsonIgnoreProperties
public class GetInstanceResponse {

    private String id;
    private Plan plan;
    private String name;
    private String description;
    private String ipAddress;
    private String licenseType;
    private Connections connections;
    private String version;
    private String edition;
    private String masterUsername;
    private int diskSize;
    private int diskUsage;
    private int iops;
    private String status;
    private String creatorEmail;
    private String creatorName;
    private AvailableRestoreWindows availableRestoreWindows;
    private PitrSettings pitrSettings;
    private SnapshotSettings snapshotSettings;
    private String maintenanceTime;
    @JsonProperty("DREnabled")
    private boolean dREnabled;
    private String[] permissions;
    private String createdAt;
    private String updatedAt;
    private String failedReason;
    private String serviceGroupId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getMasterUsername() {
        return masterUsername;
    }

    public void setMasterUsername(String masterUsername) {
        this.masterUsername = masterUsername;
    }

    public int getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(int diskSize) {
        this.diskSize = diskSize;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public AvailableRestoreWindows getAvailableRestoreWindows() {
        return availableRestoreWindows;
    }

    public void setAvailableRestoreWindows(AvailableRestoreWindows availableRestoreWindows) {
        this.availableRestoreWindows = availableRestoreWindows;
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

    public boolean isdREnabled() {
        return dREnabled;
    }

    public void setdREnabled(boolean dREnabled) {
        this.dREnabled = dREnabled;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public Connections getConnections() {
        return connections;
    }

    public void setConnections(Connections connections) {
        this.connections = connections;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

    public String getServiceGroupId() {
        return serviceGroupId;
    }

    public void setServiceGroupId(String serviceGroupId) {
        this.serviceGroupId = serviceGroupId;
    }
}
