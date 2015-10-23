package com.vmware.vchs.model.constant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.MoreObjects;
import com.vmware.vchs.model.portal.instance.Plan;

import java.util.List;

/**
 * Created by liuda on 15/5/12.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentModel {
    public Meters getMeters() {
        return meters;
    }

    public void setMeters(Meters meters) {
        this.meters = meters;
    }

    private Meters meters;

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(Meter meter:meters){
            sb.append(meter.toString());
        }
        return sb.toString();
    }
}
