package com.vmware.vchs.test.client.rest.async;

import com.codahale.metrics.MetricRegistry;
import com.vmware.vchs.common.utils.Utils;
import com.vmware.vchs.test.client.rest.IdleConnectionMonitorThread;
import com.vmware.vchs.test.client.rest.metrics.HttpRequestMetrics;
import com.vmware.vchs.test.client.rest.metrics.async.InstrumentedNHttpClientBuilder;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.IOReactorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by georgeliu on 14/11/19.
 */
public class AsyncHttpClient {

    private static final Logger logger = LoggerFactory.getLogger(AsyncHttpClient.class);
    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 10000;
    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 1000;
    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (180 * 1000);

    public static CloseableHttpAsyncClient getAsyncHttpClient() {
        PoolingNHttpClientConnectionManager connectionManager = null;
        try {
            connectionManager = new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor(IOReactorConfig.DEFAULT));
        } catch (IOReactorException e) {
            logger.info(Utils.getStackTrace(e));
        }
        connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);


        IdleConnectionMonitorThread.getIdleConnectionMonitorThread().addAsyncConnMgr(connectionManager);

        RequestConfig config = RequestConfig.custom().setConnectTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
                .setConnectionRequestTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
                .setSocketTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
                .setStaleConnectionCheckEnabled(true)
                .build();

        CloseableHttpAsyncClient httpClient = HttpAsyncClientBuilder
                .create().setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config).build();
        return httpClient;
    }

    public static CloseableHttpAsyncClient getMetricsAsyncHttpClient(MetricRegistry metricRegistry, HttpRequestMetrics httpRequestMetrics) {
        PoolingNHttpClientConnectionManager connectionManager = null;
        try {
            connectionManager = new PoolingNHttpClientConnectionManager(new DefaultConnectingIOReactor(IOReactorConfig.DEFAULT));
        } catch (IOReactorException e) {
            logger.info(Utils.getStackTrace(e));
        }
        connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);

        IdleConnectionMonitorThread.getIdleConnectionMonitorThread().addAsyncConnMgr(connectionManager);

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
                .build();
        CloseableHttpAsyncClient httpClient = new InstrumentedNHttpClientBuilder(metricRegistry, httpRequestMetrics).setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config).build();
        return httpClient;
    }
}
