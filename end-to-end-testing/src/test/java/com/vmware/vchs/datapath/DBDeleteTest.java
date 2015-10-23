package com.vmware.vchs.datapath;

import com.vmware.vchs.base.DataPathInstance;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by sjun on 8/12/15.
 */
public class DBDeleteTest extends DataPathBaseTest {
    protected void testWithDisabledPITR(DataPathInstance instance, InstanceSnapshotMethod method) throws Exception {
        instance.preparePreDb();
        instance.createDatabaseAndTable("INDB1", "INTable1");
        instance.createDatabaseAndTable("INDB1", "INTable2");
        instance.createDatabaseAndTable("INDB2", "INTable1");
        instance.createDatabaseAndTable("INDB2", "INTable2");

        instance.startWriters();

        //round 1
        {
            instance.dropDatabase(DataPathInstance.preDbPrefix + "1");

            GetSnapshotResponse holder = method.getSnapshot(instance);

            DataPathInstance instanceRestore = builder.restoreFromSnapshot(getNamePrefix(), holder.getId());
            instanceRestore.verifyDataLists(holder,instance.data);
            assertThat(instanceRestore.getMssqlConnection().isDatabaseExists(DataPathInstance.preDbPrefix + "1")).isFalse();
            instanceRestore.delete();
        }
        //round 2
        {
            instance.dropDatabase("INDB1");

            GetSnapshotResponse holder = method.getSnapshot(instance);

            DataPathInstance instanceRestore = builder.restoreFromSnapshot(getNamePrefix(), holder.getId());
            instanceRestore.verifyDataLists(holder,instance.data);
            assertThat(instanceRestore.getMssqlConnection().isDatabaseExists("INDB1")).isFalse();
            instanceRestore.delete();
        }
        //round 3
        {
            instance.dropDatabase(DataPathInstance.preDbPrefix + "2");

            GetSnapshotResponse holder = method.getSnapshot(instance);

            DataPathInstance instanceRestore = builder.restoreFromSnapshot(getNamePrefix(), holder.getId());
            instanceRestore.verifyDataLists(holder,instance.data);
            assertThat(instanceRestore.getMssqlConnection().isDatabaseExists(DataPathInstance.preDbPrefix + "2")).isFalse();
            instanceRestore.delete();
        }
        instance.stopWriters();
        try {
            instance.dropDatabase(DataPathInstance.preDbPrefix + 1);
            instance.dropDatabase(DataPathInstance.preDbPrefix + 2);
        } catch (Exception e) {}
    }

    @Test(groups = {"datapath-manual-snapshot"})
    public void testDBDeleteAndManualSnapshotWithDisabledPITR() throws Exception {
        DataPathInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        testWithDisabledPITR(instance, manualSnapshot);
    }

    @Test(groups = {"datapath-auto-snapshot"})
    public void testDBDeleteAndAutoSnapshotWithDisabledPITR() throws Exception {
        DataPathInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        testWithDisabledPITR(instance, autoSnapshot);
    }
}
