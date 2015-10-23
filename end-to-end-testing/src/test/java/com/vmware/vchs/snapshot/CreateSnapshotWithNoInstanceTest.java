package com.vmware.vchs.snapshot;

import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.common.utils.exception.RestException;
import com.vmware.vchs.constant.Constant;
import com.vmware.vchs.constant.Constants;
import com.vmware.vchs.logback.Logger;
import com.vmware.vchs.model.portal.common.Data;
import com.vmware.vchs.model.portal.instance.DebugProperties;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.model.portal.instance.SnapshotSettings;
import com.vmware.vchs.model.portal.instance.UpdateInstanceRequest;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import com.vmware.vchs.test.client.db.SQLStatements;
import com.vmware.vchs.test.client.db.model.Employee;
import com.vmware.vchs.test.client.etcd.BasicEtcdClient;
import com.vmware.vchs.utils.RandomEmployee;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.vmware.vchs.test.client.db.SQLStatements.DB_TESTDB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * Created by georgeliu on 14/12/18.
 */
public class CreateSnapshotWithNoInstanceTest extends SnapshotTest {
    final static String PREFIX = "Snapshot\\(";
    final static String SUFFIX = "\\)";

    @BeforeClass(alwaysRun = true)
    public void setUpClass() throws Exception {
        super.preCreateInstance = false;
        super.setUpClass();
    }

    @Test
    public void testUnProvisionInstanceDuringCreatingSnapshot() throws Exception {
        try {
            DbaasInstance instance = builder.createInstanceWithRetry(getNamePrefix());
            instance.createSnapshot();
            dbaasApi.deleteForDBInstance(instance.getInstanceid());
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    @Test
    public void testProvisionInstanceFromInvalidSnapshot() throws Exception {
        try {
            builder.restoreFromSnapshot(getNamePrefix(), Constants.INVALID_STRING);
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
        }
    }

    //TODO update snapshot to avoid recursiveDeleteInstanceWithRetry
    @Test
    public void testAutoCreateSnapshot() throws Exception {
        DebugProperties properties = new DebugProperties();
        properties.setSnapshotCycle("00:00:02:00");
        DbaasInstance instance = builder.setAutoSnapshot(true).setDebugProperties(properties).createInstanceWithRetry(getNamePrefix());
        if (!configuration.isGateway_debug()) {
            instance.updatePreferredStartTime();
        }
        instance.waitAutoSnapshotReachLimit();
        int limit = instance.getCreateInstanceRequest().getSnapshotSettings().getLimit();
        long cycleInSeconds = 30;
        int count = 6;
        while (count != 0) {
            sleepBySeconds(cycleInSeconds);
            assertThat(dbaasApi.listAvailableAndAutoSnapshots(instance.getInstanceid()).size()).isLessThanOrEqualTo(limit);
            count--;
        }
    }

    @Test
    public void testReclaimOfAutoCreateSnapshotDuringUpdateLimit() throws Exception {
        int limit = 2;
        boolean isLimitChanged = false;
        DebugProperties properties = new DebugProperties();
        properties.setSnapshotCycle("00:00:04:00");
        SnapshotSettings snapshotSettings = dbaasApi.generateSnapshotSettings(true);
        snapshotSettings.setEnabled(true);
        snapshotSettings.setLimit(limit);

        DbaasInstance instance = builder.setAutoSnapshot(true).setDebugProperties(properties).setSnapshotSettings(snapshotSettings).createInstanceWithRetry(getNamePrefix());
        instance.updatePreferredStartTime();
        instance.waitAutoSnapshotReachLimit();
        String instanceId = instance.getInstanceid();
        long cycleInSeconds = 60;
        int count = 6;

        //comparator for descending order by update time
        Comparator<Data> comparator = new Comparator<Data>() {
            @Override
            public int compare(Data snapshot1, Data snapshot2) {
                return snapshot2.getUpdatedAt().compareTo(snapshot1.getUpdatedAt());
            }
        };
        while (count != 0) {
            List<Data> oldSnapshotsInLimitList = dbaasApi.listAvailableAndAutoSnapshots(instanceId);
            oldSnapshotsInLimitList.sort(comparator);
            logger.info("old snapshot list");
            oldSnapshotsInLimitList.forEach(e -> logger.info(e.getId()));

            //update the limit
            snapshotSettings.setPreferredStartTime(instance.getInstanceResponse().getSnapshotSettings().getPreferredStartTime());
            if ((count % 2) == 0) {
                snapshotSettings.setLimit(limit + 1);
            } else {
                snapshotSettings.setLimit(limit - 1);
            }
            GetInstanceResponse instanceUpdated = instance.updateSnapshotSettingsWithRetry(snapshotSettings);
            limit = instanceUpdated.getSnapshotSettings().getLimit();
            sleepBySeconds(cycleInSeconds);
            instance.waitAutoSnapshotReachLimit();
            //verify the reclaim logic
            List<Data> newSnapshotsInLimitList = dbaasApi.listAvailableAndAutoSnapshots(instanceId);
            newSnapshotsInLimitList.sort(comparator);
            logger.info("new snapshot list");
            newSnapshotsInLimitList.forEach(e -> logger.info(e.getId()));

            int oldSnapshotsSize = oldSnapshotsInLimitList.size();
            if (oldSnapshotsSize > 1) {
                Data thirdInOldList;
                Data thirdInNewList;
                Data firstInOldList = oldSnapshotsInLimitList.get(0);
                Data secondInOldList = oldSnapshotsInLimitList.get(1);
                Data firstInNewList = newSnapshotsInLimitList.get(0);
                Data secondInNewList = newSnapshotsInLimitList.get(1);
                if (!firstInOldList.equals(firstInNewList)) {
                    if (oldSnapshotsSize == 2) {
                        if (newSnapshotsInLimitList.size() == 2) {
                            isLimitChanged = true;
                            assertThat(firstInOldList.getId()).isEqualToIgnoringCase(secondInNewList.getId());
                        } else if (newSnapshotsInLimitList.size() == 3) {
                            isLimitChanged = true;
                            thirdInNewList = newSnapshotsInLimitList.get(2);
                            assertThat(firstInOldList.getId()).isEqualToIgnoringCase(secondInNewList.getId());
                            assertThat(secondInOldList.getId()).isEqualToIgnoringCase(thirdInNewList.getId());
                        }
                        assertThat(firstInNewList).isNotIn(oldSnapshotsInLimitList);
                    } else {
                        thirdInOldList = oldSnapshotsInLimitList.get(2);
                        if (newSnapshotsInLimitList.size() == 3) {
                            isLimitChanged = true;
                            thirdInNewList = newSnapshotsInLimitList.get(2);
                            assertThat(firstInOldList.getId()).isEqualToIgnoringCase(secondInNewList.getId());
                            assertThat(secondInOldList.getId()).isEqualToIgnoringCase(thirdInNewList.getId());
                        } else if (newSnapshotsInLimitList.size() == 2) {
                            isLimitChanged = true;
                            assertThat(firstInOldList.getId()).isEqualToIgnoringCase(secondInNewList.getId());
                        }
                        assertThat(thirdInOldList).isNotIn(newSnapshotsInLimitList);
                    }
                }
            }
            assertThat(newSnapshotsInLimitList.size()).isLessThanOrEqualTo(limit);
            count--;
        }
        assertThat(isLimitChanged).isTrue();
        dbaasApi.listAvailableAndAutoSnapshots(instanceId).stream().forEach(e -> assertThat(e.getType()).isEqualToIgnoringCase(Constant.AUTO.value()));
    }

    @Test
    public void testCreateSnapshotDuringInstanceProvision() throws Exception {
        DbaasInstance provisioningInstance = builder.createInstance(this.getClass().getName());
        try {
            provisioningInstance.createSnapshot();
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
        }
        provisioningInstance.waitAndGetAvailableInstance();
    }

    @Test
    public void testCreateSnapshotDuringInstanceUnProvision() throws Exception {
        DbaasInstance instanceToUnProvision = builder.createInstanceWithRetry(getNamePrefix());
        instanceToUnProvision.delete();
        try {
            instanceToUnProvision.createSnapshotWithRetry();
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
            //assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.RESOURCE_NOT_FOUND.getCode());
            //TODO error code is not correct.
        }
        instanceToUnProvision.waitInstanceDeleted();
    }


    @Test
    public void testInvalidSnapshotSettingsWhenAutoCreateSnapshot() throws Exception {
        SnapshotSettings snapshotSettings = new SnapshotSettings();
        snapshotSettings.setEnabled(true);
        snapshotSettings.setCycle(-1);
        snapshotSettings.setPreferredStartTime(Constants.INVALID_STRING);
        try {
            builder.setAutoSnapshot(true).setSnapshotSettings(snapshotSettings).createInstanceWithRetry(getNamePrefix());
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
//            assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.INVALID_ARGUMENT.getCode());
        }
    }


    @Test
    public void testCreateManualSnapshotWhileCreateAutoSnapshot() throws Exception {
        DebugProperties properties = new DebugProperties();
        properties.setSnapshotCycle("00:00:02:00");
        DbaasInstance instance = builder.setAutoSnapshot(true).setDebugProperties(properties).createInstanceWithRetry(getNamePrefix());
        if (!configuration.isGateway_debug()) {
            instance.updatePreferredStartTime();
        }
        logger.info("Write 300M data table." + Instant.now());
        instance.loadDataBySize("INDB1", "INTable1", 300 * 1024);
        logger.info("Write 300M data complete." + Instant.now());

        DateTime snapshotTime = getAutoSnapshotTime(instance.getInstanceid());
        if (snapshotTime == null) {
            throw new Exception("snapshot time is null");
        }
        DateTime current = getCurrentTime(instance);
        if (current == null) {
            throw new Exception("current time is null");
        }
        long diffInMillis = snapshotTime.getMillis() - current.getMillis() + 3000;
        logger.info("time diff add 3 seconds in milliseconds is " + diffInMillis);
        Thread.sleep(diffInMillis);

        try {
            instance.createSnapshot();
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
            logger.info("Create snapshot failed when it is already snapshotting");
        }
    }


    @Test
    public void testCreateAutoSnapshotWhileCreatManualSnapshot() throws Exception {
        final int MINUTES_TO_WAIT_FOR_AUTO_SNAPSHOT = 3;
        DebugProperties properties = new DebugProperties();
        properties.setSnapshotCycle("00:00:05:00");
        SnapshotSettings snapshotSettings = dbaasApi.generateSnapshotSettings(false);
        snapshotSettings.setPreferredStartTime(0, -1);
        DbaasInstance instance = builder.setAutoSnapshot(false).setDebugProperties(properties).setSnapshotSettings(snapshotSettings).createInstanceWithRetry(getNamePrefix());

        logger.info("Write 300M data table." + Instant.now());
        instance.loadDataBySize("INDB1", "INTable1", 300 * 1024);
        logger.info("Write 300M data complete." + Instant.now());
        if (!configuration.isGateway_debug()) {
            instance.updatePreferredStartTime();
        }

        //enable auto snapshot
        UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
        updateInstanceRequest.setSnapshotSettings(dbaasApi.generateSnapshotSettings(true));
        updateInstanceRequest.setDebugProperties(properties);
        instance.updateInstanceWithRetry(updateInstanceRequest);

        DateTime snapshotTime = getAutoSnapshotTime(instance.getInstanceid());
        if (snapshotTime == null) {
            throw new Exception("snapshot time is null");
        }
        DateTime current = getCurrentTime(instance);
        if (current == null) {
            throw new Exception("current time is null");
        }
        long diffInMillis = snapshotTime.getMillis() - current.getMillis() - 10000;
        logger.info("time diff minus 10 seconds in milliseconds is " + diffInMillis);
        Thread.sleep(diffInMillis);

        instance.createSnapshot();
        DateTime checkSnapshotTime = snapshotTime.plusMinutes(MINUTES_TO_WAIT_FOR_AUTO_SNAPSHOT);
        logger.info("check snapshot time is: " + checkSnapshotTime.toString());
        current = getCurrentTime(instance);
        logger.info("current time is: " + current.toString());
        diffInMillis = checkSnapshotTime.getMillis() - current.getMillis();
        Thread.sleep(diffInMillis);
        assertThat(instance.listAvailableAndAutoSnapshots().size()).isEqualTo(0);
    }

    private DateTime getAutoSnapshotTime(String instanceId) throws Exception {
        final int numberOfRetries = 5;
        return retryTask.createExecutor().setRetries(numberOfRetries).execute(new Callable<DateTime>() {
            @Override
            public DateTime call() throws Exception {
                BasicEtcdClient client = new BasicEtcdClient(configuration.getEtcd().getBaseUrl());
                logger.info("Dump etcd " + BasicEtcdClient.PATH_NODES_MSSQL);
                List<String> jsons = client.listRaw(BasicEtcdClient.PATH_NODES_MSSQL);
                logger.info("Etcd raw json is: ");
                Logger.logList(jsons);
                DateTime snapshotTime = null;
                for (String json : jsons) {
                    if (json.contains(instanceId)) {
                        Pattern keyValuePattern = Pattern.compile(PREFIX + ".*" + SUFFIX);
                        Matcher keyValueMatcher = keyValuePattern.matcher(json);
                        if (keyValueMatcher.find()) {
                            logger.info("find match pattern" + keyValueMatcher.group());
                            String time = keyValueMatcher.group().substring(PREFIX.length() - 1, keyValueMatcher.group().indexOf(")"));
                            snapshotTime = DateTime.parse(time,
                                    DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss"));
                            logger.info("snapshot time is: " + snapshotTime.toString());
                            return snapshotTime;
                        }
                    }
                }
                return null;
            }
        }, "getFromEtcd");


    }

    @Test
    public void testProvisionInstanceFromSnapshotWithMultipleDB() throws Exception {
        String db1 = DB_TESTDB + "1";
        String db2 = DB_TESTDB + "2";
        String db3 = DB_TESTDB + "3";
        Employee testEmployee2 = RandomEmployee.getRandomEmployee();
        Employee testEmployee3 = RandomEmployee.getRandomEmployee();
        DbaasInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        instance.createDatabase(db1);
        instance.createDatabase(db2);
        instance.createDatabase(db3);
        instance.createTable(db1, SQLStatements.TBL_EMPLOYEE);
        instance.createTable(db2, SQLStatements.TBL_EMPLOYEE);
        instance.createTable(db3, SQLStatements.TBL_EMPLOYEE);
        instance.insertData(db1, testEmployee1);
        instance.insertData(db1, testEmployee2);
        instance.insertData(db2, testEmployee2);
        instance.insertData(db2, testEmployee3);

        GetSnapshotResponse snapshotResponse = instance.createSnapshotWithRetry();
        instance.insertData(db3, testEmployee1);
        instance.insertData(db3, testEmployee3);

        DbaasInstance activeInstanceFromSnapshot = builder.restoreFromSnapshot(getNamePrefix(), snapshotResponse.getId());

        assertThat(instance.getCreateInstanceRequest().getPlan().equals(activeInstanceFromSnapshot.getInstanceResponse().getPlan()));
        assertThat(activeInstanceFromSnapshot.isDatabaseExists(db1)).isTrue();
        assertThat(activeInstanceFromSnapshot.isDataExists(db1, testEmployee1)).isTrue();
        assertThat(activeInstanceFromSnapshot.isDataExists(db1, testEmployee2)).isTrue();
        assertThat(activeInstanceFromSnapshot.isDataExists(db2, testEmployee2)).isTrue();
        assertThat(activeInstanceFromSnapshot.isDataExists(db2, testEmployee3)).isTrue();
        assertThat(activeInstanceFromSnapshot.isDatabaseExists(db3)).isTrue();
        assertThat(activeInstanceFromSnapshot.isDataExists(db3, testEmployee3)).isFalse();
    }

    private DateTime getCurrentTime(DbaasInstance instance) {
        String currentTime = instance.getOriginalCurrentTime();
        currentTime = currentTime.substring(0, currentTime.lastIndexOf("."));
        DateTime current = DateTime.parse(currentTime,
                DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("current time is: " + current.toString());
        return current;
    }
}
