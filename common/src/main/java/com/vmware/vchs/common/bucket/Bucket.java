package com.vmware.vchs.common.bucket;

public interface Bucket {

    void consume();

    long consumeNonBlock();
}
