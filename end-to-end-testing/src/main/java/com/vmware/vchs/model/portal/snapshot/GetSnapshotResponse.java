package com.vmware.vchs.model.portal.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vmware.vchs.model.portal.common.SourceInstance;

import java.util.List;

/**
 * Created by liuda on 15/4/11.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetSnapshotResponse {

    @JsonProperty("name")
    private String name;

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("id")
    private String id;

    @JsonProperty("description")
    private String description;

    @JsonProperty("type")
    private String type;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("updatedAt")
    private String updatedAt;

    @JsonProperty("status")
    private String status;

    @JsonProperty("sourceInstance")
    private SourceInstance sourceInstance;

    @JsonProperty("creatorEmail")
    private String creatorEmail;

    @JsonProperty("snapshotTime")
    private String snapshotTime;

    @JsonProperty("permissions")
    private List<String> permissions;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public SourceInstance getSourceInstance() {
        return sourceInstance;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public String getSnapshotTime() {
        return snapshotTime;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
