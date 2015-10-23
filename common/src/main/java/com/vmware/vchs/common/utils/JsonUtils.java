/*
 * ****************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ****************************************************
 */

package com.vmware.vchs.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vchs.common.Serializer.JodaTimeModule;

public class JsonUtils {
    private static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.registerModule(new JodaTimeModule());
    }

    private JsonUtils() {}

    public static <T> T parseJsonObject(Object src, Class<T> type) {
        T t = null;
        try {
            t = mapper.convertValue(src, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> T parseJsonObject(String src, Class<T> type) {
        T t = null;
        try {
            t = mapper.readValue(src, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> T parseJsonObject(byte[] src, Class<T> type) {
        T t = null;
        try {
            t = mapper.readValue(src, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public static byte[] toByteArray(Object object) throws JsonProcessingException {
        return mapper.writeValueAsBytes(object);
    }
}
