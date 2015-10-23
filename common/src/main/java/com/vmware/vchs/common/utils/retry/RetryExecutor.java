package com.vmware.vchs.common.utils.retry;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by sjun on 8/20/15.
 */
public interface RetryExecutor {
    RetryExecutor setRetries(int retries);
    RetryExecutor setWaitTimes(List<Integer> waitTimes);
    RetryExecutor setStrategies(List<RetryStrategy> strategies);
    <T> T execute(Callable<T> retryTask, String taskName) throws Exception;
    void execute(Runnable retryTask, String taskName) throws Exception;
}
