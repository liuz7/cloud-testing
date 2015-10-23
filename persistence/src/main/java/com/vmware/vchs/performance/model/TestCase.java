package com.vmware.vchs.performance.model;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by georgeliu on 14/11/29.
 */
@Entity
@Table(name = "test_cases")
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "test_name", nullable = false)
    private String testName;
//    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "testCase")
//    private List<Request> requests = Lists.newArrayList();
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "executed_at", nullable = false)
    private Date executedAt;

    protected TestCase() {
    }

    public TestCase(String testName, Date executedAt) {
        this.testName = testName;
        this.executedAt = executedAt;
    }

    public TestCase(String testName) {
        this.testName = testName;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

//    public List<Request> getRequests() {
//        return requests;
//    }
//
//    public void setRequests(List<Request> requests) {
//        this.requests = requests;
//    }

    public Date getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(Date executedAt) {
        this.executedAt = executedAt;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("TestName", testName)
                .add("Executed", executedAt)
                .toString();
    }
}
