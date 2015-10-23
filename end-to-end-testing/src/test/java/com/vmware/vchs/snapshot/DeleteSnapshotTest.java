package com.vmware.vchs.snapshot;

import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.common.utils.exception.RestException;
import com.vmware.vchs.constant.Constants;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * Created by georgeliu on 14/12/18.
 */
public class DeleteSnapshotTest extends SnapshotTest {

    @Test(groups = {"sanity"})
    public void testDeleteSnapshotInstance() throws Exception {
        GetSnapshotResponse snapshotResponse = instance.createSnapshotWithRetry();
        assertThat(snapshotResponse).isNotNull();
        instance.deleteSnapshotWithRetry(snapshotResponse.getId());
    }

    @Test
    public void testDeleteInvalidSnapshotInstance() throws Exception {
        try {
            instance.deleteSnapshot("abcdef");
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
            //assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.RESOURCE_NOT_FOUND.getCode());
//            assertThat(((PortalError) e.getError()).getMessage()).containsIgnoringCase(Error.REQUEST_RESOURCE_NOT_FOUND.toString());
        }
    }

    @Test
    public void testDeleteNonExistingSnapshotInstanceWithRealUUID() throws Exception {
        try {
            instance.deleteSnapshot(Constants.INVALID_STRING);
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
            //assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.RESOURCE_NOT_FOUND.getCode());
//            assertThat(((PortalError) e.getError()).getMessage()).containsIgnoringCase(Error.REQUEST_RESOURCE_NOT_FOUND.toString());
        }
    }

    @Test
    public void testDeleteSnapshotInstanceByList() throws Exception {
        for (int i = 0; i < 3; i++) {
            instance.createSnapshotWithRetry();
        }
        instance.listSnapshots().stream().forEach(e -> instance.deleteSnapshot(e.getId()));
        instance.waitSnapshotEmpty();
    }

    @Test
    public void testDeleteSnapshotInstanceDuringUnProvision() throws Exception {
        DbaasInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        GetSnapshotResponse snapshotResponse = instance.createSnapshotWithRetry();

        instance.delete();
            instance.deleteSnapshotWithRetry(snapshotResponse.getId());
            try {
                instance.getSnapshot(snapshotResponse.getId());
                failBecauseExceptionWasNotThrown(RestException.class);
            } catch (RestException e) {
                assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
                //assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.RESOURCE_NOT_FOUND.getCode());//TODO due to DBAAS-563
    //                assertThat(((PortalError) e.getError()).getMessage()).containsIgnoringCase(Error.SNAPSHOT_NOT_EXIST.toString());
            }
    }
}
