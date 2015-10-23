package com.vmware.vchs.model.portal.networks;

import java.util.List;

/**
 * Created by georgeliu on 15/5/28.
 */
public class Region {

    private String regionName;
    private List<Org> orgs;

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public List<Org> getOrgs() {
        return orgs;
    }

    public void setOrgs(List<Org> orgs) {
        this.orgs = orgs;
    }
}
