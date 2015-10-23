package com.vmware.vchs.tmclient.processor;


import com.vmware.vchs.test.client.model.Request;

public interface MessageProcessor {
    public Request handleRequest(Request request);

}
