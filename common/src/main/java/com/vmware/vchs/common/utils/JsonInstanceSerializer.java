/*
 *
 *  * ******************************************************
 *  * Copyright VMware, Inc. 2014.   All Rights Reserved.
 *  * ******************************************************
 *
 */

package com.vmware.vchs.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by liuzhiwen on 14-7-4.
 */
public class JsonInstanceSerializer<T> {

    private final ObjectMapper mapper;
    private Class<T> payloadClass = null;

    public JsonInstanceSerializer(Class<T> payloadClass) {
        mapper = new ObjectMapper();
        this.payloadClass = payloadClass;
    }

    public JsonInstanceSerializer() {
        mapper = new ObjectMapper();
    }

    public T deserialize(String jsonString) throws IOException {
        T instance = mapper.readValue(jsonString, payloadClass);
        return instance;
    }

    public T deserialize(String jsonString, Class<T> type) throws IOException {
        T instance = mapper.readValue(jsonString, type);
        return instance;
    }

    public String serialize(T instance) throws JsonProcessingException {
        String jsonString = mapper.writeValueAsString(instance);
        return jsonString;
    }

    public T deserialize(Map jsonMap) throws IOException {
        String jsonString = mapper.writeValueAsString(jsonMap);
        T instance = deserialize(jsonString);
        return instance;
    }

    public ObjectMapper getObjectMapper() {
        return this.mapper;
    }
}
