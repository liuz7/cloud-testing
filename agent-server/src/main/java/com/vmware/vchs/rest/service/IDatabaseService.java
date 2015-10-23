package com.vmware.vchs.rest.service;

import com.vmware.vchs.rest.domain.Database;

/**
 * Created by liuzhiwen on 14-7-18.
 */
public interface IDatabaseService {

    public Database provision(String name, String plan);

    public String unProvision(String id);

}
