package com.vmware.vchs.base;

import com.google.common.collect.Lists;
import com.vmware.vchs.launcher.TestHelper;
import com.vmware.vchs.model.portal.instance.PitrSettings;
import com.vmware.vchs.model.portal.instance.SnapshotSettings;
import com.vmware.vchs.model.portal.instance.UpdateInstanceRequest;
import org.testng.annotations.DataProvider;

import java.util.List;

/**
 * Created by georgeliu on 15/1/15.
 */
public class BaseDataProvider {

    private static UpdateInstanceRequest getPasswordCanBeUpdated() {
        UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
        updateInstanceRequest.setMasterPassword("Updated Password");
        return updateInstanceRequest;
    }

    private static UpdateInstanceRequest getPitrSettingsCanBeUpdated() {
        UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
        PitrSettings pitrSettings = new PitrSettings();
        pitrSettings.setRetention(3);
        updateInstanceRequest.setPitrSettings(pitrSettings);
        return updateInstanceRequest;
    }

    private static UpdateInstanceRequest getSnapshotSettingsCanBeUpdated() {
        UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
        SnapshotSettings snapshotSettings = new SnapshotSettings();
        snapshotSettings.setEnabled(true);
        snapshotSettings.setPreferredStartTime("02:00");
        snapshotSettings.setLimit(2);
        snapshotSettings.setNamePrefix("auto-snapshot#updated");
        snapshotSettings.setCycle(2);
        updateInstanceRequest.setSnapshotSettings(snapshotSettings);
        return updateInstanceRequest;
    }

    private static UpdateInstanceRequest getMaintenanceTimeCanBeUpdated() {
        UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
        updateInstanceRequest.setMaintenanceTime("Sun:10:00");
        return updateInstanceRequest;
    }

    private static UpdateInstanceRequest getMaintenanceTimeAndPasswordCanBeUpdated() {
        UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
        updateInstanceRequest.setMasterPassword("Updated Password");
        updateInstanceRequest.setMaintenanceTime("Sun:10:00");
        return updateInstanceRequest;
    }

    private static UpdateInstanceRequest getDiskSizeCanBeUpdated() {
        UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
        updateInstanceRequest.setDiskSize(153600);
        return updateInstanceRequest;
    }

    private static List<UpdateInstanceRequest> getPasswordListCanBeUpdated() {
        int size = Integer.parseInt(TestHelper.getConfiguration().getThreadSizeForInstance());
        List<UpdateInstanceRequest> updateInstanceRequestList = Lists.newArrayList();
        for (int i = 0; i < size; i++) {
            UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
            updateInstanceRequest.setMasterPassword("Updated Password" + i);
            updateInstanceRequestList.add(updateInstanceRequest);
        }
        return updateInstanceRequestList;
    }

    private static UpdateInstanceRequest getDataCanNotBeUpdated() {
        UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
        updateInstanceRequest.setMasterUsername("NonExistingUsername");
        return updateInstanceRequest;
    }

    private static UpdateInstanceRequest getDataCanNotBeAllUpdated() {
        UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
        updateInstanceRequest.setMasterUsername("NonExistingUsername");
        updateInstanceRequest.setMasterPassword("Updated Password aaa");
        return updateInstanceRequest;
    }

    @DataProvider(name = "can be updated")
    public static Object[][] createDataCanBeUpdated() {
        return new Object[][]{
                new Object[]{getPasswordCanBeUpdated()},
                //new Object[]{getMaintenanceTimeCanBeUpdated()},
                //new Object[]{getMaintenanceTimeAndPasswordCanBeUpdated()},
//                new Object[]{getPitrSettingsCanBeUpdated()},
                new Object[]{getSnapshotSettingsCanBeUpdated()},
                //new Object[]{new UpdateInstanceRequest()},
        };
    }

    @DataProvider(name = "can be updated during backup")
    public static Object[][] createDataCanBeUpdatedDuringBackup() {
        return new Object[][]{
                new Object[]{getPasswordCanBeUpdated()},
                //new Object[]{getMaintenanceTimeCanBeUpdated()},
                //new Object[]{getMaintenanceTimeAndPasswordCanBeUpdated()},
                new Object[]{getSnapshotSettingsCanBeUpdated()},
        };
    }

    @DataProvider(name = "disk can be updated")
    public static Object[][] createDiskSizeUpdated() {
        return new Object[][]{
                new Object[]{getDiskSizeCanBeUpdated()},
        };
    }

    @DataProvider(name = "can be updated concurrently")
    public static Object[][] createDataCanBeUpdatedConcurrently() {
        return new Object[][]{
                new Object[]{getPasswordListCanBeUpdated()},
        };
    }

    @DataProvider(name = "can not be updated")
    public static Object[][] createDataCanNotBeUpdated() {
        return new Object[][]{
                new Object[]{getDataCanNotBeUpdated()},
                new Object[]{getDataCanNotBeAllUpdated()},
        };
    }

    @DataProvider(name = "plan name")
    public static Object[][] createPlanNames() {
        return new Object[][]{
                new Object[]{"Small"},
                new Object[]{"Medium"},
                new Object[]{"Large"},
        };
    }

    @DataProvider(name = "invalid port")
    public static Object[][] invalidPort() {
        return new Object[][]{
                new Object[]{1000},
                new Object[]{70000},
        };
    }
}
