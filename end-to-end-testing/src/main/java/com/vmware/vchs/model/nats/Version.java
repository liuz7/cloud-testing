package com.vmware.vchs.model.nats;

import com.google.common.base.Objects;

import java.util.Arrays;

/**
 * Created by georgeliu on 15/4/9.
 */
public class Version {

    private String sqlVersion;
    private String osVersion;
    private String[] appliedUpdates;

    public String getSqlVersion() {
        return sqlVersion;
    }

    public void setSqlVersion(String sqlVersion) {
        this.sqlVersion = sqlVersion;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String[] getAppliedUpdates() {
        return appliedUpdates;
    }

    public void setAppliedUpdates(String[] appliedUpdates) {
        this.appliedUpdates = appliedUpdates;
    }

    @Override
    public String toString() {
        return "Version{" +
                "sqlVersion='" + sqlVersion + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", appliedUpdates=" + Arrays.toString(appliedUpdates) +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.sqlVersion, this.osVersion, this.appliedUpdates);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Version other = (Version) obj;
        return Objects.equal(this.sqlVersion, other.sqlVersion)
                && Objects.equal(this.osVersion, other.osVersion)
                && Objects.equal(this.appliedUpdates, other.appliedUpdates);
    }
}
