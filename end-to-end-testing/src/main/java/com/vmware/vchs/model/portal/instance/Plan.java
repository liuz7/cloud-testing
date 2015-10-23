package com.vmware.vchs.model.portal.instance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Objects;

/**
 * Created by georgeliu on 15/4/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Plan {

    private String id;
    private int vcpu;
    private int memory;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getVcpu() {
        return vcpu;
    }

    public void setVcpu(int vcpu) {
        this.vcpu = vcpu;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id, this.vcpu, this.memory);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Plan other = (Plan) obj;
        return Objects.equal(this.id, other.getId())
                && Objects.equal(this.vcpu, other.getVcpu())
                && Objects.equal(this.memory, other.getMemory());
    }
}
