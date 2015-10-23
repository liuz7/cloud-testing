package com.vmware.vchs.model.portal.instance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Created by georgeliu on 15/4/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PitrSettings {

    private boolean enabled = true;
    private int retention;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getRetention() {
        return retention;
    }

    public void setRetention(int retention) {
        this.retention = retention;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.retention);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final PitrSettings other = (PitrSettings) obj;
        return Objects.equal(this.retention, other.retention);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("enabled", enabled)
                .add("retention", retention)
                .toString();

    }
}