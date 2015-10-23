package com.vmware.vchs.datapath;

import com.vmware.vchs.base.DataPathInstance;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import org.testng.annotations.Test;

/**
 * Created by sjun on 8/12/15.
 */
public class DBCreationTest extends DataPathBaseTest {
    protected void testWithDisabledPITR(DataPathInstance instance, InstanceSnapshotMethod method) throws Exception {
        instance.preparePreDb();
        instance.startWriters();
        //round 1
        {
            DBWriter w1 = instance.createDatabaseAndTable("INDB1", "INTable1");
            DBWriter w2 = instance.createDatabaseAndTable("INDB1", "INTable2");
            w1.start();
            w2.start();

            GetSnapshotResponse holder = method.getSnapshot(instance);

            DataPathInstance instanceRestore = builder.restoreFromSnapshot(getNamePrefix(), holder.getId());
            instanceRestore.verifyDataLists(holder,instance.data);
            instanceRestore.delete();
        }
        //round 2
        {
            DBWriter w1 = instance.createDatabaseAndTable("INDB2", "INTable1");
            DBWriter w2 = instance.createDatabaseAndTable("INDB2", "INTable2");
            w1.start();
            w2.start();

            GetSnapshotResponse holder = method.getSnapshot(instance);

            DataPathInstance instanceRestore = builder.restoreFromSnapshot(getNamePrefix(), holder.getId());
            instanceRestore.verifyDataLists(holder,instance.data);
            instanceRestore.delete();
        }
        instance.stopWriters();
        try {
            instance.dropDatabase(DataPathInstance.preDbPrefix + 1);
            instance.dropDatabase(DataPathInstance.preDbPrefix + 2);
        } catch (Exception e) {}
    }

    @Test(groups = {"datapath-manual-snapshot"})
    public void testDBCreationAndManualSnapshotWithDisabledPITR() throws Exception {
        DataPathInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        testWithDisabledPITR(instance, manualSnapshot);
    }

    @Test(groups = {"datapath-auto-snapshot"})
    public void testDBCreationAndAutoSnapshotWithDisabledPITR() throws Exception {
        DataPathInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        testWithDisabledPITR(instance, autoSnapshot);
    }
}
