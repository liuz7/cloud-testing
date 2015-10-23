package com.vmware.vchs.common.bucket;

import java.util.concurrent.TimeUnit;

public class LeakyBucketBuilder {

    private long capacity = 1;
    private RefillStrategy refillStrategy = null;
    private long numRequests = -1;
    private TimeUnit timeUnit;

    public LeakyBucketBuilder() {

    }

    public LeakyBucketBuilder withCapacity(long capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }

        this.capacity = capacity;

        return this;
    }

    public LeakyBucketBuilder withRefillStrategy(RefillStrategy refillStrategy) {
        this.refillStrategy = refillStrategy;

        return this;
    }

    public LeakyBucketBuilder withRefillStrategy(long numTokens, long interval, TimeUnit timeUnit) {
        this.refillStrategy = new PeriodicRefillStrategy(numTokens, interval, timeUnit);

        return this;
    }

    public LeakyBucketBuilder withNumRequests(long numRequests) {
        this.numRequests = numRequests;

        return this;
    }

    public LeakyBucketBuilder withTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;

        return this;
    }

    public LeakyBucket build() {
        return new LeakyBucketImpl(capacity,refillStrategy);
//        return new LeakyBucketImpl(numRequests, timeUnit);
    }
}
