package com.vmware.vchs.performance.model;

import com.google.common.base.MoreObjects;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

/**
 * Created by georgeliu on 14/11/29.
 */
@Entity
@Table(name = "metrics")
public class MetricsData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "cpu", nullable = false)
    private String cpu;

    @ManyToOne(cascade = CascadeType.REFRESH)
    private TestRun testRun;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "memory", nullable = false)
    private String memory;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @Column(name = "date", nullable = false)
    private LocalDateTime date;


    public MetricsData(MetricsDataBuilder builder) {
        this.cpu = builder.cpu;
        this.testRun = builder.testRun;
        this.memory = builder.memory;
        this.type = builder.type.toString();
        this.date = builder.date;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("cpu", this.cpu)
                .add("testRun", this.testRun.toString())
                .add("type", this.type)
                .add("memory", this.memory)
                .add("date", this.date)
                .toString();
    }

    public static class MetricsDataBuilder {
        private String cpu;

        private TestRun testRun;

        private ComponentType type;

        private String memory;

        private LocalDateTime date;


        public MetricsDataBuilder(String cpu, TestRun testRun, ComponentType type, String memory, LocalDateTime date) {
            this.cpu = cpu;
            this.testRun = testRun;
            this.type = type;
            this.memory = memory;
            this.date = date;
        }

        public MetricsData build() {
            return new MetricsData(this);
        }
    }


}
