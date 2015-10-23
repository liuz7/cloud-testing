package com.vmware.vchs.common.utils;

import java.util.concurrent.*;

/**
 * Created by georgeliu on 15/3/19.
 */
public class ThreadTask {

    private ExecutorService executor;

    public ThreadTask(ExecutorService executor) {
        this.executor = executor;
    }

    public ThreadTask() {
        this.executor = Executors.newCachedThreadPool(new DaemonThreadFactory("ThreadTask"));
    }

    public void runWithTimeout(final Runnable runnable, long timeout, TimeUnit timeUnit) throws Exception {
        runWithTimeout(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                runnable.run();
                return null;
            }
        }, timeout, timeUnit);
    }

    public <T> T runWithTimeout(Callable<T> callable, long timeout, TimeUnit timeUnit) throws Exception {
        final Future<T> future = this.executor.submit(callable);
        try {
            return future.get(timeout, timeUnit);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw e;
        } catch (ExecutionException e) {
            throw e;
        }
    }

    public <T> Future<T> submitTask(Callable<T> callable) throws Exception {
        final Future<T> future = this.executor.submit(callable);
        return future;
    }
}
