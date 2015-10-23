package com.vmware.vchs.rest.discovery;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by liuzhiwen on 14-7-15.
 */
@Component
@ConfigurationProperties(prefix = "service.discovery")
public class ServiceDiscoveryProperties {

    private String host;
    private int port;
    private String path;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
