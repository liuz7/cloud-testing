package com.vmware.vchs.load.generator.server;

public interface LoadServer {

    public boolean addVUser(long duration);

    public boolean addVUser(int num, long duration);

    public boolean startTesting();

    public boolean stopTesting();

    public LoadTestResult getResult();

}
