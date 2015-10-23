package com.vmware.vchs.test.config;

/**
 * Created by georgeliu on 14/11/14.
 */
public class Concurrent {

    private int invocationCount;
    private int threadPoolSize;

    public int getInvocationCount() {
        return invocationCount;
    }

    public void setInvocationCount(int invocationCount) {
        this.invocationCount = invocationCount;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }
}
