package com.vmware.vchs.performance.service;

import java.util.List;
import java.util.Map;

/**
 * Created by georgeliu on 14/12/1.
 */
public interface RequestMetricsService {

    public Map<String, List<Double>> getRequestResponseByTestCase(String testCaseName);

    public Map<String, Map<Double, Double>> getRequestResponsePercentileByTestCase(String testCaseName);

    public List<String> getAllTestCases();

    public Map<String, List<Double>> getAllRequestResponse();
}
