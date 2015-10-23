/*
 *
 *  * ******************************************************
 *  * Copyright VMware, Inc. 2014.   All Rights Reserved.
 *  * ******************************************************
 *
 */

package com.vmware.vchs.test.client.model.portal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.vmware.vchs.common.utils.exception.PortalError;
import com.vmware.vchs.common.utils.exception.RestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.io.InputStreamReader;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The RestResponseErrorHandler to override the hasError method for rest template.
 */
public class PortalResponseErrorHandler implements ResponseErrorHandler {

    private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(PortalResponseErrorHandler.class);

    public boolean hasError(ClientHttpResponse response) throws IOException {
        return errorHandler.hasError(response);
    }

    public void handleError(ClientHttpResponse response) throws IOException {
        String body = CharStreams.toString(new InputStreamReader(response.getBody(), Charsets.UTF_8));
        JsonNode rootNode = this.objectMapper.readTree(body);
        JsonNode code = checkNotNull(rootNode.findValue("code"), "Error code should not be null");
        JsonNode message = checkNotNull(rootNode.findValue("message"), "Error message should not be null");
        PortalError portalError = new PortalError();
        portalError.setCode(code.asText());
        portalError.setMessage(message.asText());
        RestException exception = new RestException();
        exception.setStatusCode(response.getRawStatusCode());
        exception.setError(portalError);
        logger.info(portalError.toString());
        throw exception;
    }

}
