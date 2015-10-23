package com.vmware.vchs.test.client.rest.metrics;

import org.apache.http.HttpRequest;

public interface HttpClientMetricNameStrategy {
    String getNameFor(String name, HttpRequest request);
}
