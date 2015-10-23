package com.vmware.vchs.load.generator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by liuda on 6/18/15.
 */
@Component
@ConfigurationProperties("performance")
public class PerformanceProperties {
    String component;

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }
}
