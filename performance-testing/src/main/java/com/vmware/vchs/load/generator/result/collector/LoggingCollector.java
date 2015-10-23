package com.vmware.vchs.load.generator.result.collector;

/**
 * Created by liuda on 6/18/15.
 */

import com.vmware.vchs.load.generator.config.PerformanceProperties;
import com.vmware.vchs.load.generator.result.model.ElasticSearchResponse;
import com.vmware.vchs.load.generator.result.model.SubHits;
import com.vmware.vchs.load.generator.util.TimeUtil;
import com.vmware.vchs.performance.model.ComponentType;
import com.vmware.vchs.performance.model.LoggingData;
import com.vmware.vchs.performance.repository.LoggingDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by liuda on 6/1/15.
 */
@Component
public class LoggingCollector extends BaseCollector implements Collectable {


    @Autowired
    LoggingDataRepository loggingDataRepository;

    private List<String> requestIds;


    public List<String> getRequestIds() {
        return requestIds;
    }

    public void setRequestIds(List<String> requestIds) {
        this.requestIds = requestIds;
    }

    public LoggingCollector() {
    }

    @Override
    public void doCollect() {
        String start = TimeUtil.fromDate(super.getTestRun().getStartTime());
        String end = TimeUtil.fromDate(super.getTestRun().getEndTime());
        String span = start + "," + end;
        //TODO change to this line
//        for(String component:getLoggingComponent()){
//            ElasticSearchResponse response = testClient.getTypeSearchPath(component, span, 100000);
//        }
        ElasticSearchResponse response = testClient.getTypeSearchPath("LoggingDemo", "logstash-2015.06.15,logstash-2015.06.15", 10);
        for (SubHits subHits : response.getHits().getHits()) {
            if(getLoggingComponent().contains(subHits.get_type())) {
                LoggingData.LoggingDataBuilder builder = new LoggingData.LoggingDataBuilder(subHits.get_source().getMessage(), super.getTestRun(), ComponentType.valueOf(subHits.get_type()), subHits.get_source().getContext().getRequestId());
                builder.dispatchedAt(subHits.get_source().getContext().getBenchmark().getDispatchedAt().toLocalDateTime());
                builder.receivedAt(subHits.get_source().getContext().getBenchmark().getReceivedAt().toLocalDateTime());
                LoggingData data = builder.build();
                LOG.info("LoggingData is " + data.toString());
                loggingDataRepository.save(data);
            }
        }
    }

}