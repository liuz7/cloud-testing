package com.vmware.vchs.load.generator.result.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by liuda on 6/3/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubHits {
    private String _type;

    public Source get_source() {
        return _source;
    }

    public void set_source(Source _source) {
        this._source = _source;
    }

    private Source _source;

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }
}
