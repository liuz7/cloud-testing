package com.vmware.vchs.constant;

import java.lang.*;
import java.util.EnumMap;

/**
 * Created by georgeliu on 14/11/3.
 */
public class ErrorMap {

    private static EnumMap<Error, Integer> errorMap;

    static {
        errorMap = new EnumMap<Error, Integer>(Error.class);
        errorMap.put(Error.ALREADY_EXISTS, 400);
        errorMap.put(Error.ALREADY_IN_DESIRED_STATE, 400);
        errorMap.put(Error.FEATURE_IN_USE, 400);
        errorMap.put(Error.INTERNAL_SERVER_ERROR, 500);
        errorMap.put(Error.INVALID_ARGUMENT, 400);
        errorMap.put(Error.INVALID_CONFIGURATION_CHANGE, 400);
        errorMap.put(Error.INVALID_ELEMENT_CONFIGURATION, 400);
        errorMap.put(Error.INVALID_ELEMENT_TYPE, 400);
        errorMap.put(Error.INVALID_REQUEST, 400);
        errorMap.put(Error.RESOURCE_NOT_FOUND, 404);
        errorMap.put(Error.OPERATION_NOT_ALLOWED, 400);
        errorMap.put(Error.RESOURCE_BUSY, 400);
        errorMap.put(Error.RESOURCE_IN_USE, 400);
        errorMap.put(Error.RESOURCE_INACCESSIBLE, 400);
        errorMap.put(Error.SERVICE_NOT_FOUND, 404);
        errorMap.put(Error.SERVICE_UNAVAILABLE, 503);
        errorMap.put(Error.TIMED_OUT, 504);
        errorMap.put(Error.UNABLE_TO_ALLOCATE_RESOURCE, 500);
        errorMap.put(Error.UNAUTHENTICATED, 401);
        errorMap.put(Error.UNAUTHORIZED, 403);
        errorMap.put(Error.UNIMPLEMENTED, 501);
        errorMap.put(Error.UNSUPPORTED, 400);
    }

    public static int getErrorCode(Error error) {
        return errorMap.get(error);
    }

}
