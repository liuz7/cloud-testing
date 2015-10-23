package com.vmware.vchs.longrunning.tests;

import com.vmware.vchs.longrunning.base.State;
import com.vmware.vchs.longrunning.base.TestCase;
import com.vmware.vchs.longrunning.base.TestData;
import com.vmware.vchs.longrunning.core.TestStates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by sjun on 8/27/15.
 */
@TestCase
public class SampleTestWithHashMapData {
    protected static final Logger logger = LoggerFactory.getLogger(SampleTestWithHashMapData.class);

    @TestData
    private HashMap<String, String> data;

    @State(name=TestStates.INIT_STATE)
    public String init() {
        data.put("aa", "bb");
        return "initialized";
    }


    @State(name="initialized", verification=true)
    public String initialized() {
        assertThat(data.get("aa")).isEqualTo("bb");
        return TestStates.FINISHED_STATE;
    }
}
