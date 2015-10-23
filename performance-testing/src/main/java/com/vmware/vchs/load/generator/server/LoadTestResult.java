package com.vmware.vchs.load.generator.server;

public class LoadTestResult {
    private int numSuccess;
    private int numFailure;
    private int vUserFailure;

    public LoadTestResult() {
        numSuccess = 0;
        numFailure = 0;
        vUserFailure = 0;
    }

    public void addSuccess(int num) {
        numSuccess += num;
    }

    public void addFailure(int num) {
        numFailure += num;
    }

    public void addVUserFailure(int num) {
        vUserFailure += num;
    }

    public int getNumSuccess() {
        return numSuccess;
    }

    public int getNumFailure() {
        return numFailure;
    }

    public int getVUserFailure() {
        return vUserFailure;
    }
}
