package com.vmware.vchs.common.utils.retry;

import com.google.common.collect.Lists;
import com.vmware.vchs.common.utils.exception.RetryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by sjun on 7/21/15.
 */
public class RetryExecutorImpl implements RetryExecutor {
    private static final Logger logger = LoggerFactory.getLogger(RetryExecutor.class);
    private static final int DEFAULT_NUMBER_OF_RETRIES = 46;
    private static final List<Integer> DEFAULT_WAIT_TIMES = Lists.newArrayList(4, 8, 16);

    private List<Integer> waitTimes;
    private int retries;
    private List<RetryStrategy> strategies;

    public RetryExecutorImpl() {
        this.waitTimes = DEFAULT_WAIT_TIMES;
        this.retries = DEFAULT_NUMBER_OF_RETRIES;
        this.strategies = null;
    }

    @Override
    public RetryExecutor setRetries(int retries) {
        this.retries = retries;
        return this;
    }

    @Override
    public RetryExecutor setWaitTimes(List<Integer> waitTimes) {
        this.waitTimes = waitTimes;
        return this;
    }

    @Override
    public RetryExecutor setStrategies(List<RetryStrategy> strategies) {
        this.strategies = strategies;
        return this;
    }

    @Override
    public void execute(Runnable retryTask, String taskName) throws Exception {
        execute(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                retryTask.run();
                return null;
            }
        }, taskName);
    }

    @Override
    public <T> T execute(Callable<T> retryTask, String taskName) throws Exception {
        T ret;
        long startTime = new Date().getTime() / 1000;
        int triedCount = 0;

        while (true) {
            try {
                ret = retryTask.call();
                break;
            } catch (Exception e) {
                triedCount++;
                long executedTime = new Date().getTime() / 1000 - startTime;

                RetryStrategy.RetryStrategyResult strategyResult = RetryStrategy.RetryStrategyResult.NotHandled;
                if (strategies != null) {
                    for (RetryStrategy strategy : strategies) {
                        RetryStrategy.RetryStrategyResult rsRet = strategy.handleException(e, executedTime);
                        if (!rsRet.equals(RetryStrategy.RetryStrategyResult.NotHandled)) {
                            strategyResult = rsRet;
                        }

                        if (rsRet.equals(RetryStrategy.RetryStrategyResult.Error) || rsRet.equals(RetryStrategy.RetryStrategyResult.TimeOut)) { // quit retry
                            break;
                        }
                    }
                }

                if (strategyResult.equals(RetryStrategy.RetryStrategyResult.Error) || strategyResult.equals(RetryStrategy.RetryStrategyResult.NotHandled)) {
                    throw e;
                }

                if (strategyResult.equals(RetryStrategy.RetryStrategyResult.TimeOut) || triedCount > retries) {
                    throw new RetryException(triedCount + " attempts to retry failed after " + executedTime + " seconds", e);
                }

                int sleepTime = waitTimes.get((triedCount >= waitTimes.size()) ? waitTimes.size() - 1 : triedCount - 1);
                logger.info("Sleeping " + sleepTime + " seconds. Tried " + triedCount + " times on " + taskName + ".");
                TimeUnit.SECONDS.sleep(sleepTime);
            }
        }
        long executedTime = new Date().getTime() / 1000 - startTime;
        logger.info(retryTask.toString() + " finished in " + executedTime + " seconds.");
        return ret;
    }

}
