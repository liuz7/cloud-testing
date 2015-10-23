package com.vmware.vchs.load.generator.result.path;

import com.vmware.vchs.test.client.model.portal.PortalResponseErrorHandler;
import com.vmware.vchs.load.generator.config.InfluxDbProperties;
import com.vmware.vchs.load.generator.result.model.ElasticSearchResponse;
import com.vmware.vchs.load.generator.result.model.InfluxDbResponse;
import com.vmware.vchs.test.client.rest.RestClient;
import com.vmware.vchs.test.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by georgeliu on 15/3/10.
 */
@Component
public class PerformanceTestClientImpl implements PerformanceTestClient {

    @Autowired
    InfluxDbProperties influxDbProperties;

    private Configuration configuration;
    protected static RestClient restClient;

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        this.restClient = new RestClient(this.configuration);
        restClient.loadConverters("com.vmware.vchs.load.generator.result.converter");
        restClient.setErrorHandler(new PortalResponseErrorHandler());
    }

    public PerformanceTestClientImpl() {
    }
    @Override
    public ElasticSearchResponse getTypeSearchPath(String type, String time, int size) {
        Map<String, Object> query=new HashMap<>();
        query.put("size", size);
        query.put("q", "type:" + type);
        this.restClient.setBaseUrl(this.configuration.getElasticSearch().getBaseUrl());
        return restClient.getForObject(PerformanceLinkBuilder.getTypeSearchPath(time), query, ElasticSearchResponse.class);
    }

    @Override
    public InfluxDbResponse getFromInfluxDb(int size, String command){
//        try {
            Map<String, Object> query = new LinkedHashMap<>();
            query.put("u", influxDbProperties.getUser());
            query.put("p", influxDbProperties.getPassword());
            query.put("q", command);
//        query.put("limit", influxDbProperties.getPassword());
            this.restClient.setBaseUrl(influxDbProperties.getBaseUrl());
            return restClient.getForObject(PerformanceLinkBuilder.getInfluxDbPath(), query, InfluxDbResponse.class);
//        }
//        catch (RestClientException e){
//            return  null;
//        }
    }
}
