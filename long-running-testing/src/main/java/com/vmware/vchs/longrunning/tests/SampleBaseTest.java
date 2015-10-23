package com.vmware.vchs.longrunning.tests;

import com.vmware.vchs.longrunning.base.State;
import com.vmware.vchs.longrunning.base.TestData;
import com.vmware.vchs.longrunning.core.TestStates;

/**
 * Created by sjun on 8/27/15.
 */
public class SampleBaseTest {
    public static class Data {
        public String name;
        public int size;
    }

    @TestData
    protected Data data;

    @State(name= TestStates.INIT_STATE)
    public String init() {
        data.name = "error name";
        data.size = 10;
        return "initialized";
    }
}
