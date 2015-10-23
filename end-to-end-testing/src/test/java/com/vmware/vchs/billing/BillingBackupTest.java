package com.vmware.vchs.billing;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.vmware.vchs.SnapshotBaseTest;
import com.vmware.vchs.billing.model.Event;
import com.vmware.vchs.common.utils.TimeUtils;
import com.vmware.vchs.common.utils.Utils;
import com.vmware.vchs.common.utils.exception.RestException;
import com.vmware.vchs.constant.Constants;
import com.vmware.vchs.gateway.model.BackupResource;
import com.vmware.vchs.model.billing.MeterModel;
import com.vmware.vchs.model.constant.ContentModel;
import com.vmware.vchs.model.constant.Meter;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.test.client.db.MsSqlDaoFactory;
import com.vmware.vchs.test.client.db.model.Employee;
import com.vmware.vchs.utils.BillingUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.vmware.vchs.test.client.db.SQLStatements.DB_TESTDB;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by georgeliu on 15/7/9.
 */
@Test(groups = {"billing"})
public class BillingBackupTest extends SnapshotBaseTest {

    /*ObjectMapper mapper;

    @BeforeMethod(alwaysRun = true)
    public void setup() throws Exception {
        try {
            mapper = new ObjectMapper();
        } catch (Exception e) {
            logger.info(Utils.getStackTrace(e));
            throw e;
        }
    }

    @Test
    public void testBillingFromBackup() throws Exception {
        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
        List<Event> results = getEventsByInstanceId(instanceCreated.getId());
        List<Event> provisionResults = getEventByMeterType(results, Constants.BILLING_STORAGE);
        List<Event> backupResults = getEventByMeterType(results, Constants.BILLING_BACKUP);
        assertThat(provisionResults.size()).isEqualTo(1);
        assertThat(backupResults.size()).isNotNegative();
        for (Event event : provisionResults) {
            logger.info("event is " + event.toString());
            assertThat(event.getInstanceId()).isEqualToIgnoringCase(instanceCreated.getId());
            assertThat(event.getService_name()).isEqualToIgnoringCase(SERVICE_NAME);
            assertThat(event.getStatus()).isEqualToIgnoringCase(Status.UNDELIVERED.toString());
            ContentModel content = mapper.readValue(event.getContent(), ContentModel.class);
            logger.info("content is " + content.toString());
            checkNotNull(content);
            checkNotNull(content.getMeters());
            assertThat(content.getMeters().size()).isEqualTo(2);
            MeterModel meterModel = BillingUtils.getMeter(content.getMeters(), this.createInstanceRequest, super.planName.toLowerCase());
            assertThat(meterModel.getBackup()).isEqualTo(-1);
        }
    }

    @Test
    public void testBillingFromProvisionDBFromBackup() throws Exception {
        String oldRetentionWindow = this.createInstanceRequest.getDebugProperties().getBackupRetention();
        try {
            String newRetentionWindow = "00:01:00:00";
            this.createInstanceRequest.getDebugProperties().setBackupRetention(newRetentionWindow);
            Employee testEmployee1 = testData.get(0);
            Employee testEmployee2 = testData.get(1);
            GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
            MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
            assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
            insertData(jdbcClient, DB_TESTDB, testEmployee1);
            assertThat(isDataExists(jdbcClient.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
            String[] firstRange = getRestoreWindow(instanceCreated.getId());
            String[] range = getRestoreWindowRightEdgeAfterCurrentTime(jdbcClient, instanceCreated.getId(), firstRange);

            //check the backup event and its file size
            List<Event> resultsForInstanceCreated = getEventsByInstanceId(instanceCreated.getId());
            List<Event> backupResults = getEventByMeterType(resultsForInstanceCreated, Constants.BILLING_BACKUP);
            for (Event event : backupResults) {
                logger.info("event is " + event.toString());
                assertThat(event.getInstanceId()).isEqualToIgnoringCase(instanceCreated.getId());
                assertThat(event.getService_name()).isEqualToIgnoringCase(SERVICE_NAME);
                assertThat(event.getStatus()).isEqualToIgnoringCase(Status.UNDELIVERED.toString());
                ContentModel content = mapper.readValue(event.getContent(), ContentModel.class);
                logger.info("content is " + content.toString());
                checkNotNull(content);
                checkNotNull(content.getMeters());
                assertThat(content.getMeters().size()).isEqualTo(1);
            }
            int sumOfBackupSize = getSumOfBackupSize(instanceCreated.getId());
            sleepBySeconds(5);
            resultsForInstanceCreated = getEventsByInstanceId(instanceCreated.getId());
            backupResults = getEventByMeterType(resultsForInstanceCreated, Constants.BILLING_BACKUP);
            Event lastBackupEvent = Iterables.getLast(backupResults);
            ContentModel lastContent = mapper.readValue(lastBackupEvent.getContent(), ContentModel.class);
            MeterModel lastMeterModel = BillingUtils.getMeter(lastContent.getMeters(), this.createInstanceRequest, super.planName.toLowerCase());
            assertThat(lastMeterModel.getBackup()).isEqualTo(sumOfBackupSize);

            //restore instance from backup
            getCurrentTimeAfterRestoreTime(jdbcClient, range[1]);
            insertData(jdbcClient, DB_TESTDB, testEmployee2);
            GetInstanceResponse activeInstanceFromBackup = createInstanceFromBackup(instanceCreated, range[1], masterPassword);
            MsSqlDaoFactory jdbcClientFromBackup = getDbConnection(activeInstanceFromBackup);
            assertThat(testDbConnectionWithRetry(jdbcClientFromBackup)).isTrue();
            assertThat(jdbcClientFromBackup.createSysDao().isDatabaseExists(DB_TESTDB)).isTrue();
            assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
            assertThat(isDataExists(jdbcClientFromBackup.createEmployeeDao(), DB_TESTDB, testEmployee2)).isFalse();

            //check the event for instance restored from backup
            List<Event> resultsForactiveInstanceFromBackup = getEventsByInstanceId(activeInstanceFromBackup.getId());
            List<Event> provisionResults = getEventByMeterType(resultsForactiveInstanceFromBackup, Constants.BILLING_STORAGE);
            assertThat(provisionResults.size()).isEqualTo(1);
            for (Event event : provisionResults) {
                logger.info("event is " + event.toString());
                assertThat(event.getInstanceId()).isEqualToIgnoringCase(activeInstanceFromBackup.getId());
                assertThat(event.getService_name()).isEqualToIgnoringCase(SERVICE_NAME);
                assertThat(event.getStatus()).isEqualToIgnoringCase(Status.UNDELIVERED.toString());
                ContentModel content = mapper.readValue(event.getContent(), ContentModel.class);
                logger.info("content is " + content.toString());
                checkNotNull(content);
                checkNotNull(content.getMeters());
                assertThat(content.getMeters().size()).isEqualTo(2);
                MeterModel meterModel = BillingUtils.getMeter(content.getMeters(), this.createInstanceRequest, super.planName.toLowerCase());
                assertThat(meterModel.getBackup()).isEqualTo(-1);
            }
        } finally {
            this.createInstanceRequest.getDebugProperties().setBackupRetention(oldRetentionWindow);
        }
    }

    @Test
    public void testNoBillingFromProvisionDBFromBackupFailed() throws Exception {
        Employee testEmployee1 = testData.get(0);
        Employee testEmployee2 = testData.get(1);
        GetInstanceResponse instanceCreated = createInstanceWithBackupToTrue(this.createInstanceRequest);
        String[] firstRange = getRestoreWindow(instanceCreated.getId());
        MsSqlDaoFactory jdbcClient = getDbConnection(instanceCreated);
        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
        insertData(jdbcClient, DB_TESTDB, testEmployee1);
        assertThat(isDataExists(jdbcClient.createEmployeeDao(), DB_TESTDB, testEmployee1)).isTrue();
        insertData(jdbcClient, DB_TESTDB, testEmployee2);
        String invalidRestoreTime = TimeUtils.getDateTime(TimeUtils.parseDate(firstRange[0]).minusSeconds(1));
        try {
            createInstanceFromBackup(instanceCreated, invalidRestoreTime, masterPassword);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
            List<Event> results = getEventsByInstanceId(instanceCreated.getId());
            List<Event> provisionResults = getEventByMeterType(results, Constants.BILLING_STORAGE);
            assertThat(provisionResults.size()).isEqualTo(1);
            for (Event event : provisionResults) {
                logger.info("event is " + event.toString());
                assertThat(event.getInstanceId()).isEqualToIgnoringCase(instanceCreated.getId());
                assertThat(event.getService_name()).isEqualToIgnoringCase(SERVICE_NAME);
                assertThat(event.getStatus()).isEqualToIgnoringCase(Status.UNDELIVERED.toString());
                ContentModel content = mapper.readValue(event.getContent(), ContentModel.class);
                logger.info("content is " + content.toString());
                checkNotNull(content);
                checkNotNull(content.getMeters());
                assertThat(content.getMeters().size()).isEqualTo(2);
                MeterModel meterModel = BillingUtils.getMeter(content.getMeters(), this.createInstanceRequest, super.planName.toLowerCase());
                assertThat(meterModel.getBackup()).isEqualTo(-1);
            }
        }
    }

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

    public int getSumOfBackupSize(String instanceId) {
        List<BackupResource> backupResources = getBackupsByInstanceId(instanceId);
        return BillingUtils.getSumOfBackupSize(backupResources);
    }*/

}
