package com.vmware.vchs.common.bucket;

import java.util.concurrent.TimeUnit;

public class PeriodicRefillStrategy implements RefillStrategy {

    private final long numTokens;
    private final long interval;
    private long nextRefillTime;

    public PeriodicRefillStrategy(long numTokens, long interval, TimeUnit timeUnit) {
        this.numTokens = numTokens;
        this.interval = timeUnit.toNanos(interval);
        this.nextRefillTime = -1;
    }

    @Override
    public synchronized long refill() {
        long nowTime = System.nanoTime();

        if (nextRefillTime == -1) {
            nextRefillTime = nowTime + interval;
            return numTokens;
        }

        if (nowTime < nextRefillTime) {
            return 0;
        }

        long newTokens = ((nowTime - nextRefillTime) / interval + 1) * numTokens;
        nextRefillTime = nowTime + interval;

        return newTokens;
    }

    @Override
    public long getNextRefillTime() {

        return nextRefillTime;
    }
}
