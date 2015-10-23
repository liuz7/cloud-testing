package com.vmware.vchs.model.portal.instance;

/**
 * Created by georgeliu on 14/10/31.
 */
public class CreatePitrRequest extends BaseRequest{

    private String restoreTime;
    private Plan plan;
    private String name;
    private String description;
    private String serviceGroupId;
    private String licenseType;
    private Connections connections;
    private String masterPassword;
    private int diskSize;
    private PitrSettings pitrSettings;
    private SnapshotSettings snapshotSettings;
    private String maintenanceTime;
    private DebugProperties debugProperties;

    public DebugProperties getDebugProperties() {
        return debugProperties;
    }

    public void setDebugProperties(DebugProperties debugProperties) {
        this.debugProperties = debugProperties;
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

    public String getRestoreTime() {
        return restoreTime;
    }

    public void setRestoreTime(String restoreTime) {
        this.restoreTime = restoreTime;
    }

    public String getServiceGroupId() {
        return serviceGroupId;
    }

    public void setServiceGroupId(String serviceGroupId) {
        this.serviceGroupId = serviceGroupId;
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
}
