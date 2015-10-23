package com.vmware.vchs.testng;

import com.vmware.vchs.test.config.Configuration;

/**
 * Testng base method listener for listening the method invocations.
 */
public abstract class BaseTestListener {
    protected static Configuration configuration;

    public static Configuration getConfiguration() {
        return configuration;
    }

    public static void setConfiguration(Configuration configuration) {
        BaseTestListener.configuration = configuration;
    }
}
