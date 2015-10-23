package com.vmware.vchs.utils;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.vmware.vchs.billing.model.Event;
import com.vmware.vchs.constant.Constants;
import com.vmware.vchs.gateway.model.BackupResource;
import com.vmware.vchs.model.billing.MeterModel;
import com.vmware.vchs.model.constant.Meter;
import com.vmware.vchs.model.constant.Meters;
import com.vmware.vchs.model.portal.instance.CreateInstanceRequest;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.test.client.gcs.GCSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.LongSummaryStatistics;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by liuda on 6/25/15.
 */
public class BillingUtils {
    protected static final Logger logger = LoggerFactory.getLogger(BillingUtils.class);
    public final static int PROVISION = 1;
    public final static int UNPROVISION = 0;

    public static Event getLatestEvent(List<Event> backupResults) {
        return backupResults.stream().sorted(new Comparator<Event>() {
            public int compare(Event m1, Event m2) {
                return m1.getReceive_date().after(m2.getReceive_date()) ? 1 : -1;
            }
        }).reduce((a, b) -> b).get();
    }

    public static String getDbaasPlan(GetInstanceResponse request, String planName) {
        String plan = null;
        String licenseType = Constants.LICENSETYPE.get(request.getLicenseType());
        if (Constants.EDITIONTYPE.containsKey(request.getEdition())) {
            plan = Constants.BILLING_PLAN_LICENSE + "." + Constants.EDITIONTYPE.get(request.getEdition()) + "." + licenseType + "." + planName;
        } else {
            plan = Constants.BILLING_PLAN_LICENSE + "." + licenseType + "." + planName;
        }
        logger.info("billing plan is " + plan);
        return plan;
    }

    public static String getDbaasStorage() {
        String storage = Constants.BILLING_STORAGE;
        logger.info("billing storage is " + storage);
        return storage;
    }


    public static void checkFileSizeWithGCS(BackupResource backupResource) {
        String backupFileName = Iterables.getLast(Splitter.on("/").trimResults().omitEmptyStrings().split(backupResource.getResourceUri()));
        int backupFileSize = new GCSClient().getSizeOfFile(backupFileName);
        assertThat(Math.round(backupFileSize / 1024)).isEqualTo((int) backupResource.getSize());
    }

    public static int getSumOfBackupSize(List<BackupResource> backupResources) {
        backupResources.forEach(e -> BillingUtils.checkFileSizeWithGCS(e));
        LongSummaryStatistics stats = backupResources.stream().mapToLong((x) -> x.getSize()).summaryStatistics();
        return (int) stats.getSum();
    }

    public static String getDbaasBackup() {
        String storage = Constants.BILLING_BACKUP;
        logger.info("billing backup is " + storage);
        return storage;
    }

    public static MeterModel getMeter(Meters meters, GetInstanceResponse createInstanceRequest, String planName) throws Exception {
        MeterModel meterModel = new MeterModel();
        getMeter(meters, createInstanceRequest, planName, meterModel);
        return meterModel;
    }

    public static MeterModel getMeter(Meters meters, GetInstanceResponse createInstanceRequest, String planName, MeterModel meterModel) throws Exception {
        for (Meter meter : meters) {
            logger.info("meter name and value is " + meter.getName() + " " + meter.getValue());
            if (meter.getName().equalsIgnoreCase(getDbaasStorage())) {
                meterModel.setStorage(Integer.valueOf(meter.getValue()));
            } else if (meter.getName().contains(getDbaasPlan(createInstanceRequest, planName))) {
                meterModel.setPlan(Integer.valueOf(meter.getValue()));
            } else if (meter.getName().equalsIgnoreCase(getDbaasBackup())) {
                meterModel.setBackup(Integer.valueOf(meter.getValue()));
            } else if (meter.getName().equalsIgnoreCase(Constants.BILLING_EGRESS)) {
                meterModel.setEgress(Integer.valueOf(meter.getValue()));
            } else if (meter.getName().equalsIgnoreCase(Constants.BILLING_INGRESS)) {
                meterModel.setIngress(Integer.valueOf(meter.getValue()));
            } else if (meter.getName().equalsIgnoreCase(Constants.BILLING_IOPS)) {
                meterModel.setIops(Integer.valueOf(meter.getValue()));
            } else {
                throw new Exception("Unknown meter name: " + meter.getName());
            }
        }
        return meterModel;
    }
}
