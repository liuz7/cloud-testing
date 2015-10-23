package com.vmware.vchs.load.generator.result.collector;

/**
 * Created by liuda on 6/18/15.
 */

import com.vmware.vchs.load.generator.common.Constants;
import com.vmware.vchs.load.generator.config.InfluxDbProperties;
import com.vmware.vchs.load.generator.result.model.DetailInfluxDbResponse;
import com.vmware.vchs.load.generator.result.model.InfluxDbResponse;
import com.vmware.vchs.load.generator.util.TimeUtil;
import com.vmware.vchs.performance.model.ComponentType;
import com.vmware.vchs.performance.model.MetricsData;
import com.vmware.vchs.performance.repository.MetricsDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by liuda on 6/1/15.
 */
@Component
public class MetricsCollector extends BaseCollector implements Collectable {

    @Autowired
    InfluxDbProperties influxDbProperties;

    @Autowired
    MetricsDataRepository metricsDataRepository;

    public MetricsCollector() {
    }

    private int findKeyFromInfluxDbResponse(String key, InfluxDbResponse response) {
        for (DetailInfluxDbResponse detailInfluxDbResponse : response) {
            if (detailInfluxDbResponse.getColumns() != null && detailInfluxDbResponse.getColumns().length > 0) {
                for (int i = 0; i < detailInfluxDbResponse.getColumns().length; i++) {
                    if (detailInfluxDbResponse.getColumns()[i].equalsIgnoreCase(key)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }


    @Override
    public void doCollect() {
        InfluxDbResponse listSeries = testClient.getFromInfluxDb(50, "list series");
        int index = findKeyFromInfluxDbResponse(Constants.INFLUXDB_COLUMNS_NAME, listSeries);
        Set<String> containerNames = new HashSet<>();
        for (DetailInfluxDbResponse detailInfluxDbResponse : listSeries) {
            for (String[] points : detailInfluxDbResponse.getPoints()) {
                if (points != null && points.length > index && points[index].startsWith(Constants.INFLUXDB_POINTS_DBAAS_PREFIX) && points[index].contains(Constants.INFLUXDB_POINTS_DBAAS_CONTAINER)) {
                    containerNames.add(points[index]);
                }
            }
        }
        boolean findIndex = false;
//        for (String containerName : containerNames) {
//            InfluxDbResponse response = testClient.getFromInfluxDb(50, "select * from \""+containerName+"\" where time > '"+ TimeUtil.formatInfluxDbTime(super.getTestRun().getStartTime())+"' and time < '"+TimeUtil.formatInfluxDbTime(super.getTestRun().getEndTime())+"'");
        InfluxDbResponse response = testClient.getFromInfluxDb(50, "select * from \"dbaas-20150612_59.us.container.4490e615b0.192_168_80_78\" where time < '2015-06-26 11:37:18.000' and time >'2015-06-23 10:37:18.000'");

//            InfluxDbResponse response = testClient.getFromInfluxDb(50, "select * from \"dbaas-20150612_59.us.container.ccb63b22d2.192_168_80_79\" where time < '2015-06-25 11:37:18.000' and time >'2015-06-24 10:37:18.000'");
        int mem = -1;
        int cpu = -1;
        int type = -1;
        int date = -1;
        if (findIndex == false) {
            mem = findKeyFromInfluxDbResponse(influxDbProperties.getIndicator().getMem(), response);
            cpu = findKeyFromInfluxDbResponse(influxDbProperties.getIndicator().getCpu(), response);
            type = findKeyFromInfluxDbResponse(influxDbProperties.getIndicator().getType(), response);
            date = findKeyFromInfluxDbResponse(influxDbProperties.getIndicator().getTime(), response);
            findIndex = true;
        }

        MetricsData.MetricsDataBuilder builder = null;
        for (DetailInfluxDbResponse detailInfluxDbResponse : response) {
            for (String[] points : detailInfluxDbResponse.getPoints()) {
                if (getLoggingComponent().contains(points[type])) {
                    builder = new MetricsData.MetricsDataBuilder(points[cpu], super.getTestRun(), ComponentType.valueOf(points[type]), points[mem], TimeUtil.fromTimeStamp(points[date]));
                    MetricsData data = builder.build();
                    LOG.info("MetricsData is " + data.toString());
                    metricsDataRepository.save(data);
//                }
                }
            }
        }

    }

}