package com.vmware.vchs.model.portal.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by georgeliu on 15/4/1.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataResponse {

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
