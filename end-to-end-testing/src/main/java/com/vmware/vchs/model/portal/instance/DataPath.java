package com.vmware.vchs.model.portal.instance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by georgeliu on 15/5/11.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataPath {

    //private String displayName;
    private int destPort;
    private List<String> allowedIPs;

    /*public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }*/

    public int getDestPort() {
        return destPort;
    }

    public void setDestPort(int destPort) {
        this.destPort = destPort;
    }

    public List<String> getAllowedIPs() {
        return allowedIPs;
    }

    public void setAllowedIPs(List<String> allowedIPs) {
        this.allowedIPs = allowedIPs;
    }
}
