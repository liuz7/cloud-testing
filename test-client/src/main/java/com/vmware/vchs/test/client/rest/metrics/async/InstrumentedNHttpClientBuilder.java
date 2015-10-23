package com.vmware.vchs.test.client.rest.metrics.async;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.vmware.vchs.common.utils.Utils;
import com.vmware.vchs.test.client.rest.metrics.HttpClientMetricNameStrategies;
import com.vmware.vchs.test.client.rest.metrics.HttpClientMetricNameStrategy;
import com.vmware.vchs.test.client.rest.metrics.HttpRequestMetrics;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncResponseConsumer;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Future;

/**
 * Created by georgeliu on 14/11/27.
 */
public class InstrumentedNHttpClientBuilder extends HttpAsyncClientBuilder {

    private final MetricRegistry metricRegistry;
    private final String name;
    private final HttpClientMetricNameStrategy metricNameStrategy;
    private final HttpRequestMetrics metrics;
    private static final Logger logger = LoggerFactory.getLogger(InstrumentedNHttpClientBuilder.class);

    public InstrumentedNHttpClientBuilder(MetricRegistry metricRegistry, HttpClientMetricNameStrategy metricNameStrategy, String name, HttpRequestMetrics metrics) {
        super();
        this.metricRegistry = metricRegistry;
        this.metricNameStrategy = metricNameStrategy;
        this.name = name;
        this.metrics = metrics;
    }

    public InstrumentedNHttpClientBuilder(MetricRegistry metricRegistry, HttpRequestMetrics metrics) {
        this(metricRegistry, HttpClientMetricNameStrategies.METHOD_ONLY, null, metrics);

    }

    public InstrumentedNHttpClientBuilder(MetricRegistry metricRegistry, HttpClientMetricNameStrategy metricNameStrategy, HttpRequestMetrics metrics) {
        this(metricRegistry, metricNameStrategy, null, metrics);
    }

    public InstrumentedNHttpClientBuilder(MetricRegistry metricRegistry, String name, HttpRequestMetrics metrics) {
        this(metricRegistry, HttpClientMetricNameStrategies.METHOD_ONLY, name, metrics);
    }

    private Timer timer(HttpRequest request) {
        return metricRegistry.timer(metricNameStrategy.getNameFor(name, request));
    }

    @Override
    public CloseableHttpAsyncClient build() {
        final CloseableHttpAsyncClient ac = super.build();
        return new CloseableHttpAsyncClient() {

            @Override
            public boolean isRunning() {
                return ac.isRunning();
            }

            @Override
            public void start() {
                ac.start();
            }

            @Override
            public <T> Future<T> execute(HttpAsyncRequestProducer requestProducer, HttpAsyncResponseConsumer<T> responseConsumer, HttpContext context, FutureCallback<T> callback) {
                final Timer.Context timerContext;
                try {
                    timerContext = timer(requestProducer.generateRequest()).time();
                } catch (IOException ex) {
                    throw new AssertionError(ex);
                } catch (HttpException ex) {
                    throw new AssertionError(ex);
                }
                try {
                    metrics.getActiveConnections().incrementAndGet();
                    metrics.getScheduledConnections().decrementAndGet();
                    try {
                        logger.info(requestProducer.generateRequest().getRequestLine().getMethod() + " " + requestProducer.generateRequest().getRequestLine().getUri());
                    } catch (IOException | HttpException e) {
                        logger.info(Utils.getStackTrace(e));
                    }
                    return ac.execute(requestProducer, responseConsumer, context, callback);
                } finally {
                    timerContext.stop();
                }
            }

            @Override
            public void close() throws IOException {
                ac.close();
            }
        };
    }

}
