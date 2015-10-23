package com.vmware.vchs.rest.discovery;

import com.vmware.vchs.common.utils.Utils;
import com.vmware.vchs.discovery.InstanceDetail;
import com.vmware.vchs.discovery.ServiceDiscoveryClient;
import com.vmware.vchs.discovery.ServiceDiscoveryClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * Created by liuzhiwen on 14-7-15.
 */

@Component
@EnableScheduling
public class ServiceDiscoveryTimer {

    @Autowired
    private ServiceDiscoveryProperties serviceDiscoveryProperties;

    @Value("${server.port}")
    private int port;

    @Scheduled(fixedRate = 3000)
    public void register() throws Exception {
        if (Utils.checkHostAndPort(serviceDiscoveryProperties.getHost(), String.valueOf(serviceDiscoveryProperties.getPort()))) {
            addInstanceWithTTL(getInstanceDetail().getHostname(), getInstanceDetail(), 5);
        }
    }

    private void addInstanceWithTTL(String instanceId, InstanceDetail instanceDetail, int ttl) throws Exception {
        String serverUrl = "http://" + serviceDiscoveryProperties.getHost() + ":" + serviceDiscoveryProperties.getPort();
        ServiceDiscoveryClient serviceDiscoveryClient = ServiceDiscoveryClientFactory.newInstance(serverUrl);
        serviceDiscoveryClient.addInstanceWithTTL(serviceDiscoveryProperties.getPath() + instanceId, instanceDetail, ttl);
    }

    private InstanceDetail getInstanceDetail() throws UnknownHostException {
        String localIP = Inet4Address.getLocalHost().getHostAddress();
        int pid = Integer.valueOf(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
        InstanceDetail instanceDetail = new InstanceDetail(localIP, port, pid);
        return instanceDetail;
    }

}
