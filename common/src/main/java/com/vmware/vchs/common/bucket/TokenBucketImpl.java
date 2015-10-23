package com.vmware.vchs.common.bucket;

import java.util.concurrent.TimeUnit;

public class TokenBucketImpl implements TokenBucket {

    private final long capacity;
    private final RefillStrategy refillStrategy;
    private long remainder = 0;

    public TokenBucketImpl(long capacity, RefillStrategy refillStrategy) {
        if (capacity <= 0 || refillStrategy == null) {
            throw new IllegalArgumentException();
        }

        this.capacity = capacity;
        this.refillStrategy = refillStrategy;
    }

    @Override
    public synchronized void consume() {

        consume(1);
    }

    @Override
    public synchronized void consume(long num) {
        if (num <= 0) {
            throw new IllegalArgumentException();
        }

        long remain = 0;
        if (num > capacity) {
            remain = num - capacity;
            num = capacity;
        }

        while (true) {
            if (tryConsume(num)) {
                break;
            }

            long nowTime = System.nanoTime();
            long refillTime = refillStrategy.getNextRefillTime();
            try {
                TimeUnit.NANOSECONDS.sleep(refillTime - nowTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (remain > 0) {
            consume(remain);
        }
    }

    @Override
    public synchronized long consumeNonBlock() {

        return consumeNonBlock(1);
    }

    @Override
    public synchronized long consumeNonBlock(long num) {
        if (num <= 0) {
            throw new IllegalArgumentException();
        }

        long newTokens = Math.min(capacity, refillStrategy.refill());
        remainder = Math.min(capacity, remainder + newTokens);
        long out = Math.min(num, remainder);
        remainder -= out;

        return out;
    }

    private synchronized boolean tryConsume(long num) {
        if (num <= 0 || num > capacity) {
            throw new IllegalArgumentException();
        }

        long newTokens = Math.min(capacity, refillStrategy.refill());
        remainder = Math.min(capacity, remainder + newTokens);

        if (num <= remainder) {
            remainder -= num;
            return true;
        }

        return false;
    }

    private synchronized boolean tryConsume() {
        return tryConsume(1);
    }

}
