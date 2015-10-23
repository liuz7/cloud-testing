package com.vmware.vchs.longrunning.base;

/**
 * Created by sjun on 8/27/15.
 */
public interface TestContext {
    String getState();
    String getPreviousState();
    void setGlobalValue(String key, String value);
    String getGlobalValue(String key);
}
