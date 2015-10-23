package com.vmware.vchs.billing.model;

import com.google.common.base.MoreObjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by georgeliu on 15/7/16.
 */
@Entity
@Table(name = "event")
public class Event {
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("service_id", service_id)
                .add("event_id", event_id)
                .add("service_name", service_name)
                .add("instanceId", instanceId)
                .add("client_id", client_id)
                .add("event_utc", event_utc)
                .add("modify_date", modify_date)
                .add("receive_date", receive_date)
                .add("revision", revision)
                .add("status", status)
                .add("acceptance_id", acceptance_id)
                .add("retry_count", retry_count)
                .add("retry_interval", retry_interval)
                .add("retry_date", retry_date)
                .add("content", content)
                .toString();
    }

    private String service_id;
    @Id
    private String event_id;
    private String service_name;
    @Column(name = "instance_id", nullable = false)
    private String instanceId;
    private String client_id;
    private String event_utc;
    private Timestamp receive_date;
    private Timestamp modify_date;
    private int revision;
    private String status;
    private String acceptance_id;
    private int retry_count;
    private int retry_interval;
    private Timestamp retry_date;
    private String content;

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getEvent_utc() {
        return event_utc;
    }

    public void setEvent_utc(String event_utc) {
        this.event_utc = event_utc;
    }

    public Timestamp getReceive_date() {
        return receive_date;
    }

    public void setReceive_date(Timestamp receive_date) {
        this.receive_date = receive_date;
    }

    public Timestamp getModify_date() {
        return modify_date;
    }

    public void setModify_date(Timestamp modify_date) {
        this.modify_date = modify_date;
    }

    public int getRevision() {
        return revision;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAcceptance_id() {
        return acceptance_id;
    }

    public void setAcceptance_id(String acceptance_id) {
        this.acceptance_id = acceptance_id;
    }

    public int getRetry_count() {
        return retry_count;
    }

    public void setRetry_count(int retry_count) {
        this.retry_count = retry_count;
    }

    public int getRetry_interval() {
        return retry_interval;
    }

    public void setRetry_interval(int retry_interval) {
        this.retry_interval = retry_interval;
    }

    public Timestamp getRetry_date() {
        return retry_date;
    }

    public void setRetry_date(Timestamp retry_date) {
        this.retry_date = retry_date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}