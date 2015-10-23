package com.vmware.vchs.load.generator.result.collector;

/**
 * Created by liuda on 6/18/15.
 */

import com.vmware.vchs.performance.repository.TestRunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liuda on 6/1/15.
 */
@Component
public class ResultCollector extends BaseCollector {

    @Autowired
    TestRunRepository testRunRepository;

    @Autowired
    LoggingCollector loggingCollector;

    @Autowired
    MetricsCollector metricsCollector;


    public void setRequestIds(List<String> requestIds) {
        loggingCollector.setRequestIds(requestIds);
    }
    //
//    @Autowired
//    public ResultCollector(@Value("DefaultName")String name){
//        name.toString();
//    }

    //    @Autowired
//    public ResultCollector(TestRun testRun){
////        this(null,testRun);
//    }
    public ResultCollector() {
//        super.setTestRun(testRun);

    }


    @Override
    public void beforeCollect() {
        super.beforeCollect();
//        super.getCollectors().add(loggingCollector);
        super.getCollectors().add(metricsCollector);
        for (Collectable collector : super.getCollectors()) {
            collector.setConfiguration(getConfiguration());
            collector.setTestRun(super.getTestRun());
        }
        testRunRepository.save(super.getTestRun());
    }

    @Override
    public void doCollect() {
        for (Collectable collector : super.getCollectors()) {
            collector.collect();
        }
    }


}