package com.vmware.vchs.model.portal.instance;

/**
 * Created by georgeliu on 14/10/31.
 */
public class RestoreFromSnapshotRequest extends BaseRequest{

    private Connections connections;
    private Plan plan;
    private SnapshotSettings snapshotSettings;
    private PitrSettings pitrSettings;
    private String snapshotId;


    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public SnapshotSettings getSnapshotSettings() {
        return snapshotSettings;
    }

    public void setSnapshotSettings(SnapshotSettings snapshotSettings) {
        this.snapshotSettings = snapshotSettings;
    }

    public PitrSettings getPitrSettings() {
        return pitrSettings;
    }

    public void setPitrSettings(PitrSettings pitrSettings) {
        this.pitrSettings = pitrSettings;
    }

    public String getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(String snapshotId) {
        this.snapshotId = snapshotId;
    }

    public Connections getConnections() {
        return connections;
    }

    public void setConnections(Connections connections) {
        this.connections = connections;
    }
}
