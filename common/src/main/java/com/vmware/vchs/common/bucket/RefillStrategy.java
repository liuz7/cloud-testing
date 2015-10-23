package com.vmware.vchs.common.bucket;

public interface RefillStrategy {

    long refill();

    long getNextRefillTime();
}
