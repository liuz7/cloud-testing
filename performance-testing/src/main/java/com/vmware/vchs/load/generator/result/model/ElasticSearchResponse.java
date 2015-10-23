package com.vmware.vchs.load.generator.result.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vmware.vchs.load.generator.result.model.Hits;
import com.vmware.vchs.model.portal.networks.Data;

/**
 * Created by georgeliu on 15/5/28.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ElasticSearchResponse {

    private int took;
    private boolean timed_out;
    private Hits hits;

    public int getTook() {
        return took;
    }

    public void setTook(int took) {
        this.took = took;
    }

    public boolean isTimed_out() {
        return timed_out;
    }

    public void setTimed_out(boolean timed_out) {
        this.timed_out = timed_out;
    }

    public Hits getHits() {
        return hits;
    }

    public void setHits(Hits hits) {
        this.hits = hits;
    }
}
