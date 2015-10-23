package com.vmware.vchs.model.portal.instance;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by georgeliu on 14/10/31.
 */
public class CreateInstanceRequest extends BaseRequest {

    private Plan plan;
    private Connections connections;
    private String version;
    private String edition;
    private String masterUsername;
    private PitrSettings pitrSettings;
    private SnapshotSettings snapshotSettings;
    private String maintenanceTime;

    @Override
    public String getMaintenanceTime() {
        return maintenanceTime;
    }

    @Override
    public void setMaintenanceTime(String maintenanceTime) {
        this.maintenanceTime = maintenanceTime;
    }

    /*@JsonProperty("DREnabled")
        private boolean dREnabled;*/
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


    /*public boolean isdREnabled() {
        return dREnabled;
    }

    public void setdREnabled(boolean dREnabled) {
        this.dREnabled = dREnabled;
    }*/


    public Connections getConnections() {
        return connections;
    }

    public void setConnections(Connections connections) {
        this.connections = connections;
    }
}
