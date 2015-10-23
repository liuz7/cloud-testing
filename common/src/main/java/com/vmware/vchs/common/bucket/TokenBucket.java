package com.vmware.vchs.common.bucket;

public interface TokenBucket extends Bucket {

    public void consume(long num);

    public long consumeNonBlock(long num);
}
