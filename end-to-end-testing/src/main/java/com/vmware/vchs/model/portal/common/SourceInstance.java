package com.vmware.vchs.model.portal.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vmware.vchs.model.portal.instance.Plan;

/**
 * Created by liuda on 15/4/11.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SourceInstance {

    private int diskSize;

    private String id;

    private Plan plan;

    private String status;

    private String ipAddress;

    private String name;

    private String version;

    private String edition;

    private String description;

    private String masterUsername;

    public int getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(int diskSize) {
        this.diskSize = diskSize;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMasterUsername() {
        return masterUsername;
    }

    public void setMasterUsername(String masterUsername) {
        this.masterUsername = masterUsername;
    }
}
