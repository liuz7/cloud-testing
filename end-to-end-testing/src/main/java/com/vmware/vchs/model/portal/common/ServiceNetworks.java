package com.vmware.vchs.model.portal.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by georgeliu on 14/10/31.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceNetworks {

    private List<String> allowedIPs;

    public List<String> getAllowedIPs() {
        return allowedIPs;
    }

    public void setAllowedIPs(List<String> allowedIPs) {
        this.allowedIPs = allowedIPs;
    }
}
