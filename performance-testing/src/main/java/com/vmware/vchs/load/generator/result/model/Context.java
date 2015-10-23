package com.vmware.vchs.load.generator.result.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by liuda on 6/3/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Context {
    private Benchmark benchmark;
    private Properties properties;
    private String requestId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Benchmark getBenchmark() {
        return benchmark;
    }

    public void setBenchmark(Benchmark benchmark) {
        this.benchmark = benchmark;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
