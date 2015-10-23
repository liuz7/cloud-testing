package com.vmware.vchs.performance.model;

import com.google.common.base.MoreObjects;

import javax.persistence.*;

/**
 * Created by georgeliu on 14/11/27.
 */
@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "duration", nullable = false)
    private double duration;
    @Column(name = "operation", nullable = false)
    private String operation;
    @Column(name = "url", nullable = false)
    private String url;
    @ManyToOne(cascade = CascadeType.ALL)
    private TestCase testCase;

    protected Request() {
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
    }

    public Request(double duration, String operation, String url) {
        this.duration = duration;
        this.operation = operation;
        this.url = url;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("Duration", duration)
                .add("Operation", operation)
                .add("Url", url)
                .toString();
    }
}
