package com.vmware.vchs.datapath;

/**
 * Created by sjun on 8/12/15.
 */

import com.vmware.vchs.SnapshotBaseTest;
import com.vmware.vchs.base.DataPathInstance;
import com.vmware.vchs.base.E2ETest;
import com.vmware.vchs.model.portal.instance.DebugProperties;
import com.vmware.vchs.model.portal.instance.SnapshotSettings;
import com.vmware.vchs.model.portal.instance.UpdateInstanceRequest;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

@Test(groups = {"datapath", "full"})
public class DataPathBaseTest extends SnapshotBaseTest implements E2ETest {
//    protected static final Configuration configuration = TestHelper.getConfiguration();
//    protected static final DbaasApi dbaasApi = new DbaasApi();

    @BeforeClass(alwaysRun = true)
    public void setUpClass() throws Exception {
        super.preCreateInstance=false;
        super.setUpClass();

        DebugProperties properties = new DebugProperties();
        properties.setBackupCycle("00:01:00:00");
        properties.setBackupStrategy("FLLLF");
        properties.setBackupRetention("00:04:00:00");
        properties.setSnapshotCycle("00:12:00:00");
        builder.setDebugProperties(properties);
    }
    protected interface InstanceSnapshotMethod {
        GetSnapshotResponse getSnapshot(DataPathInstance instance) throws Exception;
    }

    protected InstanceSnapshotMethod autoSnapshot = new InstanceSnapshotMethod() {
        @Override
        public GetSnapshotResponse getSnapshot(DataPathInstance instance) throws Exception {
            UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
            SnapshotSettings snapshotSettings = dbaasApi.generateSnapshotSettings(false);
            updateInstanceRequest.setSnapshotSettings(snapshotSettings);

            instance.updateInstanceWithRetry(updateInstanceRequest);

            snapshotSettings.setEnabled(true);
            snapshotSettings.setPreferredStartTime(0, 2);
            instance.updateInstanceWithRetry(updateInstanceRequest);

            GetSnapshotResponse response = instance.waitAutoSnapshot();

            return response;
        }
    };

    protected InstanceSnapshotMethod manualSnapshot = new InstanceSnapshotMethod() {
        @Override
        public GetSnapshotResponse getSnapshot(DataPathInstance instance) throws Exception {
            TimeUnit.SECONDS.sleep(getSleepTime());
            return instance.createSnapshotWithRetry();
        }
    };

    protected int getSleepTime() {
        return configuration.getDataPath().getWaitTime();
    }

}
