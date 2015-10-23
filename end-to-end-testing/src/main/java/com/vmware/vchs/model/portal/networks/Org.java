package com.vmware.vchs.model.portal.networks;

import java.util.List;

/**
 * Created by georgeliu on 15/5/28.
 */
public class Org {

    private String orgName;
    private List<VDC> vdcs;

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public List<VDC> getVdcs() {
        return vdcs;
    }

    public void setVdcs(List<VDC> vdcs) {
        this.vdcs = vdcs;
    }
}
