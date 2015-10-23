package com.vmware.vchs.load.generator.result.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by liuda on 6/3/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Source {
    private String message;
    private String applicationName;
    private Context context;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
