package com.vmware.vchs.instance;

import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.common.utils.exception.RestException;
import com.vmware.vchs.constant.Constants;
import com.vmware.vchs.constant.StatusCode;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * Created by georgeliu on 14/11/4.
 */
public class GetInstanceTest extends InstanceTest {

    @Test(groups = {"sanity"}, priority = 1)
    public void testGetDBInstance() throws Exception {
        DbaasInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        assertThat(instance).isNotNull();
    }

    @Test(invocationCount = 5)
    public void testGetDBInstanceDuringProvision() throws Exception {
        DbaasInstance provisioningInstance=builder.createInstance(getNamePrefix());
        assertThat(provisioningInstance.getInstanceInCreatingResponse().getStatus()).isEqualToIgnoringCase(StatusCode.CREATING.value());
        ResponseEntity<GetInstanceResponse> getInstanceResponse = dbaasApi.getDBInstanceEntity(provisioningInstance.getInstanceid());
        assertThat(getInstanceResponse.getStatusCode().value()).isEqualTo(HttpURLConnection.HTTP_OK);
        provisioningInstance.waitAndGetAvailableInstance();
    }

    @Test
    public void testGetNonExistingDBInstance() throws Exception {
        try {
            dbaasApi.getDBInstance(Constants.INVALID_STRING);
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
        }
    }

    @Test
    public void testGetExistingDBInstanceWithIncorrectType() throws Exception {
        DbaasInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        try {
            dbaasApi.getSnapshotDBInstance(instance.getInstanceid());
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
            //assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.RESOURCE_NOT_FOUND.getCode());
            //assertThat(((PortalError) e.getError()).getMessage()).containsIgnoringCase(Error.SNAPSHOT_NOT_EXIST.toString());
        }
    }
}
