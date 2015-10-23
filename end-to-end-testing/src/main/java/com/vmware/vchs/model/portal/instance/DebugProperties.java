package com.vmware.vchs.model.portal.instance;

import com.google.common.base.Objects;

/**
 * Created by liuda on 15/4/28.
 */
public class DebugProperties {
    private String snapshotCycle;
    private String backupCycle;
    private String backupStrategy;
    private String backupRetention;

    public String getBackupRetention() {
        return backupRetention;
    }

    public void setBackupRetention(String backupRetention) {
        this.backupRetention = backupRetention;
    }

    public String getSnapshotCycle() {
        return snapshotCycle;
    }

    public void setSnapshotCycle(String snapshotCycle) {
        this.snapshotCycle = snapshotCycle;
    }

    public String getBackupCycle() {
        return backupCycle;
    }

    public void setBackupCycle(String backupCycle) {
        this.backupCycle = backupCycle;
    }

    public String getBackupStrategy() {
        return backupStrategy;
    }

    public void setBackupStrategy(String backupStrategy) {
        this.backupStrategy = backupStrategy;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.snapshotCycle, this.backupCycle, this.backupStrategy, this.backupRetention);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final DebugProperties other = (DebugProperties) obj;
        return Objects.equal(this.snapshotCycle, other.snapshotCycle)
                && Objects.equal(this.backupCycle, other.backupCycle)
                && Objects.equal(this.backupStrategy, other.backupStrategy)
                && Objects.equal(this.backupRetention, other.backupRetention);
    }
}
