package com.vmware.vchs.billing;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vchs.InstanceBaseTest;
import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.billing.model.Event;
import com.vmware.vchs.common.utils.Utils;
import com.vmware.vchs.constant.Constants;
import com.vmware.vchs.gateway.model.BackupResource;
import com.vmware.vchs.gateway.model.SnapshotResource;
import com.vmware.vchs.model.billing.MeterModel;
import com.vmware.vchs.model.constant.ContentModel;
import com.vmware.vchs.model.constant.Meter;
import com.vmware.vchs.model.portal.instance.DebugProperties;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import com.vmware.vchs.utils.BillingUtils;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by david on 15/3/4.
 */
@Test(groups = {"billing"})
public class BillingSnapshotTest extends InstanceBaseTest {
    ObjectMapper mapper = new ObjectMapper();

    private List<Event> getEventByMeterType(List<Event> events, String type) {
        List<Event> result = Lists.newArrayList();
        for (Event event : events) {
            try {
                ContentModel content = mapper.readValue(event.getContent(), ContentModel.class);
                for (Meter meter : content.getMeters()) {
                    if (meter.getName().equalsIgnoreCase(type)) {
                        result.add(event);
                    }
                }
            } catch (Exception e) {
                Utils.getStackTrace(e);
            }
        }
        return result;
    }

    @Test
    public void testBillingFromManualSnapshot() throws Exception {
        DbaasInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        instance.createSnapshotWithRetry();
        instance.waitAllSnapshotCompact();
        instance.recursiveDeleteInstanceWithRetry();
        List<Event> results = instance.getEvents();
        List<Event> resultsForInstanceCreated = instance.waitBillingEventByInstanceIdReachCount(results.size() + 8);

        List<BackupResource> backupResources = new ArrayList<>();
        List<SnapshotResource> snapshotResources = instance.getSnapshotIds();
        for (SnapshotResource snapshotResource : snapshotResources) {
            backupResources.addAll(snapshotResource.getBackups());
        }

        int sumOfBackupSize = BillingUtils.getSumOfBackupSize(backupResources);
        logger.info("sumOfBackupSize is: " + sumOfBackupSize);
        List<Event> backupResults = getEventByMeterType(resultsForInstanceCreated, Constants.BILLING_BACKUP);
        Event lastBackupEvent = BillingUtils.getLatestEvent(backupResults);
        logger.info("event is " + lastBackupEvent.toString());
        ContentModel lastContent = mapper.readValue(lastBackupEvent.getContent(), ContentModel.class);
        MeterModel lastMeterModel = BillingUtils.getMeter(lastContent.getMeters(), instance.getInstanceResponse(), configuration.getPlanName().toLowerCase());
        logger.info("lastMeterModel is: " + lastMeterModel.getBackup());
        assertThat(lastMeterModel.getBackup()).isEqualTo(sumOfBackupSize);
    }

    @Test
    public void testBillingFromAutoSnapshot() throws Exception {
        DebugProperties properties = new DebugProperties();
        properties.setSnapshotCycle("00:00:02:00");
        DbaasInstance instance = builder.setAutoSnapshot(true).setDebugProperties(properties).createInstanceWithRetry(getNamePrefix());

        instance.waitSnapshotLargeThanOrEqualToMin(1);
        instance.waitAllSnapshotCompact();
        instance.recursiveDeleteInstanceWithRetry();

        List<Event> results = instance.getEvents();
        List<Event> resultsForInstanceCreated = instance.waitBillingEventByInstanceIdReachCount(results.size() + 5);

        List<BackupResource> backupResources = new ArrayList<>();
        List<SnapshotResource> snapshotResources = instance.getSnapshotIds();
        for (SnapshotResource snapshotResource : snapshotResources) {
            backupResources.addAll(snapshotResource.getBackups());
        }

        int sumOfBackupSize = BillingUtils.getSumOfBackupSize(backupResources);
        logger.info("sumOfBackupSize is: " + sumOfBackupSize);
        List<Event> backupResults = getEventByMeterType(resultsForInstanceCreated, Constants.BILLING_BACKUP);
        Event lastBackupEvent = BillingUtils.getLatestEvent(backupResults);
        ContentModel lastContent = mapper.readValue(lastBackupEvent.getContent(), ContentModel.class);
        MeterModel lastMeterModel = BillingUtils.getMeter(lastContent.getMeters(), instance.getInstanceResponse(), configuration.getPlanName().toLowerCase());
        logger.info("lastMeterModel is: " + lastMeterModel.getBackup());
        assertThat(lastMeterModel.getBackup()).isEqualTo(sumOfBackupSize);
    }


    @Test
    public void testBillingFromProvisionInstanceFromSnapshot() throws Exception {
        DbaasInstance restoredInstance = null;
        DbaasInstance instanceRestoredFrom = null;
        instanceRestoredFrom =  builder.createInstanceWithRetry(getNamePrefix());
        GetSnapshotResponse snapshotResponse=instanceRestoredFrom.createSnapshotWithRetry();
        restoredInstance = builder.restoreFromSnapshot(getNamePrefix(),snapshotResponse.getId());
        List<Event> results = restoredInstance.getEvents();
        for (Event event : results) {
            logger.info("event is " + event.toString());
        }
        assertThat(results.size()).isGreaterThanOrEqualTo(1);
        MeterModel meterModel = new MeterModel();
        for (Event event : results) {
            logger.info("event is " + event.toString());
            assertThat(event.getInstanceId()).isEqualToIgnoringCase(restoredInstance.getInstanceid());
            assertThat(event.getService_name()).isEqualToIgnoringCase(Constants.SERVICE_NAME);
            ContentModel content = mapper.readValue(event.getContent(), ContentModel.class);
            logger.info("content is " + content.toString());
            checkNotNull(content);
            checkNotNull(content.getMeters());
            meterModel = BillingUtils.getMeter(content.getMeters(), restoredInstance.getInstanceResponse(), configuration.getPlanName().toLowerCase(), meterModel);

        }
        assertThat(meterModel.getStorage()).isEqualTo(restoredInstance.getInstanceResponse().getDiskSize());
        assertThat(meterModel.getPlan()).isEqualTo(BillingUtils.PROVISION);
    }


}
