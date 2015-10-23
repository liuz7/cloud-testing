package com.vmware.vchs.model.billing;

/**
 * Created by liuda on 6/25/15.
 */
public class MeterModel {
    private int plan = -1;
    private int storage = -1;
    private int backup = -1;
    private int iops = -1;
    private int egress = -1;
    private int ingress = -1;

    public int getIops() {
        return iops;
    }

    public void setIops(int iops) {
        this.iops = iops;
    }

    public int getEgress() {
        return egress;
    }

    public void setEgress(int engress) {
        this.egress = engress;
    }

    public int getIngress() {
        return ingress;
    }

    public void setIngress(int ingress) {
        this.ingress = ingress;
    }

    public int getBackup() {
        return backup;
    }

    public void setBackup(int backup) {
        this.backup = backup;
    }

    public int getPlan() {
        return plan;
    }

    public void setPlan(int plan) {
        this.plan = plan;
    }

    public int getStorage() {
        return storage;
    }

    public void setStorage(int storage) {
        this.storage = storage;
    }
}
