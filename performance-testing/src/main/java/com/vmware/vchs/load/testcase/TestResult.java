package com.vmware.vchs.load.testcase;

public class TestResult {

    private boolean success;

    private String requestId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public TestResult() {
        success = false;
    }

    public void setSuccess(boolean flag) {
        success = flag;
    }

    public boolean getSuccess() {
        return success;
    }
}
