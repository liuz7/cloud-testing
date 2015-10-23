package com.vmware.vchs.load.generator.result.collector;

import com.vmware.vchs.performance.model.TestRun;
import com.vmware.vchs.test.config.Configuration;

/**
 * Created by liuda on 6/1/15.
 */
public interface Collectable {
    void collect();

    void setTestRun(TestRun testRun);

    void setConfiguration(Configuration configuration);
}
