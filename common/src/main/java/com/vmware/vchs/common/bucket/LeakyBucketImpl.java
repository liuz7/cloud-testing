package com.vmware.vchs.common.bucket;

import java.util.concurrent.TimeUnit;

public class LeakyBucketImpl implements LeakyBucket {

    private TokenBucket tokenBucket;

    public LeakyBucketImpl(long capacity, RefillStrategy refillStrategy) {
        tokenBucket = new TokenBucketBuilder().withCapacity(capacity).withRefillStrategy(refillStrategy).build();
    }

    public LeakyBucketImpl(long numRequests, TimeUnit timeUnit) {
        long durationMs = timeUnit.toMillis(1);
        long interval = durationMs / numRequests;
        RefillStrategy refillStrategy = new PeriodicRefillStrategy(1, interval, TimeUnit.MILLISECONDS);

        tokenBucket = new TokenBucketBuilder().withCapacity(1).withRefillStrategy(refillStrategy).build();
    }

    @Override
    public void consume() {
        tokenBucket.consume();
    }

    @Override
    public long consumeNonBlock() {
        return tokenBucket.consumeNonBlock();
    }
}
