package com.vmware.vchs.common.utils.exception;

/**
 * Created by georgeliu on 15/4/30.
 */
public class FailException extends RuntimeException {

    public FailException() {

    }

    public FailException(String message) {
        super(message);
    }

    public FailException(Throwable cause) {
        super(cause);
    }

    public FailException(String message, Throwable cause) {
        super(message, cause);
    }
}
