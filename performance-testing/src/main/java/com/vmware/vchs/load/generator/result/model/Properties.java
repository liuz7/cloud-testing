package com.vmware.vchs.load.generator.result.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by liuda on 6/3/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Properties {
    @JsonProperty("Operation")
    private String Operation;
    @JsonProperty("Recordtype")
    private String Recordtype;
    @JsonProperty("Subcomponent")
    private String Subcomponent;

    public String getOperation() {
        return Operation;
    }

    public void setOperation(String operation) {
        Operation = operation;
    }

    public String getRecordtype() {
        return Recordtype;
    }

    public void setRecordtype(String recordtype) {
        Recordtype = recordtype;
    }

    public String getSubcomponent() {
        return Subcomponent;
    }

    public void setSubcomponent(String subcomponent) {
        Subcomponent = subcomponent;
    }
}
