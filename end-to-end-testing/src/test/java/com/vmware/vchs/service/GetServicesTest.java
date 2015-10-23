package com.vmware.vchs.service;

import com.vmware.vchs.InstanceBaseTest;
import com.vmware.vchs.base.BaseDataProvider;
import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.model.constant.PlanModel;
import com.vmware.vchs.model.portal.instance.Plan;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by georgeliu on 15/4/23.
 */
@Test
public class GetServicesTest extends InstanceBaseTest {

    @Test(dataProvider = "plan name", dataProviderClass = BaseDataProvider.class)
    public void testServicePlansAfterProvision(String planName) throws Exception {
        PlanModel planData = dbaasApi.getPlans().get(planName);
        assertThat(planData).isNotNull();
        DbaasInstance instance =builder.setVersion("mssql_2008R2").setPlan(planData.toPlan()).createInstanceWithRetry(getNamePrefix());
        Plan plan = instance.getInstanceResponse().getPlan();
        int diskSize = instance.getInstanceResponse().getDiskSize();
        assertThat(plan.getId()).isEqualTo(planData.getId());
        assertThat(plan.getMemory()).isEqualTo(planData.getMemory());
        assertThat(plan.getVcpu()).isEqualTo(planData.getCpu());
        assertThat(diskSize).isGreaterThanOrEqualTo(planData.getDisk());
    }

}
