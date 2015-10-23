package com.vmware.vchs.model.portal.instance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Objects;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Created by georgeliu on 15/4/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SnapshotSettings {

    private boolean enabled;
    private String preferredStartTime;
    private int cycle;
    private int limit;
    private String namePrefix;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPreferredStartTime() {
        return preferredStartTime;
    }

    public void setPreferredStartTime(String preferredStartTime) {
        this.preferredStartTime = preferredStartTime;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getNamePrefix() {
        return namePrefix;
    }

    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }

    public void setPreferredStartTime(int hour, int minute) {
        LocalDateTime currentDateTime = LocalDateTime.now(Clock.systemUTC());
        currentDateTime.plusHours(hour);
        currentDateTime = currentDateTime.plusMinutes(minute);
        int hours = currentDateTime.getHour();
        String hoursString = hours < 10 ? "0" + hours : String.valueOf(hours);
        int minutes = currentDateTime.getMinute();
        String minutesString = minutes < 10 ? "0" + minutes : String.valueOf(minutes);
        String preferredStartTime = hoursString + ":" + minutesString;
        setPreferredStartTime(preferredStartTime);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.enabled, this.preferredStartTime, this.cycle, this.limit, this.namePrefix);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final SnapshotSettings other = (SnapshotSettings) obj;
        return Objects.equal(this.enabled, other.enabled)
                && Objects.equal(this.preferredStartTime, other.preferredStartTime)
                && Objects.equal(this.cycle, other.cycle)
                && Objects.equal(this.limit, other.limit)
                && Objects.equal(this.namePrefix, other.namePrefix);
    }
}
