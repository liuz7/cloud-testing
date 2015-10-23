package com.vmware.vchs.test.client.model.etcd;

/**
 * Created by georgeliu on 15/4/8.
 */
public class SystemInfo {

    private String totalStorage;
    private String freeStorage;

    public String getTotalStorage() {
        return totalStorage;
    }

    public void setTotalStorage(String totalStorage) {
        this.totalStorage = totalStorage;
    }

    public String getFreeStorage() {
        return freeStorage;
    }

    public void setFreeStorage(String freeStorage) {
        this.freeStorage = freeStorage;
    }
}
