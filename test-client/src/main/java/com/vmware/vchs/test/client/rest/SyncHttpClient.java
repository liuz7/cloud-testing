package com.vmware.vchs.test.client.rest;

import com.codahale.metrics.MetricRegistry;
import com.vmware.vchs.test.client.rest.metrics.InstrumentedHttpClients;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;


/**
 * Created by georgeliu on 14/11/19.
 */
public class SyncHttpClient {

    private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 1000;
    private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 500;
    private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (180 * 1000);
    
    public static CloseableHttpClient getHttpClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);

        IdleConnectionMonitorThread.getIdleConnectionMonitorThread().addSyncConnMgr(connectionManager);

        RequestConfig config = RequestConfig.custom().setConnectTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
                                                     .setConnectionRequestTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
                                                     .setSocketTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS)
                                                     .setStaleConnectionCheckEnabled(true)
                                                     .build();

        CloseableHttpClient defaultHttpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config).build();

        return defaultHttpClient;
    }

    public static CloseableHttpClient getMetricsHttpClient(MetricRegistry metricRegistry) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);

        IdleConnectionMonitorThread.getIdleConnectionMonitorThread().addSyncConnMgr(connectionManager);

        RequestConfig config = RequestConfig.custom().setConnectTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS).build();
        CloseableHttpClient defaultHttpClient = InstrumentedHttpClients.custom(metricRegistry)
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config).build();
        return defaultHttpClient;
    }
}
