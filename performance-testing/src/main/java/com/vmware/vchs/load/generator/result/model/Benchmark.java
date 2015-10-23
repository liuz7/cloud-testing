package com.vmware.vchs.load.generator.result.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vmware.vchs.model.portal.common.ISO8601TimeDeserializer;
import com.vmware.vchs.model.portal.common.ISO8601TimeSerializer;
import org.joda.time.DateTime;

/**
 * Created by liuda on 6/3/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Benchmark {

    @JsonSerialize(using = ISO8601TimeSerializer.class)
    @JsonDeserialize(using = ISO8601TimeDeserializer.class)
    private DateTime dispatchedAt;

    @JsonSerialize(using = ISO8601TimeSerializer.class)
    @JsonDeserialize(using = ISO8601TimeDeserializer.class)
    private DateTime receivedAt;
    private int requestResponseInMillis;


    public DateTime getDispatchedAt() {
        return dispatchedAt;
    }

    public void setDispatchedAt(DateTime dispatchedAt) {
        this.dispatchedAt = dispatchedAt;
    }

    public DateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(DateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public int getRequestResponseInMillis() {
        return requestResponseInMillis;
    }

    public void setRequestResponseInMillis(int requestResponseInMillis) {
        this.requestResponseInMillis = requestResponseInMillis;
    }
}
