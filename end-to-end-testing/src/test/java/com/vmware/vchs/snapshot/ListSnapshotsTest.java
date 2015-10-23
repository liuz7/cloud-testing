package com.vmware.vchs.snapshot;

import com.vmware.vchs.model.constant.ResourceState;
import com.vmware.vchs.model.constant.SnapshotType;
import com.vmware.vchs.model.portal.common.Data;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import com.vmware.vchs.model.portal.snapshot.ListSnapshotResponse;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by georgeliu on 14/12/18.
 */
public class ListSnapshotsTest extends SnapshotTest {

    @Test(groups = {"sanity"})
    public void testListSnapshotInstances() throws Exception {
//        clearSnapshots();
        List<String> snapshotIds = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            GetSnapshotResponse getSnapshotResponse = instance.createSnapshotWithRetry();
            snapshotIds.add(getSnapshotResponse.getId());
        }
        ListSnapshotResponse responseSnapshotInstanceList = dbaasApi.listSnapshot();
        assertThat(responseSnapshotInstanceList).isNotNull();
        assertThat(responseSnapshotInstanceList.getTotal()).isGreaterThanOrEqualTo(3);
        assertThat(responseSnapshotInstanceList.getPage()).isGreaterThanOrEqualTo(1);
        assertThat(responseSnapshotInstanceList.getPageSize()).isGreaterThan(0);
        assertThat(responseSnapshotInstanceList.getData().size()).isGreaterThanOrEqualTo(3);
        for (Data data : responseSnapshotInstanceList.getData()) {
            assertThat(data.getId()).isNotNull();
            if (snapshotIds.contains(data.getId())) {
                snapshotIds.remove(data.getId());

                assertThat(data.getName().equalsIgnoreCase(instance.getSnapshotResponse().getName()));
                assertThat(data.getSourceInstance().getId()).isEqualToIgnoringCase(instance.getInstanceid());
                assertThat(data.getSourceInstance().getDiskSize()).isEqualTo(instance.getInstanceResponse().getDiskSize());
                assertThat(data.getSourceInstance().getName()).isNotNull();
                assertThat(data.getSourceInstance().getEdition()).isNotNull();
                assertThat(data.getSourceInstance().getVersion()).isNotNull();
                assertThat(data.getCreatedAt()).isNotNull();
//            assertThat(data.getCreatorEmail()).isNotNull();  //TODO find out whose bug is this, iam disable's value is null
                assertThat(data.getDescription().equalsIgnoreCase(instance.getSnapshotResponse().getDescription()));//TODO bug
                assertThat(data.getStatus()).isEqualToIgnoringCase(ResourceState.available.toString());
                assertThat(data.getType()).isEqualToIgnoringCase(SnapshotType.manual.toString());
                assertThat(data.getUpdatedAt()).isNotNull();
//            assertThat(data.getSourceInstance().getIpAddress()).isNotNull();
                assertThat(data.getSourceInstance().getDiskSize() == instance.getInstanceResponse().getDiskSize());
                assertThat(data.getSourceInstance().getMasterUsername().equalsIgnoreCase(instance.getInstanceResponse().getMasterUsername()));
                assertThat(data.getSourceInstance().getVersion().equalsIgnoreCase(instance.getInstanceResponse().getVersion()));
                assertThat(data.getSourceInstance().getEdition().equalsIgnoreCase(instance.getInstanceResponse().getEdition()));
//            assertThat(data.getSourceInstance().getPlan().equals(this.createInstanceRequest.getPlan()));
            }
        }
        assertThat(snapshotIds.size()).isEqualTo(0); // ensure all the snapshot created are listed.
    }
}
