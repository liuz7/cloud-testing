package com.vmware.vchs.common.utils;

import com.google.common.collect.Lists;
import com.vmware.vchs.common.utils.exception.PortalError;
import com.vmware.vchs.common.utils.exception.RestException;
import com.vmware.vchs.common.utils.exception.RetryException;
import com.vmware.vchs.common.utils.retry.RetryExecutor;
import com.vmware.vchs.common.utils.retry.RetryExecutorImpl;
import com.vmware.vchs.common.utils.retry.RetryStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * The retry task to retry the operation with wait time and max number of times.
 */
public class RetryTask {
    public static RetryExecutor createExecutor() {
        return new RetryExecutorImpl();
    }

    public static class ExceptionStrategy<T> implements RetryStrategy {
        private Class<T> exceptionClass;

        public ExceptionStrategy(Class<T> exceptionClass) {
            this.exceptionClass = exceptionClass;
        }

        public RetryStrategyResult handleException(Exception e, long executedSeconds) {
            if (e != null) {
                if (exceptionClass.isInstance(e)) {
                    return RetryStrategyResult.Handled;
                }
            }
            return RetryStrategyResult.NotHandled;
        }
    }

    public static class TimeoutStrategy implements RetryStrategy {
        private long totalSeconds;

        public TimeoutStrategy(long totalSeconds) {
            this.totalSeconds = totalSeconds;
        }

        public RetryStrategyResult handleException(Exception e, long executedSeconds) {
            if (totalSeconds < executedSeconds) {
                return RetryStrategyResult.TimeOut;
            }
            return RetryStrategyResult.NotHandled;
        }
    }

    public static class ExceptionFailedStrategy implements RetryStrategy {
        private static final int DEFAULT_FAILED_TRY_COUNT = 5;
        private int maxCount;
        private int triedCount;
        private List<String> errorMessages = new ArrayList<String>();

        public ExceptionFailedStrategy() {
            this(DEFAULT_FAILED_TRY_COUNT);
        }

        public ExceptionFailedStrategy(int maxCount) {
            this.maxCount = maxCount;
            this.triedCount = 0;
            errorMessages.add("Failed to authorize user");
            errorMessages.add("Service Unavailable");
        }

        public RetryStrategyResult handleException(Exception e, long executedSeconds) {
            if (shouldHandleException(e)) {
                triedCount++;
                if (triedCount >= maxCount) {
                    return RetryStrategyResult.Error;
                }
                return RetryStrategyResult.Handled;
            }
            return RetryStrategyResult.NotHandled;
        }

        private <T> T getCause(Throwable e, Class<T> causeClass) {
            while(e!=null) {
                if (e.getClass().equals(causeClass)) {
                    return (T)e;
                }
                e = e.getCause();
            }
            return null;
        }

        private boolean shouldHandleException(Exception e) {
            if (isNetworkTimeoutException(e)) return true;
            if (isAuthFailedException(e)) return true;
            return false;
        }

        private boolean isNetworkTimeoutException(Exception e) {
            return getCause(e, java.net.SocketTimeoutException.class) != null;
        }

        private boolean isAuthFailedException(Exception e) {
            RestException restException = getCause(e, RestException.class);
            if (restException != null) {
                Object innerError = restException.getError();
                if (innerError != null && innerError instanceof PortalError) {
                    PortalError portalError = (PortalError) innerError;
                    for (String error : errorMessages) {
                        if (portalError.getMessage().contains(error)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    protected static final Logger logger = LoggerFactory.getLogger(RetryTask.class);
    protected static final int defaultTimeout = 900;
    protected static final int DEFAULT_NUMBER_OF_RETRIES = 46;
    protected static final List<Integer> DEFAULT_WAIT_TIMES = Lists.newArrayList(4, 8, 16);

    private List<Integer> waitTimes;
    private int retries;
    private int timeout;

    public RetryTask() {
        this(DEFAULT_NUMBER_OF_RETRIES, DEFAULT_WAIT_TIMES);
    }

    public RetryTask(int retries, List<Integer> waitTimes) {
        this(retries, waitTimes, defaultTimeout);
    }

    public RetryTask(int retries, List<Integer> waitTimes, int timeout) {
        this.retries = retries;
        this.waitTimes = waitTimes;
        this.timeout = timeout;
    }

    private RetryExecutor getExecutor() {
        return createExecutor().setRetries(retries).setWaitTimes(waitTimes)
                .setStrategies(Lists.newArrayList(
                        new ExceptionStrategy<>(RetryException.class),
                        new ExceptionFailedStrategy(),
                        new TimeoutStrategy(timeout)
                ));
    }

    public void execute(Runnable retryTask) throws Exception {
        execute(retryTask, retryTask.toString());
    }

    public void execute(Runnable retryTask, String taskName) throws Exception {
        getExecutor().execute(retryTask, taskName);
    }

    public <T> T execute(Callable<T> retryTask) throws Exception {
        return execute(retryTask, retryTask.toString());
    }

    public <T> T execute(Callable<T> retryTask, String taskName) throws Exception {
        return getExecutor().execute(retryTask, taskName);
    }
}
