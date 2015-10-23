package com.vmware.vchs.tmclient.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vchs.test.client.model.Request;

public abstract class AbstractMessageProcessor implements MessageProcessor {
    protected final ObjectMapper objectMapper;

    public AbstractMessageProcessor() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Request handleRequest(Request request) {
        return request;
    }

}
