package com.vmware.vchs.common.utils.retry;

/**
 * Created by sjun on 8/20/15.
 */
public interface RetryStrategy {
    enum RetryStrategyResult {
        Handled, // Exception Handled, could retry the task again
        NotHandled, // Exception Not Handled, no effect to the result and other strategies result,
        Error, // Error, can not retry the task any more
        TimeOut, //Timeout, can not retry the task any more
    }
    RetryStrategyResult handleException(Exception e, long executedSeconds);
}
