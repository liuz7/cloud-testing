package com.vmware.vchs.model.portal.networks;

import java.util.List;

/**
 * Created by georgeliu on 15/5/28.
 */
public class UpdateDataPath {

    private List<String> addIPs;
    private List<String> revokeIPs;

    public List<String> getAddIPs() {
        return addIPs;
    }

    public void setAddIPs(List<String> addIPs) {
        this.addIPs = addIPs;
    }

    public List<String> getRevokeIPs() {
        return revokeIPs;
    }

    public void setRevokeIPs(List<String> revokeIPs) {
        this.revokeIPs = revokeIPs;
    }
}
