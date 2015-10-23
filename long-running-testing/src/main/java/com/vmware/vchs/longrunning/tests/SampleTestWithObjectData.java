package com.vmware.vchs.longrunning.tests;

import com.vmware.vchs.longrunning.base.State;
import com.vmware.vchs.longrunning.base.TestCase;
import com.vmware.vchs.longrunning.core.TestStates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by sjun on 8/27/15.
 */
@TestCase
public class SampleTestWithObjectData extends SampleBaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(SampleTestWithObjectData.class);

    @State(name="initialized", verification=true)
    public String initialized() {
        assertThat(data.name).isEqualTo("me");
        assertThat(data.size).isEqualTo(10);
        return TestStates.FINISHED_STATE;
    }

    @State(name=TestStates.ERROR_STATE)
    public String recover() {
        data.name = "me";
        data.size = 10;
        return "initialized";
    }
}
