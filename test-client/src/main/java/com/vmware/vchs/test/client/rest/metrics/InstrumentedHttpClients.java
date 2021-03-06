package com.vmware.vchs.test.client.rest.metrics;

import com.codahale.metrics.MetricRegistry;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;


public class InstrumentedHttpClients {
    private InstrumentedHttpClients() {
        super();
    }

    public static CloseableHttpClient createDefault(MetricRegistry metricRegistry) {
        return createDefault(metricRegistry, HttpClientMetricNameStrategies.METHOD_ONLY);
    }

    public static CloseableHttpClient createDefault(MetricRegistry metricRegistry,
                                                    HttpClientMetricNameStrategy metricNameStrategy) {
        return custom(metricRegistry, metricNameStrategy).build();
    }

    public static HttpClientBuilder custom(MetricRegistry metricRegistry) {
        return custom(metricRegistry, HttpClientMetricNameStrategies.METHOD_ONLY);
    }

    public static HttpClientBuilder custom(MetricRegistry metricRegistry,
                                           HttpClientMetricNameStrategy metricNameStrategy) {
        return HttpClientBuilder.create()
                .setRequestExecutor(new InstrumentedHttpRequestExecutor(metricRegistry, metricNameStrategy))
                .setConnectionManager(new InstrumentedHttpClientConnectionManager(metricRegistry));
    }


}
