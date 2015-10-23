package com.vmware.vchs.model.portal.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by georgeliu on 15/4/1.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsyncResponse {

    private String id;
    private String status;
    private String startTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
