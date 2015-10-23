package com.vmware.vchs.performance.model;

import javax.persistence.*;

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

/**
 * Created by georgeliu on 14/11/29.
 */
@Entity
@Table(name = "logging")
public class LoggingData {

    public enum Recordtype {
        REQUEST, RESPONSE, NOTIFICATION, LoggingDemo
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "message", nullable = false)
    private String message;

    @ManyToOne(cascade = CascadeType.REFRESH)
    private TestRun testRun;

    @Column(name = "type", nullable = false)
    private ComponentType type;

    @Column(name = "requestId", nullable = false)
    private String requestId;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @Column(name = "dispatchedAt")
    private LocalDateTime dispatchedAt;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @Column(name = "receivedAt")
    private LocalDateTime receivedAt;

    @Column(name = "operation")
    private String operation;

    @Column(name = "recordType")
    private Recordtype recordType;

    @Column(name = "subComponent")
    private ComponentType subComponent;

    public LocalDateTime getDispatchedAt() {
        return dispatchedAt;
    }

    public void setDispatchedAt(LocalDateTime dispatchedAt) {
        this.dispatchedAt = dispatchedAt;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public TestRun getTestRun() {
        return testRun;
    }

    public ComponentType getType() {
        return type;
    }

    public String getRequestId() {
        return requestId;
    }


    public String getOperation() {
        return operation;
    }

    public Recordtype getRecordType() {
        return recordType;
    }

    public ComponentType getSubComponent() {
        return subComponent;
    }

    public LoggingData(LoggingDataBuilder builder) {
        this.message = builder.message;
        this.testRun = builder.testRun;
        this.requestId = builder.requestId;
        this.dispatchedAt = builder.dispatchedAt;
        this.receivedAt = builder.receivedAt;
        this.operation = builder.operation;
        this.recordType = builder.recordType;
        this.subComponent = builder.subComponent;
        this.type = builder.type;
    }

    @Override
    public String toString(){
        return MoreObjects.toStringHelper(this)
                .add("message", this.message)
                .add("testRun", this.testRun.toString())
                .add("requestId", this.requestId)
                .add("dispatchedAt", this.dispatchedAt)
                .add("receivedAt", this.receivedAt)
                .add("operation", this.operation)
                .add("recordType", this.recordType)
                .add("subComponent", this.subComponent)
                .add("type", this.type)
                .toString();
    }

    public static class LoggingDataBuilder {
        private String message;

        private TestRun testRun;

        private ComponentType type;

        private String requestId;

        private LocalDateTime dispatchedAt;

        private LocalDateTime receivedAt;

        private String operation;

        private Recordtype recordType;

        private ComponentType subComponent;

        public LoggingDataBuilder(String message, TestRun testRun, ComponentType type, String requestId) {
            this.message = message;
            this.testRun = testRun;
            this.type = type;
            this.requestId = requestId;
        }

        public LoggingDataBuilder dispatchedAt(LocalDateTime dispatchedAt) {
            this.dispatchedAt = dispatchedAt;
            return this;
        }

        public LoggingDataBuilder receivedAt(LocalDateTime receivedAt) {
            this.receivedAt = receivedAt;
            return this;
        }

        public LoggingDataBuilder operation(String operation) {
            this.operation = operation;
            return this;
        }

        public LoggingDataBuilder recordType(Recordtype recordType) {
            this.recordType = recordType;
            return this;
        }

        public LoggingDataBuilder subComponent(ComponentType subComponent) {
            this.subComponent = subComponent;
            return this;
        }

        public LoggingData build() {
            return new LoggingData(this);
        }
    }


}
