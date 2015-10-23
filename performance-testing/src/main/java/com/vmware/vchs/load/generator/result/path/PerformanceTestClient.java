package com.vmware.vchs.load.generator.result.path;

import com.vmware.vchs.load.generator.result.model.ElasticSearchResponse;
import com.vmware.vchs.load.generator.result.model.InfluxDbResponse;
import com.vmware.vchs.test.config.Configuration;

/**
 * Created by liuda on 6/3/15.
 */
public interface PerformanceTestClient {
    ElasticSearchResponse getTypeSearchPath(String type, String time, int size);
    InfluxDbResponse getFromInfluxDb(int size, String command);
    void setConfiguration(Configuration configuration);
}
