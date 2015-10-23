package com.vmware.vchs.model.portal.networks;

import java.util.List;

/**
 * Created by georgeliu on 15/5/28.
 */
public class VDC {

    private String vdcName;
    private String description;
    private List<String> ips;

    public String getVdcName() {
        return vdcName;
    }

    public void setVdcName(String vdcName) {
        this.vdcName = vdcName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getIps() {
        return ips;
    }

    public void setIps(List<String> ips) {
        this.ips = ips;
    }
}
