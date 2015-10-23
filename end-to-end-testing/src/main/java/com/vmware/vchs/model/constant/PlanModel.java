package com.vmware.vchs.model.constant;

import com.vmware.vchs.model.portal.instance.CreateInstanceRequest;
import com.vmware.vchs.model.portal.instance.Plan;

/**
 * Created by liuda on 15/5/12.
 */
public class PlanModel {
    private String id;
    private int cpu;
    private int disk;

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public int getDisk() {
        return disk;
    }

    public void setDisk(int disk) {
        this.disk = disk;
    }

    private int memory;

    public Plan toPlan(){
        Plan plan = new Plan();
        plan.setId(this.id);
        plan.setVcpu(this.cpu);
        plan.setMemory(this.memory);
        return plan;
    }

    public static PlanModel fromPlan(Plan plan, int diskSize){
        PlanModel planData = new PlanModel();
        planData.setId(plan.getId());
        planData.setMemory(plan.getMemory());
        planData.setCpu(plan.getVcpu());
        planData.setDisk(diskSize);
        return planData;
    }
}
