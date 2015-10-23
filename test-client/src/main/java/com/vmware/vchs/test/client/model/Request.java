package com.vmware.vchs.test.client.model;

import java.util.Map;

public class Request {
    public Map<String, String> getHeaders() {
        return headers;
    }

    public Object getBody() {
        return body;
    }

    private final Map<String, String> headers;
    private final Object body;

    public Request(Map<String, String> headers, Object body) {
        this.headers = headers;
        this.body = body;
    }
}