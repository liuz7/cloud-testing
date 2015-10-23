package com.vmware.vchs.common.utils.exception;

/**
 * The Retry exception for retry task.
 */
public class RetryException extends RuntimeException {

    public RetryException() {

    }

    public RetryException(String message) {
        super(message);
    }

    public RetryException(Throwable cause) {
        super(cause);
    }

    public RetryException(String message, Throwable cause) {
        super(message, cause);
    }
}
