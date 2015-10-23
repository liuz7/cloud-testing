/*
 *
 *  * ******************************************************
 *  * Copyright VMware, Inc. 2014.   All Rights Reserved.
 *  * ******************************************************
 *
 */

package com.vmware.vchs.test.client.rest;

import com.codahale.metrics.MetricRegistry;
import com.vmware.vchs.common.utils.Utils;
import com.vmware.vchs.test.client.rest.async.AsyncHttpClient;
import com.vmware.vchs.test.client.rest.metrics.HttpRequestMetrics;
import com.vmware.vchs.test.config.Configuration;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by georgeliu on 14/11/20.
 */
public class RestClient {

    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);
    protected AsyncRestTemplate asyncRestTemplate;
    protected UriBuilder uriBuilder;
    protected RequestInfo requestInfo;
    private HttpHeaders httpHeaders;
    private Configuration configuration;

    public RestClient(MetricRegistry metricRegistry, HttpRequestMetrics httpRequestMetrics, String baseUrl) {
        this.asyncRestTemplate = new AsyncRestTemplate(getMetricsAsyncHttpClientFactory(metricRegistry, httpRequestMetrics), getSyncHttpClientFactory());
        init(baseUrl);
    }

    public RestClient(String baseUrl) {
        if (isSSL(baseUrl)) {
            this.asyncRestTemplate = new AsyncRestTemplate(getAsyncHttpClientFactory(), getSSLHttpClientFactory());
        } else {
            this.asyncRestTemplate = new AsyncRestTemplate(getAsyncHttpClientFactory(), getSyncHttpClientFactory());
        }
        init(baseUrl);
    }

    public RestClient(String username, String password, String baseUrl) {
        if (isSSL(baseUrl)) {
            this.asyncRestTemplate = new AsyncRestTemplate(getAsyncHttpClientFactoryWithBasicAuth(username, password), getHttpClientFactoryWithBasicAuthAndSSL(username, password));
        } else {
            this.asyncRestTemplate = new AsyncRestTemplate(getAsyncHttpClientFactoryWithBasicAuth(username, password), getHttpClientFactoryWithBasicAuth(username, password));
        }
        init(baseUrl);
    }

    public RestClient(MetricRegistry metricRegistry, HttpRequestMetrics httpRequestMetrics) {
        this(metricRegistry, httpRequestMetrics, null);
    }

    public RestClient() {
        this.asyncRestTemplate = new AsyncRestTemplate(getAsyncHttpClientFactory(), getSyncHttpClientFactory());
        init(null);
    }

    public RestClient(String username, String password, Configuration configuration) {
        this.configuration = configuration;
        String baseUrl = this.configuration.getRest().getBaseUrl();
        if (isSSL(baseUrl)) {
            this.asyncRestTemplate = new AsyncRestTemplate(getAsyncHttpClientFactoryWithBasicAuth(username, password), getHttpClientFactoryWithBasicAuthAndSSL(username, password));
        } else {
            this.asyncRestTemplate = new AsyncRestTemplate(getAsyncHttpClientFactoryWithBasicAuth(username, password), getHttpClientFactoryWithBasicAuth(username, password));
        }
        init(null);
    }

    public RestClient(Configuration configuration) {
        this.configuration = configuration;
        String baseUrl = this.configuration.getRest().getBaseUrl();
        if (!isSSL(baseUrl)) {
            this.asyncRestTemplate = new AsyncRestTemplate(getAsyncHttpClientFactory(), getSyncHttpClientFactory());
        } else {
            this.asyncRestTemplate = new AsyncRestTemplate(getAsyncHttpClientFactory(), getSSLHttpClientFactory());
        }
        init(null);
    }

    private boolean isSSL(String urlString) {
        if (!urlString.startsWith("http")) {
            urlString = "http://" + urlString;
        }
        String protocol = null;
        try {
            protocol = new URL(urlString).getProtocol();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return protocol.equalsIgnoreCase("https");
    }

    private void init(String baseUrl) {
        if (baseUrl == null) {
            this.uriBuilder = new UriBuilder(this.configuration);
        } else {
            this.uriBuilder = new UriBuilder(baseUrl);
        }
        this.requestInfo = new RequestInfo();
        checkNotNull(this.asyncRestTemplate);
        this.asyncRestTemplate.getMessageConverters().clear();
        this.asyncRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        this.httpHeaders = checkNotNull(getJsonHttpHeaders());
    }

    public void setErrorHandler(ResponseErrorHandler responseErrorHandler) {
        this.asyncRestTemplate.setErrorHandler(responseErrorHandler);
    }

    public ResponseErrorHandler getErrorHandler() {
        return this.asyncRestTemplate.getErrorHandler();
    }

    public void setMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        this.asyncRestTemplate.setMessageConverters(messageConverters);
    }

    public List<HttpMessageConverter<?>> getMessageConverters() {
        return this.asyncRestTemplate.getMessageConverters();
    }

    public void loadConverters(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends AbstractHttpMessageConverter>> subTypes = reflections.getSubTypesOf(AbstractHttpMessageConverter.class);
        for (Class<? extends AbstractHttpMessageConverter> subType : subTypes) {
            try {
                getMessageConverters().add(subType.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                logger.info(Utils.getStackTrace(e));
            }
        }
    }

    protected ClientHttpRequestFactory getSSLHttpClientFactory() {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        return requestFactory;
    }

    protected ClientHttpRequestFactory getSyncHttpClientFactory() {
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(SyncHttpClient.getHttpClient());
        return requestFactory;
    }

    protected ClientHttpRequestFactory getMetricsSyncHttpClientFactory(MetricRegistry metricRegistry) {
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(SyncHttpClient.getMetricsHttpClient(metricRegistry));
        return requestFactory;
    }

    protected ClientHttpRequestFactory getHttpClientFactoryWithBasicAuth(String username, String password) {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(SyncHttpClient.getHttpClient());
        return requestFactory;
    }

    protected ClientHttpRequestFactory getHttpClientFactoryWithBasicAuthAndSSL(String username, String password) {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        ClientHttpRequestFactory requestFactory = getSSLHttpClientFactory();
        return requestFactory;
    }

    protected AsyncClientHttpRequestFactory getAsyncHttpClientFactory() {
        HttpComponentsAsyncClientHttpRequestFactory asyncRequestFactory = new HttpComponentsAsyncClientHttpRequestFactory(AsyncHttpClient.getAsyncHttpClient());
        return asyncRequestFactory;
    }

    protected AsyncClientHttpRequestFactory getMetricsAsyncHttpClientFactory(MetricRegistry metricRegistry, HttpRequestMetrics httpRequestMetrics) {
        HttpComponentsAsyncClientHttpRequestFactory asyncRequestFactory = new HttpComponentsAsyncClientHttpRequestFactory(AsyncHttpClient.getMetricsAsyncHttpClient(metricRegistry, httpRequestMetrics));
        return asyncRequestFactory;
    }

    protected AsyncClientHttpRequestFactory getAsyncHttpClientFactoryWithBasicAuth(String username, String password) {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        AsyncClientHttpRequestFactory requestFactory = new HttpComponentsAsyncClientHttpRequestFactory(AsyncHttpClient.getAsyncHttpClient());
        return requestFactory;
    }

    protected HttpEntity createHttpEntity() {
        return createHttpEntity(null);
    }

    protected HttpEntity createHttpEntity(Object request) {
        return new HttpEntity(request, this.httpHeaders);
    }

    public <T> ListenableFuture<ResponseEntity<T>> getForAsyncEntity(String path, Class<T> responseType) {
        HttpEntity httpEntity = createHttpEntity();
        URI targetUrl = this.uriBuilder.buildPath(path);
        this.requestInfo.setMethod(HttpMethod.GET.name());
        this.requestInfo.setPath(targetUrl.toString());
        return this.asyncRestTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, responseType);
    }

    public <T> ListenableFuture<ResponseEntity<T>> getForAsyncEntity(String path, Map<String, Object> query, Class<T> responseType) {
        HttpEntity httpEntity = createHttpEntity();
        URI targetUrl = this.uriBuilder.buildPathWithQueryParams(path, query);
        this.requestInfo.setMethod(HttpMethod.GET.name());
        this.requestInfo.setPath(targetUrl.toString());
        return this.asyncRestTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, responseType);
    }

    public <T> ListenableFuture<ResponseEntity<T>> postForAsyncEntity(String path, Object request, Class<T> responseType) {
        HttpEntity httpEntity = createHttpEntity(request);
        URI targetUrl = this.uriBuilder.buildPath(path);
        this.requestInfo.setMethod(HttpMethod.POST.name());
        this.requestInfo.setPath(targetUrl.toString());
        return this.asyncRestTemplate.exchange(targetUrl, HttpMethod.POST, httpEntity, responseType);
    }

    public <T> ListenableFuture<ResponseEntity<T>> postForAsyncEntity(String path, Map<String, Object> query, Object request, Class<T> responseType) {
        HttpEntity httpEntity = createHttpEntity(request);
        URI targetUrl = this.uriBuilder.buildPathWithQueryParams(path, query);
        this.requestInfo.setMethod(HttpMethod.POST.name());
        this.requestInfo.setPath(targetUrl.toString());
        return this.asyncRestTemplate.exchange(targetUrl, HttpMethod.POST, httpEntity, responseType);
    }

    public <T> ListenableFuture<ResponseEntity<T>> deleteForAsyncEntity(String path, Class<T> responseType) {
        HttpEntity httpEntity = createHttpEntity();
        URI targetUrl = this.uriBuilder.buildPath(path);
        this.requestInfo.setMethod(HttpMethod.DELETE.name());
        this.requestInfo.setPath(targetUrl.toString());
        return this.asyncRestTemplate.exchange(targetUrl, HttpMethod.DELETE, httpEntity, responseType);
    }

    public String getForString(String path) {
        return getForEntity(path, String.class).getBody();
    }

    public <T> T getForObject(String path, Class<T> responseType) {
        return getForEntity(path, responseType).getBody();
    }

    public <T> T getForObject(String path, Map<String, Object> query, Class<T> responseType) {
        return getForEntity(path, query, responseType).getBody();
    }

    public <T> ResponseEntity<T> getForEntity(String path, Class<T> responseType) {
        HttpEntity httpEntity = createHttpEntity();
        URI targetUrl = this.uriBuilder.buildPath(path);
        return this.asyncRestTemplate.getRestOperations().exchange(targetUrl, HttpMethod.GET, httpEntity, responseType);
    }


    public <T> ResponseEntity<T> getForEntity(String path, Map<String, Object> query, Class<T> responseType) {
        HttpEntity httpEntity = createHttpEntity();
        URI targetUrl = this.uriBuilder.buildPathWithQueryParams(path, query);
        return this.asyncRestTemplate.getRestOperations().exchange(targetUrl, HttpMethod.GET, httpEntity, responseType);
    }

    public String postForString(String path, String request) {
        return postForEntity(path, request, String.class).getBody();
    }

    public <T> T postForObject(String path, Object request, Class<T> responseType) {
        return postForEntity(path, request, responseType).getBody();
    }

    public <T> T postForObject(String path, Map<String, Object> query, Object request, Class<T> responseType) {
        return postForEntity(path, query, request, responseType).getBody();
    }

    public <T> ResponseEntity<T> postForEntity(String path, Class<T> responseType) {
        HttpEntity httpEntity = createHttpEntity();
        URI targetUrl = this.uriBuilder.buildPath(path);
        return this.asyncRestTemplate.getRestOperations().exchange(targetUrl, HttpMethod.POST, httpEntity, responseType);
    }

    public <T> ResponseEntity<T> postForEntity(String path, Object request, Class<T> responseType) {
        HttpEntity httpEntity = createHttpEntity(request);
        URI targetUrl = this.uriBuilder.buildPath(path);
        return this.asyncRestTemplate.getRestOperations().exchange(targetUrl, HttpMethod.POST, httpEntity, responseType);
    }

    public <T> ResponseEntity<T> postForEntity(String path, Map<String, Object> query, Object request, Class<T> responseType) {
        HttpEntity httpEntity = createHttpEntity(request);
        URI targetUrl = this.uriBuilder.buildPathWithQueryParams(path, query);
        return this.asyncRestTemplate.getRestOperations().exchange(targetUrl, HttpMethod.POST, httpEntity, responseType);
    }

    public void put(String path, Object request) throws RestClientException {
        HttpEntity httpEntity = createHttpEntity(request);
        URI targetUrl = this.uriBuilder.buildPath(path);
        this.asyncRestTemplate.getRestOperations().put(targetUrl, httpEntity);
    }

    public <T> ResponseEntity<T> putForEntity(String path, Object request, Class<T> responseType) throws RestClientException {
        HttpEntity httpEntity = createHttpEntity(request);
        URI targetUrl = this.uriBuilder.buildPath(path);
        return this.asyncRestTemplate.getRestOperations().exchange(targetUrl, HttpMethod.PUT, httpEntity, responseType);
    }

    public <T> T putForObject(String path, Object request, Class<T> responseType) throws RestClientException {
        return putForEntity(path, request, responseType).getBody();
    }


    public <T> ResponseEntity<T> putForEntity(String path, Object request, Map<String, Object> query, Class<T> responseType) throws RestClientException {
        HttpEntity httpEntity = createHttpEntity(request);
        URI targetUrl = this.uriBuilder.buildPathWithQueryParams(path, query);
        return this.asyncRestTemplate.getRestOperations().exchange(targetUrl, HttpMethod.PUT, httpEntity, responseType);
    }

    public <T> T putForObject(String path, Object request, Map<String, Object> query, Class<T> responseType) throws RestClientException {
        return putForEntity(path, request, query, responseType).getBody();
    }


    public <T> ResponseEntity<T> deleteForEntity(String path, Class<T> responseType) {
        HttpEntity httpEntity = createHttpEntity();
        URI targetUrl = this.uriBuilder.buildPath(path);
        return this.asyncRestTemplate.getRestOperations().exchange(targetUrl, HttpMethod.DELETE, httpEntity, responseType);
    }

    public <T> T deleteForObject(String path, Class<T> responseType) {
        return deleteForEntity(path, responseType).getBody();
    }

    protected HttpHeaders getJsonHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return headers;
    }

    public HttpHeaders getHttpHeaders() {
        return this.httpHeaders;
    }

    public void setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public String getBaseUrl() {
        return uriBuilder.getBaseUrl();
    }

    public void setBaseUrl(String baseUrl) {
        uriBuilder.setBaseUrl(baseUrl);
    }

    public RequestInfo getRequestInfo() {
        return this.requestInfo;
    }
}