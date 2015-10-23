package com.vmware.vchs.performance.model;

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

/**
 * Created by georgeliu on 14/11/29.
 */
@Entity
@Table(name = "test_run")
public class TestRun {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "test_name", nullable = false)
    private String testName;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    protected TestRun() {
    }

    public TestRun(String testName, LocalDateTime startTime, LocalDateTime endTime) {
        this.testName = testName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public TestRun(String testName) {
        this.testName = testName;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    @Override
    public String toString(){
        return MoreObjects.toStringHelper(this)
                .add("testName", this.testName)
                .add("startTime", this.startTime)
                .add("endTime", this.endTime)
                .toString();
    }

}
