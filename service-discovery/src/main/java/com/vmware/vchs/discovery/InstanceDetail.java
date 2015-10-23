package com.vmware.vchs.discovery;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Created by liuzhiwen on 14-7-3.
 */
public class InstanceDetail {

    private String hostname;
    private int port;
    private int pid;

    public InstanceDetail() {
    }

    public InstanceDetail(String hostname, int port, int pid) {
        this.hostname = hostname;
        this.port = port;
        this.pid = pid;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.hostname, this.port, this.pid);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final InstanceDetail other = (InstanceDetail) obj;
        return Objects.equal(this.hostname, other.hostname)
                && Objects.equal(this.port, other.port)
                && Objects.equal(this.pid, other.pid);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("HostName", hostname)
                .add("Port", port)
                .add("Pid", pid)
                .toString();
    }
}
