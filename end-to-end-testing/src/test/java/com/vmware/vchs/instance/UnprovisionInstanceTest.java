package com.vmware.vchs.instance;

import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.common.utils.exception.RestException;
import com.vmware.vchs.constant.Constants;
import com.vmware.vchs.constant.StatusCode;
import com.vmware.vchs.model.portal.common.AsyncResponse;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.model.portal.instance.ListInstanceItem;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.Test;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * Created by georgeliu on 14/11/4.
 */
public class UnprovisionInstanceTest extends InstanceTest {

    @Test(priority = 0)
    public void testUnProvisionDBByList() throws Exception {
        dbaasApi.cleanInstance();
    }

    @Test
    public void testUnProvisionDBDuringProvision() throws Exception {
        DbaasInstance instance = builder.createInstance(getNamePrefix());
        try {
            assertThat(instance.getStatus()).isEqualToIgnoringCase(StatusCode.CREATING.value());
            instance.delete();
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
//            assertThat(((PortalError) e.getError()).getCode()).contains(PortalErrorMap.PortalStatus.RESOURCE_BUSY.getCode());
        }
        instance.waitAndGetAvailableInstance();
    }

    @Test(groups = {"sanity"}, priority = 2)
    public void testUnProvisionDBAfterProvision() throws Exception {
        DbaasInstance instance =builder.createInstanceWithRetry(getNamePrefix());
        instance.delete();
        List<ListInstanceItem> listInstanceItems = (List<ListInstanceItem>) dbaasApi.listDBInstances().getData();
        for (ListInstanceItem listInstanceItem : listInstanceItems) {
            if(listInstanceItem.getId().equalsIgnoreCase(instance.getInstanceid())){
                assertThat(listInstanceItem.getStatus()).isEqualToIgnoringCase(StatusCode.DELETING.value());
                break;
            }
        }
        instance.waitInstanceDeleted();
        assertThat(instance.isConnected()).isFalse();
    }

    @Test
    public void testUnProvisionNonExistingDBInstance() throws Exception {
        ResponseEntity<AsyncResponse> nonExistingInstance = dbaasApi.deleteForDBInstanceEntity(Constants.INVALID_FORMAT);
        assertThat(nonExistingInstance.getStatusCode().value()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(nonExistingInstance.getBody().getStatus()).isEqualTo(StatusCode.DELETED.value());
/*        try {
            deleteInstanceWithRetry(INVALID_STRING);
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
//            assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.RESOURCE_NOT_FOUND.getCode());
//            assertThat(((PortalError) e.getError()).getMessage()).containsIgnoringCase(Error.INSTANCE_NOT_EXIST.value());
        }*/
    }

    @Test
    public void testUnProvisionNonExistingDBInstanceWithRealUUID() throws Exception {
        ResponseEntity<AsyncResponse> nonExistingInstance = dbaasApi.deleteForDBInstanceEntity(UUID.randomUUID().toString());
        assertThat(nonExistingInstance.getStatusCode().value()).isEqualTo(HttpURLConnection.HTTP_OK);
        assertThat(nonExistingInstance.getBody().getStatus()).isEqualTo(StatusCode.DELETED.value());
        /*try {
            deleteInstanceWithRetry(UUID.randomUUID().toString());
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
//            assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.RESOURCE_NOT_FOUND.getCode());
//            assertThat(((PortalError) e.getError()).getMessage()).containsIgnoringCase(Error.INSTANCE_NOT_EXIST.value());
        }*/
    }

}
