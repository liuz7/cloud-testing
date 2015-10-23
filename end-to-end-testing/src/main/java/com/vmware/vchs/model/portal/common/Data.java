package com.vmware.vchs.model.portal.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Objects;
import org.joda.time.DateTime;

/**
 * Created by liuda on 15/4/11.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {

    @JsonProperty("type")
    private final String type = null;

    public String getType() {
        return type;
    }

    public SourceInstance getSourceInstance() {
        return sourceInstance;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

    @JsonProperty("sourceInstance")
    private SourceInstance sourceInstance;

    @JsonProperty("updatedAt")
    @JsonSerialize(using = ISO8601TimeSerializer.class)
    @JsonDeserialize(using = ISO8601TimeDeserializer.class)
    private DateTime updatedAt;


    @JsonProperty("status")
    private String status;

    @JsonProperty("createdAt")
    @JsonSerialize(using = ISO8601TimeSerializer.class)
    @JsonDeserialize(using = ISO8601TimeDeserializer.class)
    private DateTime createdAt;


    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("creatorEmail")
    private String creatorEmail;

    @JsonProperty("failedReason")
    private String failedReason;

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Data other = (Data) obj;
        return Objects.equal(this.id, other.id);
    }

}
