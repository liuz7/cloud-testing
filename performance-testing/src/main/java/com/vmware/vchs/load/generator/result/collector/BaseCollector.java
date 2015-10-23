package com.vmware.vchs.load.generator.result.collector;

import com.vmware.vchs.load.generator.config.PerformanceProperties;
import com.vmware.vchs.load.generator.result.path.PerformanceTestClient;
import com.vmware.vchs.load.generator.result.path.PerformanceTestClientImpl;
import com.vmware.vchs.performance.model.TestRun;
import com.vmware.vchs.test.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liuda on 6/18/15.
 */
public abstract class BaseCollector implements Collectable {

    @Autowired
    protected  PerformanceTestClient testClient;

    @Autowired
    PerformanceProperties properties;

    private Configuration configuration;

    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
//        testClient=new PerformanceTestClientImpl();
        testClient.setConfiguration(this.configuration);
    }

    private List<String> components = new ArrayList<>();

    public final static List<String> DEFAULT_COMPONENTS = new ArrayList<>();

    static {
        DEFAULT_COMPONENTS.add("gateway");
        DEFAULT_COMPONENTS.add("node");
        DEFAULT_COMPONENTS.add("logstash");
    }

    public static final Logger LOG = LoggerFactory.getLogger(BaseCollector.class);

//    protected PerformanceTestClient testClient;
//    protected PerformanceTestClient testClient = new PerformanceTestClientImpl();

    private TestRun testRun;

    private List<Collectable> collectors = new ArrayList<>();

    public List<Collectable> getCollectors() {
        return collectors;
    }

    public void setCollectors(List<Collectable> collectors) {
        this.collectors = collectors;
    }

    public TestRun getTestRun() {
        return testRun;
    }

    public void setTestRun(TestRun testRun) {
        this.testRun = testRun;
    }

    public List<String> getLoggingComponent() {
        if (components.size() == 0) {
            if (properties.getComponent() != null) {
                components = Arrays.asList(properties.getComponent().split(","));
            }
            else {
                components = DEFAULT_COMPONENTS;
            }
        }
        return components;
    }

    @Override
    public void collect() {
        beforeCollect();
        doCollect();
        afterCollect();
    }


    public void beforeCollect() {
    }

    public void afterCollect() {

    }

    public abstract void doCollect();
}
