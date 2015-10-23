package com.vmware.vchs.test.client.model.etcd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.MoreObjects;

import java.util.Arrays;

/**
 * Created by georgeliu on 15/4/8.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MSSqlNode {

    private String id;
    private String capacity;
    private String host;
    private String vDCKey;
    private String category;
    private String[] instances;
    private String state;
    private String heartBeatTime;
    private String[] tags;
    private String[] commingEvents;
    private String[] recentApis;
    private String startupTime;
    private String releaseVer;
    private SystemInfo systemInfo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartupTime() {
        return startupTime;
    }

    public void setStartupTime(String startupTime) {
        this.startupTime = startupTime;
    }

    public String getReleaseVer() {
        return releaseVer;
    }

    public void setReleaseVer(String releaseVer) {
        this.releaseVer = releaseVer;
    }

    public String[] getRecentApis() {
        return recentApis;
    }

    public void setRecentApis(String[] recentApis) {
        this.recentApis = recentApis;
    }

    public String[] getCommingEvents() {

        return commingEvents;
    }

    public void setCommingEvents(String[] commingEvents) {
        this.commingEvents = commingEvents;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getvDCKey() {
        return vDCKey;
    }

    public void setvDCKey(String vDCKey) {
        this.vDCKey = vDCKey;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String[] getInstances() {
        return instances;
    }

    public void setInstances(String[] instances) {
        this.instances = instances;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getHeartBeatTime() {
        return heartBeatTime;
    }

    public void setHeartBeatTime(String heartBeatTime) {
        this.heartBeatTime = heartBeatTime;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public SystemInfo getSystemInfo() {
        return systemInfo;
    }

    public void setSystemInfo(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper helper = MoreObjects.toStringHelper(this);
        return helper
                .add("capacity", capacity)
                .add("tags", Arrays.toString(tags))
                        .toString();
    }
}
