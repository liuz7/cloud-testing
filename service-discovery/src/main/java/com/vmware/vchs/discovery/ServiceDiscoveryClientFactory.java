package com.vmware.vchs.discovery;

/**
 * Created by liuzhiwen on 14-7-11.
 */
public class ServiceDiscoveryClientFactory {

    private ServiceDiscoveryClientFactory(){}

    public static ServiceDiscoveryClient newInstance(final String server){
        return new ServiceDiscoveryClient(server);
    }
}
