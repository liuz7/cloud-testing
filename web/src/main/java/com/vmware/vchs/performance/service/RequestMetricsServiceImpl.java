package com.vmware.vchs.performance.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Doubles;
import com.vmware.vchs.performance.model.TestCase;
import com.vmware.vchs.performance.repository.RequestRepository;
import com.vmware.vchs.performance.repository.TestCaseRepository;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by georgeliu on 14/12/1.
 */
@Service
public class RequestMetricsServiceImpl implements RequestMetricsService {

    @Autowired
    RequestRepository requestRepository;
    @Autowired
    TestCaseRepository testCaseRepository;

    @Override
    public Map<String, List<Double>> getRequestResponseByTestCase(String testCaseName) {
        return getResponseTimeListByTestCase(testCaseName);
    }

    @Override
    public Map<String, Map<Double, Double>> getRequestResponsePercentileByTestCase(String testCaseName) {
        Map<String, List<Double>> responseTimeMap = getResponseTimeListByTestCase(testCaseName);
        double[] responseTimeArray = Doubles.toArray(responseTimeMap.get(testCaseName));
        Percentile percentile = new Percentile();
        double[] percentileValues = new double[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
        double[] percentileResult = new double[percentileValues.length];
        Map<Double, Double> percentileResultMap = Maps.newLinkedHashMap();
        Map<String, Map<Double, Double>> resultMap = Maps.newHashMap();
        for (int i = 0; i < percentileValues.length; i++) {
            percentileResult[i] = Precision.round(percentile.evaluate(responseTimeArray, percentileValues[i]), 2);
            percentileResultMap.put(new Double(percentileValues[i]), new Double(percentileResult[i]));
        }
        resultMap.put(testCaseName, percentileResultMap);
        return resultMap;
    }

    @Override
    public List<String> getAllTestCases() {
        return this.testCaseRepository.findAll().stream().map(e -> e.getTestName()).collect(Collectors.toList());
    }

    @Override
    public Map<String, List<Double>> getAllRequestResponse() {
        //TODO
        return Maps.newHashMap();
    }

    private Map<String, List<Double>> getResponseTimeListByTestCase(String testCaseName) {
        List<Double> responseTimeList = Lists.newArrayList();
        Map<String, List<Double>> responseTimeMap = Maps.newHashMap();
        List<TestCase> testCases = this.testCaseRepository.findByTestName(testCaseName);
        for (TestCase testCase : testCases) {
            List<Double> responseTimesPerTest = this.requestRepository.findByTestCase(testCase).stream().map(e -> e.getDuration()).collect(Collectors.toList());
            responseTimeList.addAll(responseTimesPerTest);
        }
        responseTimeMap.put(testCaseName, responseTimeList);
        return responseTimeMap;
    }


}
