package com.vmware.vchs.performance.controller;

import com.vmware.vchs.performance.service.RequestMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by georgeliu on 14/11/29.
 */
@RestController
@RequestMapping("/")
public class RequestMetricsController {

    @Autowired
    RequestMetricsService requestMetricsService;

    @RequestMapping("{test}/requests")
    public Map<String, List<Double>> getRequestResponseTimeByTestCase(@PathVariable String test) {
        return this.requestMetricsService.getRequestResponseByTestCase(test);
    }

    @RequestMapping("{test}/requests/p")
    public Map<String, Map<Double, Double>> getRequestResponsePercentileByTestCase(@PathVariable String test) {
        return this.requestMetricsService.getRequestResponsePercentileByTestCase(test);
    }

    @RequestMapping("tests")
    public List<String> getAllTestCases() {
        return this.requestMetricsService.getAllTestCases();
    }

}
