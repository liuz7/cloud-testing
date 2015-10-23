package com.vmware.vchs.performance;

import com.vmware.vchs.InstanceBaseTest;
import org.springframework.test.context.ActiveProfiles;
import org.testng.annotations.Test;

/**
 * Created by georgeliu on 14/11/24.
 */
@Test(groups = {"performance"})
@ActiveProfiles("prod")
public class PerformanceBaseTest extends InstanceBaseTest {

}
