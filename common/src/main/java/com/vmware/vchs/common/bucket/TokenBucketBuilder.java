package com.vmware.vchs.common.bucket;

import java.util.concurrent.TimeUnit;

public class TokenBucketBuilder {

    private long capacity = -1;
    private RefillStrategy refillStrategy = null;
    private long numRequests = -1;
    private TimeUnit timeUnit;

    public TokenBucketBuilder() {

    }

    public TokenBucketBuilder withCapacity(long capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }

        this.capacity = capacity;

        return this;
    }

    public TokenBucketBuilder withRefillStrategy(RefillStrategy refillStrategy) {
        this.refillStrategy = refillStrategy;

        return this;
    }

    public TokenBucketBuilder withRefillStrategy(long numTokens, long interval, TimeUnit timeUnit) {
        this.refillStrategy = new PeriodicRefillStrategy(numTokens, interval, timeUnit);

        return this;
    }
    
    public TokenBucketBuilder withNumRequests(long numRequests) {
        if (numRequests <= 0) {
            throw new IllegalArgumentException();
        }
        
        this.numRequests = numRequests;

        return this;
    }

    public TokenBucketBuilder withTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;

        return this;
    }
    
    public TokenBucketBuilder withBurst(long burst) {
        this.capacity = burst;

        return this;
    }

    public TokenBucket build() {
        if (numRequests != -1) {
            long durationMs = timeUnit.toMillis(1);
            long interval = durationMs / numRequests;
            refillStrategy = new PeriodicRefillStrategy(1, interval, TimeUnit.MILLISECONDS);
        }
        
        return new TokenBucketImpl(capacity, refillStrategy);
    }
}
