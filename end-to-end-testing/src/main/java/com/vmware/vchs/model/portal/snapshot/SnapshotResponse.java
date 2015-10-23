/*
 * ******************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ******************************************************
 */

package com.vmware.vchs.model.portal.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vmware.vchs.model.portal.common.ISO8601TimeDeserializer;
import com.vmware.vchs.model.portal.common.ISO8601TimeSerializer;
import org.joda.time.DateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SnapshotResponse {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("snapshotType")
    private String snapshotType;

    @JsonProperty("instanceId")
    private String instanceId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("snapshotTime")
    @JsonSerialize(using = ISO8601TimeSerializer.class)
    @JsonDeserialize(using = ISO8601TimeDeserializer.class)
    private DateTime snapshotTime;

    @JsonProperty("diskSize")
    private int diskSize;

    @JsonProperty("dbPort")
    private int dbPort;

    @JsonProperty("dbEngineVersion")
    private String dbEngineVersion;

    @JsonProperty("masterUsername")
    private String masterUsername;

    @JsonProperty("ownerId")
    private String ownerId;

    @JsonProperty("description")
    private String description;

    @JsonProperty("licenseModel")
    private String licenseModel;

    @JsonProperty("instanceCreatedAt")
    @JsonSerialize(using = ISO8601TimeSerializer.class)
    @JsonDeserialize(using = ISO8601TimeDeserializer.class)
    private DateTime instanceCreatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSnapshotType() {
        return snapshotType;
    }

    public void setSnapshotType(String snapshotType) {
        this.snapshotType = snapshotType;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DateTime getSnapshotTime() {
        return snapshotTime;
    }

    public void setSnapshotTime(DateTime snapshotTime) {
        this.snapshotTime = snapshotTime;
    }

    public int getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(int diskSize) {
        this.diskSize = diskSize;
    }

    public int getDbPort() {
        return dbPort;
    }

    public void setDbPort(int dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbEngineVersion() {
        return dbEngineVersion;
    }

    public void setDbEngineVersion(String dbEngineVersion) {
        this.dbEngineVersion = dbEngineVersion;
    }

    public String getMasterUsername() {
        return masterUsername;
    }

    public void setMasterUsername(String masterUsername) {
        this.masterUsername = masterUsername;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLicenseModel() {
        return licenseModel;
    }

    public void setLicenseModel(String licenseModel) {
        this.licenseModel = licenseModel;
    }

    public DateTime getInstanceCreatedAt() {
        return instanceCreatedAt;
    }

    public void setInstanceCreatedAt(DateTime instanceCreatedAt) {
        this.instanceCreatedAt = instanceCreatedAt;
    }
}
