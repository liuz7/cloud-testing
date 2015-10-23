package com.vmware.vchs.snapshot;

import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.common.utils.exception.RestException;
import com.vmware.vchs.constant.Constants;
import com.vmware.vchs.constant.StatusCode;
import com.vmware.vchs.model.constant.SnapshotType;
import com.vmware.vchs.model.portal.common.AsyncResponse;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.model.portal.snapshot.CreateSnapshotRequest;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * Created by georgeliu on 14/12/16.
 */
public class GetSnapshotTest extends SnapshotTest {

    @Test
    public void testGetSnapshotInstance() throws Exception {
        AsyncResponse createResponse = instance.createSnapshot();
        instance.getSnapshot(createResponse.getId());
        GetSnapshotResponse snapshotCreated =instance.waitSnapshotAvailable(createResponse.getId());

        assertThat(snapshotCreated.getStatus()).isEqualTo(StatusCode.AVAILABLE.value());
        assertThat(snapshotCreated.getId()).isNotNull();
        assertThat(snapshotCreated.getName().equalsIgnoreCase(instance.getInstanceResponse().getName()));
        assertThat(snapshotCreated.getDescription().equalsIgnoreCase(instance.getInstanceResponse().getDescription()));
        assertThat(snapshotCreated.getType()).isEqualTo(SnapshotType.manual.toString());

        assertThat(snapshotCreated.getCreatedAt()).isNotNull();
        assertThat(snapshotCreated.getUpdatedAt()).isNotNull();
        assertThat(snapshotCreated.getPermissions()).isNotNull();
        assertThat(snapshotCreated.getSourceInstance().getId()).isNotNull();
        assertThat(snapshotCreated.getSourceInstance().getName().equalsIgnoreCase(instance.getSnapshotResponse().getName()));
        assertThat(snapshotCreated.getSourceInstance().getDiskSize() == instance.getInstanceResponse().getDiskSize());
        assertThat(snapshotCreated.getSourceInstance().getDescription().equalsIgnoreCase(instance.getSnapshotResponse().getDescription()));//TODO bug
        assertThat(snapshotCreated.getSourceInstance().getStatus()).isNotNull();
        assertThat(snapshotCreated.getSourceInstance().getIpAddress()).isNotNull();
        assertThat(snapshotCreated.getSourceInstance().getMasterUsername().equalsIgnoreCase(instance.getInstanceResponse().getMasterUsername()));
        assertThat(snapshotCreated.getSourceInstance().getVersion().equalsIgnoreCase(instance.getInstanceResponse().getVersion()));
        assertThat(snapshotCreated.getSourceInstance().getEdition().equalsIgnoreCase(instance.getInstanceResponse().getEdition()));
        assertThat(snapshotCreated.getSourceInstance().getPlan().equals(instance.getInstanceResponse().getPlan()));
    }

    @Test
    public void testGetNonExistingSnapshotInstance() throws Exception {
        try {
            dbaasApi.getSnapshot(UUID.randomUUID().toString());
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
            //assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.RESOURCE_NOT_FOUND.getCode());//TODO due to DBAAS-563
//            assertThat(((PortalError) e.getError()).getMessage()).containsIgnoringCase(Error.SNAPSHOT_NOT_EXIST.toString());
        }
    }

    @Test//TODO bug
    public void testGetInvalidSnapshotInstance() throws Exception {
        try {
            dbaasApi.getSnapshot(Constants.INVALID_STRING);
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
            //assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.RESOURCE_NOT_FOUND.getCode());//TODO due to DBAAS-563
//            assertThat(((PortalError) e.getError()).getMessage()).containsIgnoringCase(Error.SNAPSHOT_NOT_EXIST.toString());
        }
    }

    @Test
    public void testGetSnapshotExistAfterDeletingInstance() throws Exception {
        DbaasInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        GetSnapshotResponse createResponse = instance.createSnapshotWithRetry();
        instance.deleteWithRetry();
        GetSnapshotResponse snapshotCreated =dbaasApi.waitAndGetAvailableSnapshot(createResponse.getId());

        assertThat(snapshotCreated.getStatus()).isEqualTo(StatusCode.AVAILABLE.value());
        assertThat(snapshotCreated.getId()).isNotNull();
        assertThat(snapshotCreated.getName()).isNotNull();
        assertThat(snapshotCreated.getDescription()).isNotNull();
        assertThat(snapshotCreated.getType()).isEqualTo(SnapshotType.manual.toString());

        assertThat(snapshotCreated.getCreatedAt()).isNotNull();
        assertThat(snapshotCreated.getUpdatedAt()).isNotNull();
        assertThat(snapshotCreated.getPermissions()).isNotNull();
        //assertThat(snapshotCreated.getPermissions().get(0)).isEqualTo(CREATEINSTANCEFROMSNAPSHOT);
//        assertThat(snapshotCreated.getCreatorEmail()).isNotNull(); //TODO bug
        assertThat(snapshotCreated.getSourceInstance().getId()).isNotNull();
        assertThat(snapshotCreated.getSourceInstance().getName().equalsIgnoreCase(instance.getSnapshotResponse().getName()));
        assertThat(snapshotCreated.getSourceInstance().getDiskSize() == instance.getInstanceResponse().getDiskSize());
        assertThat(snapshotCreated.getSourceInstance().getDescription().equalsIgnoreCase(instance.getSnapshotResponse().getDescription()));//TODO bug
        assertThat(snapshotCreated.getSourceInstance().getStatus()).isNotNull();
        assertThat(snapshotCreated.getSourceInstance().getIpAddress()).isNull();
        assertThat(snapshotCreated.getSourceInstance().getMasterUsername().equalsIgnoreCase(instance.getInstanceResponse().getMasterUsername()));
        assertThat(snapshotCreated.getSourceInstance().getVersion().equalsIgnoreCase(instance.getInstanceResponse().getVersion()));
        assertThat(snapshotCreated.getSourceInstance().getEdition().equalsIgnoreCase(instance.getInstanceResponse().getEdition()));
        assertThat(snapshotCreated.getSourceInstance().getPlan().equals(instance.getInstanceResponse().getPlan()));
    }

}
