/*
 *
 *  * ******************************************************
 *  * Copyright VMware, Inc. 2014.   All Rights Reserved.
 *  * ******************************************************
 *
 */

package com.vmware.vchs.test.client.rest;

import com.vmware.vchs.test.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The uri builder to generate the uri passed to rest client.
 */
public class UriBuilder {

    protected String baseUrl;
    private static final Logger logger = LoggerFactory.getLogger(UriBuilder.class);

    public UriBuilder(Configuration configuration) {
        this.baseUrl = configuration.getRest().getBaseUrl();
        checkUrl();
    }

    public UriBuilder(String url) {
        this.baseUrl = url;
        checkUrl();
    }

    private void checkUrl() {
        if (!this.baseUrl.startsWith("http")) {
            this.baseUrl = "http://" + this.baseUrl;
        }
        checkNotNull(getBaseUrl());
        logger.info("The base url: " + this.getBaseUrl());
    }

    public URI buildPath(String path) {
        URI targetUrl = UriComponentsBuilder.fromHttpUrl(this.baseUrl)
                .path(path)
                .build()
                .toUri();
        return targetUrl;
    }

    public URI buildPathWithQueryParams(String path, Map<String, Object> query) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(this.baseUrl)
                .path(path);
        for (Map.Entry<String, Object> entry : query.entrySet()) {
            uriComponentsBuilder.queryParam(entry.getKey(), entry.getValue());
        }
        return uriComponentsBuilder.build().toUri();
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

}
