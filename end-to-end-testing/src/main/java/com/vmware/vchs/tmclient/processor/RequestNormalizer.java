/*
 * *****************************************************
 *  Copyright VMware, Inc. 2014.   All Rights Reserved.
 *  ******************************************************
 */

package com.vmware.vchs.tmclient.processor;

import com.google.common.base.MoreObjects;
import com.vmware.vchs.microservices.model.MicroServiceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convert a request from api_head to normalized micro service request
 */
public class RequestNormalizer {
    private static Logger logger = LoggerFactory.getLogger(RequestNormalizer.class);
    private static String URI_PROPERTY = "uri";
    private static String REQUEST_METHOD_PROPERTY = "request-method";

    public static String requestToString(final MicroServiceRequest request) {
        if (request == null) {
            return "MicroServiceRequest{NULL}";
        }
        MoreObjects.ToStringHelper props_helper = MoreObjects.toStringHelper("Properties");
        for (String prop : request.getPropertyNames()) {
            props_helper.add(prop, request.getProperty(prop));
        }

        return MoreObjects.toStringHelper("MicroServiceRequest")
                .add("api_version", request.getApiVersion())
                .add("request_id", request.getRequestID())
                .add("service_id", request.getServiceID())
                .add("resource_id", request.getResourceID())
                .add("resource_type", request.getResourceType())
                .add("resource_action", request.getOperationName())
                .add("properties", props_helper.toString())
                .toString();
    }

    public static boolean isRequestNormalized(final MicroServiceRequest request) {
        return request != null && request.getResourceType() != null && request.getOperationName() != null;
    }

    /**
     * normalize request from api_head to what GW expected.
     *
     * @param request request from api_head
     * @return success normalized
     */
    public static boolean normalizeRequest(MicroServiceRequest request) {
        if (request == null) {
            return false;
        }
        logger.debug("Normalize request from api_head: {}", requestToString(request));
        final String uri = request.getProperty(URI_PROPERTY);
        final String method = request.getProperty(REQUEST_METHOD_PROPERTY);
        if (uri == null || method == null) {
            logger.warn("Failed to get uri and method from request.");
            return false;
        }

        final String[] pathFragments = uri.split("/");
        if (pathFragments.length < 3) {
            logger.warn("Failed to parse request uri: {}", uri);
            return false;
        }
        request.withServiceID(pathFragments[1]);
        request.withApiVersion(pathFragments[2]);
        request.withResourceType(pathFragments[3]);

        if (pathFragments.length > 4) {
            request.withResourceID(pathFragments[4]);
        }

        switch (method) {
            case "post":
                request.withOperationName("create");
                break;
            case "delete":
                request.withOperationName("delete");
                break;
            case "get":
                if (pathFragments.length > 4) {
                    request.withOperationName("get");
                } else {
                    request.withOperationName("list");
                }
                break;
            default:
                logger.warn("Unknown operation {}", method);
                break;
        }
        return true;
    }
}
