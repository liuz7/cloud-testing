package com.vmware.vchs.common.utils.exception;

/**
 * The Rest exception for rest client.
 */

import com.google.common.base.MoreObjects;


public class RestException extends RuntimeException {

    private int statusCode;
    private Object error;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public RestException() {

    }

    public RestException(String message) {
        super(message);
    }

    public RestException(Throwable cause) {
        super(cause);
    }

    public RestException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("StatusCode", statusCode)
                .add("Error", error)
                .toString();
    }
}
