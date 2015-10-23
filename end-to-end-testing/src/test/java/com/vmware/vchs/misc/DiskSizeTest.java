package com.vmware.vchs.misc;

import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.common.utils.exception.PortalError;
import com.vmware.vchs.common.utils.exception.RestException;
import com.vmware.vchs.constant.PortalErrorMap;
import com.vmware.vchs.instance.InstanceTest;
import com.vmware.vchs.model.constant.PlanModel;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.model.portal.instance.Plan;
import com.vmware.vchs.model.portal.instance.UpdateInstanceRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.HttpURLConnection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * Created by georgeliu on 15/5/14.
 */
@Test(groups = {"disksize"})
public class DiskSizeTest extends InstanceTest {

    private DbaasInstance instance;
    public static final int MAX_DISK_SIZE = 179200;
    public static final int DISK_SIZE_UNIT = 25600;

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod(Method method) throws Exception {
        logger.info("Setup for disk size test method...");
        super.setUpMethod(method);
        PlanModel planData = dbaasApi.getPlans().get("Medium");
        Plan plan = planData.toPlan();
//        this.createInstanceRequest.setDiskSize(planData.getDisk());
        instance =builder.setPlan(plan).createInstanceWithRetry(getNamePrefix());
    }

    @Test
    public void testIncreaseDiskStorageAboveMaximumValue() throws Exception {
        int diskSizeAboveMaximumValue = MAX_DISK_SIZE + DISK_SIZE_UNIT;
        UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
        updateInstanceRequest.setDiskSize(diskSizeAboveMaximumValue);
        GetInstanceResponse instanceUpdated = null;
        try {
            instanceUpdated = instance.updateInstanceWithRetry(updateInstanceRequest);
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
            assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.INVALID_ARGUMENT.getCode());
        }
        assertThat(instanceUpdated).isNull();
    }

    @Test
    public void testIncreaseDiskStorageBelowCurrentValue() throws Exception {
        int diskSizeBelowCurrentValue = instance.getInstanceResponse().getDiskSize() - DISK_SIZE_UNIT;
        UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
        updateInstanceRequest.setDiskSize(diskSizeBelowCurrentValue);
        GetInstanceResponse instanceUpdated = null;
        try {
            instanceUpdated = instance.updateInstanceWithRetry(updateInstanceRequest);
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
            assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.INVALID_ARGUMENT.getCode());
        }
        assertThat(instanceUpdated).isNull();
    }

    @Test
    public void testIncreaseDiskStorageAboveCurrentBelowMaximum() throws Exception {
        int diskSizeBelowCurrentValue = instance.getInstanceResponse().getDiskSize() + DISK_SIZE_UNIT;
        UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
        updateInstanceRequest.setDiskSize(diskSizeBelowCurrentValue);
        GetInstanceResponse instanceUpdated = instance.updateInstanceWithRetry(updateInstanceRequest);
        assertThat(instanceUpdated).isNotNull();
        assertThat(instanceUpdated.getDiskSize()).isEqualTo(updateInstanceRequest.getDiskSize());
    }
}
