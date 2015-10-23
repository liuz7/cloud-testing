package com.vmware.vchs.base;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.vmware.vchs.base.impl.TestClient;
import com.vmware.vchs.billing.model.Event;
import com.vmware.vchs.common.utils.RetryTask;
import com.vmware.vchs.common.utils.TimeUtils;
import com.vmware.vchs.common.utils.Utils;
import com.vmware.vchs.common.utils.exception.FailException;
import com.vmware.vchs.common.utils.exception.RestException;
import com.vmware.vchs.common.utils.exception.RetryException;
import com.vmware.vchs.constant.Constant;
import com.vmware.vchs.constant.StatusCode;
import com.vmware.vchs.datapath.MssqlConnection;
import com.vmware.vchs.launcher.TestHelper;
import com.vmware.vchs.model.constant.PlanConstants;
import com.vmware.vchs.model.constant.PlanModel;
import com.vmware.vchs.model.portal.common.AsyncResponse;
import com.vmware.vchs.model.portal.common.Data;
import com.vmware.vchs.model.portal.common.ListResponse;
import com.vmware.vchs.model.portal.instance.*;
import com.vmware.vchs.model.portal.services.ServiceConfiguration;
import com.vmware.vchs.model.portal.services.ServicePlan;
import com.vmware.vchs.model.portal.snapshot.CreateSnapshotRequest;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import com.vmware.vchs.model.portal.snapshot.ListSnapshotResponse;
import com.vmware.vchs.test.client.db.MsSqlDaoFactory;
import com.vmware.vchs.test.client.db.MsSqlDataLoader;
import com.vmware.vchs.test.config.Configuration;
import com.vmware.vchs.utils.EtcdMssqlClient;
import com.vmware.vchs.utils.TMUtils;
import com.xebialabs.overthere.OverthereProcess;
import io.netty.util.internal.ConcurrentSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.net.HttpURLConnection;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * Created by liuda on 8/24/15.
 */
public class DbaasApi extends TestClient {
    protected Configuration configuration;
    private RetryTask retryTask;
    //    protected static AtomicInteger instanceIndex = new AtomicInteger(1);
    protected final Logger logger = LoggerFactory.getLogger(DbaasApi.class);
    protected static final int DEFAULT_PORT = 1433;
    protected static final int DEFAULT_RETENTION = 2;
    public static final String DEFAULT_PREFERRED_START_TIME = "01:00";

    public static ConcurrentSet<String> instanceIdsToBeDeleted = new ConcurrentSet<>();

    private static Map<String, PlanModel> planModelMap;

    public Map<String, PlanModel> getPlans() {
        synchronized (DbaasApi.class) {
            if (planModelMap == null) {
                List<ServicePlan> servicePlans = this.getServices().getPlans();
                planModelMap = new HashMap<>();
                for (ServicePlan servicePlan : servicePlans) {
                    List<ServiceConfiguration> configurations = servicePlan.getConfigurations();
                    ServiceConfiguration vcpuConfiguration = configurations.stream().filter(e -> e.getName().equalsIgnoreCase(PlanConstants.PLANVCPU)).collect(Collectors.toList()).get(0);
                    ServiceConfiguration diskConfiguration = configurations.stream().filter(e -> e.getName().equalsIgnoreCase(PlanConstants.PLANDISK)).collect(Collectors.toList()).get(0);
                    ServiceConfiguration memoryConfiguration = configurations.stream().filter(e -> e.getName().equalsIgnoreCase(PlanConstants.PLANMEMORY)).collect(Collectors.toList()).get(0);
                    PlanModel model = new PlanModel();
                    model.setId(servicePlan.getId());
                    model.setCpu(Integer.valueOf(vcpuConfiguration.getValueCanonical()));
                    model.setMemory(Integer.valueOf(memoryConfiguration.getValueCanonical()));
                    model.setDisk(Integer.valueOf(diskConfiguration.getValueCanonical()));

                    planModelMap.put(servicePlan.getName(), model);
                }
            }
        }
        return planModelMap;
    }

    public DbaasApi(Configuration configuration) {
        super(configuration);
        this.configuration = configuration;
        int numberOfRetries = configuration.getRetryTimes();
        List<Integer> timeToWait = Splitter.on(",").trimResults().
                omitEmptyStrings().splitToList(configuration.getRetryTimeToWait()).stream().
                mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
        int timeout = configuration.getRetryTimeout();
        this.retryTask = new RetryTask(numberOfRetries, timeToWait, timeout);
        this.setAuthentication(
                configuration.getPraxisServerConnection().getDbadminUsername(),
                configuration.getPraxisServerConnection().getDbadminPassword());
    }

    public AsyncResponse sendCreateInstanceRequest(CreateInstanceRequest createInstanceRequest) throws Exception {
        AsyncResponse createResponse = createDBInstance(createInstanceRequest);
        return createResponse;
    }

    public void waitAllSnapshotCompact(final String instanceId) throws Exception {
        retryTask.execute(new Callable<Object>() {
            @Override
            public Integer call() throws Exception {
                int compactSnapshotCount = getCompactSnapshotsByInstanceId(instanceId);
                int snapshotCount = getSnapshotsByInstanceId(instanceId);
                if (compactSnapshotCount == snapshotCount) {
                    return snapshotCount;
                } else {
                    logger.info("current snapshot count is: " + snapshotCount + " , compact snapshot count is " + compactSnapshotCount);
                    throw new RetryException("snapshot are not all compact");
                }
            }
        });
    }

    protected int getSnapshotsByInstanceId(String instanceId) {
        return TestHelper.getSnapshotRepository().getCount(instanceId);
    }

    protected int getCompactSnapshotsByInstanceId(String instanceId) {
        return TestHelper.getSnapshotRepository().getCompactCount(instanceId);
    }

    private List<String> getInstanceIds(String namePrefix) {
        List<String> instanceIds = new ArrayList<>();
        ListResponse listResponse = listDBInstances();
        List<ListInstanceItem> listInstanceItems = checkNotNull((List<ListInstanceItem>) listResponse.getData());
        if (StringUtils.isEmpty(namePrefix)) {
            instanceIds.addAll(listInstanceItems.stream().map(src -> src.getId()).collect(Collectors.toList()));
        } else {
            for (ListInstanceItem listInstanceItem : listInstanceItems) {
                if (listInstanceItem.getName().startsWith(namePrefix)) {
                    instanceIds.add(listInstanceItem.getId());
                }
            }
        }
        return instanceIds;
    }

    public boolean tryCleanInstances(Collection<String> instanceIds) {
        if (instanceIds != null && instanceIds.size() > 0) {
            try {
                return retryTask.execute(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        try {
                            ListResponse listResponse = listDBInstances();
                            List<ListInstanceItem> listInstanceItems = checkNotNull((List<ListInstanceItem>) listResponse.getData());
                            boolean finished = true;
                            boolean allCleaned = true;
                            for (ListInstanceItem listInstanceItem : listInstanceItems) {
                                if (instanceIds.contains(listInstanceItem.getId())) {
                                    if (canBeDeletedStatusList.contains(listInstanceItem.getStatus())) {
                                        finished = false;
                                        deleteForDBInstance(listInstanceItem.getId());
                                        TimeUnit.SECONDS.sleep(2);
                                    } else if (listInstanceItem.getStatus().equalsIgnoreCase(StatusCode.DELETING.value())) {
                                        finished = false;
                                    }
                                    allCleaned = false;
                                }
                            }
                            if (finished) {
                                return allCleaned;
                            }
                        } catch (RestException e) {
                            logger.info("Instances cleanup failed, and retry it ...");
                            logger.info(e.toString());
                        }
                        throw new RetryException("Still have instances to be deleted.");
                    }
                });
            } catch (Exception e) {
                logger.info(Utils.getStackTrace(e));
                return false;
            }
        }
        return true;
    }

    public void cleanInstance() {
        cleanInstance(null);
    }

    public void cleanInstance(String namePrefix) {
        List<String> instanceIds = getInstanceIds(namePrefix);
        if (!tryCleanInstances(instanceIds)) {
            instanceIdsToBeDeleted.addAll(getInstanceIds(namePrefix));
        }
    }

    public void deleteRemainingInstances() {
        if (tryCleanInstances(instanceIdsToBeDeleted)) {
            instanceIdsToBeDeleted.clear();
        } else {
            instanceIdsToBeDeleted.retainAll(getInstanceIds(null));
        }
    }

    public Connections generateConnections() {
        DataPath dataPath = new DataPath();
        String port = configuration.getCustomMssqlPort();
        if (port != null && !port.isEmpty()) {
            dataPath.setDestPort(Integer.parseInt(port));
            logger.info("Allowed Port:" + port);
        } else {
            dataPath.setDestPort(DEFAULT_PORT);
        }
        String allowedIpList = configuration.getAllowedIP();
        if (!StringUtils.isEmpty(allowedIpList)) {
            String[] allowedIP = allowedIpList.split(",");
            dataPath.setAllowedIPs(Arrays.asList(allowedIP));
            logger.info("Allowed IP:" + allowedIpList);
        } else {
            dataPath.setAllowedIPs(Lists.newArrayList());
        }
        Connections connections = new Connections();
        connections.setDataPath(dataPath);
        return connections;
    }

//    protected CreatePitrRequest buildPitrInstanceRequest(String namePrefix, String masterPassword, String restoreTime) {
//        CreatePitrRequest createInstanceRequest = new CreatePitrRequest();
//        PlanModel planData = this.getPlans().get(configuration.getPlanName());
//        setBasicAttribute(createInstanceRequest, namePrefix, configuration.getLicenseType(), masterPassword, planData.getDisk(),null,null);
//        createInstanceRequest.setConnections(generateConnections());
//        createInstanceRequest.setPlan(planData.toPlan());
//        createInstanceRequest.setRestoreTime(restoreTime);
//
//        createInstanceRequest.setPitrSettings(generatePitrSettings(false));
//        createInstanceRequest.setSnapshotSettings(generateSnapshotSettings(false));
//        createInstanceRequest.setDebugProperties(generateDebugProperties("00:00:02:00", "00:00:04:00"));
//        return checkNotNull(createInstanceRequest);
//    }
//
//    protected CreateInstanceRequest buildInstanceRequest(String namePrefix) throws Exception {
//        return buildInstanceRequest(namePrefix, MASTER_PASSWORD, false, false, null, null, null);
//    }
//    protected CreateInstanceRequest buildInstanceRequest(String namePrefix, String masterPassword, boolean autosnapshot, boolean backup,SnapshotSettings snapshotSettings, PitrSettings pitrSettings, DebugProperties debugProperties,String description,String maintenanceTime) throws Exception {
//        PlanModel planData = this.getPlans().get(configuration.getPlanName());
//        CreateInstanceRequest createInstanceRequest = new CreateInstanceRequest();
//        setBasicAttribute(createInstanceRequest, namePrefix, configuration.getLicenseType(), masterPassword, planData.getDisk(), description, maintenanceTime);
//        createInstanceRequest.setConnections(generateConnections());
//        createInstanceRequest.setPlan(planData.toPlan());
//        createInstanceRequest.setVersion(configuration.getDbEngineVersion());
//        createInstanceRequest.setEdition(configuration.getEdition());
//        createInstanceRequest.setMasterUsername("johnsmith" + instanceIndex.getAndIncrement());
//        createInstanceRequest.setdREnabled(false);
//        createInstanceRequest.setPitrSettings(pitrSettings == null ? generatePitrSettings(backup) : pitrSettings);
//        createInstanceRequest.setSnapshotSettings(snapshotSettings == null ? generateSnapshotSettings(autosnapshot) : snapshotSettings);
//        createInstanceRequest.setDebugProperties(debugProperties == null ? generateDebugProperties("00:00:01:00", "00:00:04:00"): debugProperties);
//        return checkNotNull(createInstanceRequest);
//    }

//    protected CreateInstanceRequest buildInstanceRequest(String namePrefix, String masterPassword, boolean autosnapshot, boolean backup,SnapshotSettings snapshotSettings, PitrSettings pitrSettings, DebugProperties debugProperties) throws Exception {
//        return buildInstanceRequest(namePrefix,masterPassword,autosnapshot,backup,snapshotSettings,pitrSettings,debugProperties,null,null);
//
//    }

//    private void setBasicAttribute(BaseRequest createInstanceRequest, String namePrefix, String licenseType, String newPassword, int planDiskSize, String description,String maintenanceTime) {
//        int instindx = instanceIndex.getAndIncrement();
//        createInstanceRequest.setName(namePrefix + " Instance_" + instindx);
//        description=(description==null?"this is a DBaaS Instance " + instindx + " for test ":description);
//        createInstanceRequest.setDescription(description);
//        createInstanceRequest.setLicenseType(licenseType);
//        createInstanceRequest.setServiceGroupId(getIamAuthInfo().getServiceGroupId());
//        maintenanceTime=(maintenanceTime==null)?"Sun:00:00":maintenanceTime;
//        createInstanceRequest.setMaintenanceTime(maintenanceTime);
//        createInstanceRequest.setMasterPassword(newPassword);
//        createInstanceRequest.setDiskSize(generateDiskSize(Integer.valueOf(configuration.getDiskSize()), planDiskSize));
//
//    }

//    protected RestoreFromSnapshotRequest buildInstanceRequestFromSnapshot(String namePrefix, String snapshotId, String newPassword) {
//        PlanModel planData = this.getPlans().get(configuration.getPlanName());
//        RestoreFromSnapshotRequest request = new RestoreFromSnapshotRequest();
//        request.setSnapshotId(snapshotId);
//        setBasicAttribute(request, namePrefix, configuration.getLicenseType(), newPassword, planData.getDisk(),null,null);
//
//        request.setConnections(generateConnections());
//        request.setPlan(planData.toPlan());
//        request.setPitrSettings(generatePitrSettings(false));
//        request.setSnapshotSettings(generateSnapshotSettings(false));
//        return request;
//    }

    public int generateDiskSize(int configDiskSize, int planDataDiskSize) {
        return configDiskSize > 0 ? configDiskSize : planDataDiskSize;
    }

    public DebugProperties generateDebugProperties(String cycle, String retention) {
        DebugProperties properties = new DebugProperties();
        properties.setBackupCycle(cycle);
        properties.setBackupStrategy("FLLLF");
        properties.setBackupRetention(retention);
        properties.setSnapshotCycle("00:00:02:00");
        return properties;
    }

    private String parseSnapshotCycle(int cycle) {
        int s = cycle % 60;
        cycle /= 60;
        int m = cycle % 60;
        cycle /= 60;
        int h = cycle % 24;
        int d = cycle / 24;
        return String.format("%02d:%02d:%02d:%02d", d, h, m, s);
    }

    public SnapshotSettings generateSnapshotSettings(boolean enable) {
        SnapshotSettings snapshotSettings = new SnapshotSettings();
        snapshotSettings.setEnabled(enable);
        snapshotSettings.setCycle(1);
        snapshotSettings.setLimit(1);
        snapshotSettings.setNamePrefix("auto-snapshot#");
        snapshotSettings.setPreferredStartTime(DEFAULT_PREFERRED_START_TIME);
        return snapshotSettings;
    }

    public PitrSettings generatePitrSettings(boolean enable) {
        PitrSettings pitrSettings = new PitrSettings();
//        pitrSettings.setEnabled(true);
//        pitrSettings.setRetention(DEFAULT_RETENTION);
        pitrSettings.setEnabled(enable);
        pitrSettings.setRetention(DEFAULT_RETENTION);
        return pitrSettings;
    }


    public MssqlConnection getMssqlConnection(GetInstanceResponse getInstanceResponse, String password) {
        if (getInstanceResponse != null) {
            String host = getInstanceResponse.getIpAddress();
            checkNotNull(host);
            Connections connections = getInstanceResponse.getConnections();
            checkNotNull(connections.getDataPath());
            checkNotNull(connections.getDataPath().getDestPort());
            String port = String.valueOf(connections.getDataPath().getDestPort());
            String username = getInstanceResponse.getMasterUsername();
            return new MssqlConnection(host, port, username, password);
        } else {
            return null;
        }
    }

    public void waitDbConnected(MssqlConnection connection) throws Exception {
        retryTask.execute(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (connection.isConnected()) {
                    return true;
                } else {
                    throw new RetryException("Could not connected to mssql " + connection.toString());
                }
            }
        });
    }

    private abstract class GetTimeFromRestoreWindow {
        protected String[] getRestoreWindowEdgeAfterCurrentTime(final MsSqlDaoFactory jdbcClient, final String instanceId, String[] originalRange) throws Exception {
            final String currentTime = jdbcClient.createSysDao().getCurrentTime();
            final String errorMessage = "Retry timeout for get the restore window for " + instanceId;
            logger.info("Current sql time:" + currentTime);
            return retryTask.execute(new Callable<String[]>() {
                @Override
                public String[] call() throws Exception {
                    GetInstanceResponse getInstanceResponse = getDBInstance(instanceId);
                    checkFailedInstance(getInstanceResponse);
                    AvailableRestoreWindows window = getInstanceResponse.getAvailableRestoreWindows();
                    if (isStatusAvailable(getInstanceResponse.getStatus()) && window != null) {
                        String[] range = new String[]{window.getStartTime(), window.getEndTime()};
                        String targetTime = getTargetTime(range, jdbcClient);
                        if (TimeUtils.toISODateTime(currentTime).isBefore(TimeUtils.parseDate(targetTime))) {
                            return range;
                        }
                    }
                    throw new RetryException(errorMessage + instanceId);
                }
            });
        }

        protected abstract String getTargetTime(String[] currentRange, MsSqlDaoFactory jdbcClient) throws Exception;
    }

    public boolean checkAvailability(String instanceid) {
        try {
            GetInstanceResponse instanceResponse = getDBInstance(instanceid);
            return isStatusAvailable(instanceResponse.getStatus());
        } catch (Exception e) {
            logger.info(Utils.getStackTrace(e));
            return false;
        }
    }

//    protected void sleepBySeconds(long timeout) {
//        try {
//            TimeUnit.SECONDS.sleep(timeout);
//        } catch (InterruptedException e) {
//            logger.info(Utils.getStackTrace(e));
//        }
//    }

    public int getEventsCount() {
        return TestHelper.getEventRepository().getCount();
    }

//    public void deleteAll() {
//        TestHelper.getEventRepository().deleteAll();
//    }

    protected GetInstanceResponse createInstanceWithRetry(CreateInstanceRequest createInstanceRequest) throws Exception {
        AsyncResponse createResponse = sendCreateInstanceRequest(createInstanceRequest);
        return waitAndGetAvailableInstance(createResponse.getId());
    }

    protected GetInstanceResponse createPitrInstanceWithRetry(CreatePitrRequest createInstanceRequest, String instanceId) throws Exception {
        AsyncResponse createResponse = sendLaunchPitrRequest(createInstanceRequest, instanceId);
        return waitAndGetAvailableInstance(createResponse.getId());
    }


    public GetInstanceResponse restoreFromSnapshotWithRetry(RestoreFromSnapshotRequest createInstanceRequest) throws Exception {
        AsyncResponse createResponse = sendRestoreFromSnapshotRequest(createInstanceRequest);
        return waitAndGetAvailableInstance(createResponse.getId());
    }


    protected AsyncResponse sendRestoreFromSnapshotRequest(RestoreFromSnapshotRequest createInstanceRequest) throws Exception {
        AsyncResponse createResponse = restoreFromSnapshotInstance(createInstanceRequest);
        return createResponse;
    }

    protected AsyncResponse sendLaunchPitrRequest(CreatePitrRequest createInstanceRequest, String instanceId) throws Exception {
        AsyncResponse createResponse = launchPitrTask(createInstanceRequest, instanceId);
        return createResponse;
    }

    public void deleteInstanceWithRetry(String instanceId) throws Exception {
        sendDeleteInstanceRequest(instanceId);
        waitInstanceDeleted(instanceId);
    }


    public void deleteSnapshotWithRetry(String snapshotId) throws Exception {
        try {
            deleteSnapshot(snapshotId);
        } catch (Exception e) {
            throw e;
        }
        try {
            this.retryTask.execute(new GetDeletedSnapshotEntityTask(snapshotId));
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
        } catch (Exception e) {
            throw e;
        }
    }

    public void waitSnapshotEmpty(String instanceId) throws Exception {
        retryTask.execute(new Callable<ListSnapshotResponse>() {
            @Override
            public ListSnapshotResponse call() throws Exception {
                ListSnapshotResponse snapshots = listSnapshot();
                List<Data> instances = snapshots.getData().stream().filter(sc -> sc.getSourceInstance().getId().equalsIgnoreCase(instanceId)).collect(Collectors.toList());
                if (instances.size() == 0) {
                    return snapshots;
                } else {
                    throw new RetryException("Failed to get empty Snapshot instances.");
                }
            }
        });
    }

    public GetSnapshotResponse getSnapshotWithRetry(String snapshotId) throws Exception {
        return retryTask.execute(new Callable<GetSnapshotResponse>() {
            @Override
            public GetSnapshotResponse call() throws Exception {
                final String errorMessage = "Failed to get active Snapshot instance: ";
                logger.info("get snapshot id:" + snapshotId);
                GetSnapshotResponse getSnapshotResponse = getSnapshot(snapshotId);
                String status = getSnapshotResponse.getStatus();
                checkFailedSnapshot(getSnapshotResponse);
                if (status.equals(StatusCode.AVAILABLE.value())) {
                    return getSnapshotResponse;
                } else {
                    throw new RetryException(errorMessage + snapshotId);
                }
            }
        });
    }

    protected void checkFailedSnapshot(GetSnapshotResponse getSnapshotResponse) throws FailException {
        String status = getSnapshotResponse.getStatus();
        if (status == null || status.isEmpty() || status.equalsIgnoreCase(StatusCode.FAILED.value())) {
            throw new FailException("Retry exited due to some failures for snapshot: " + getSnapshotResponse.getId());
        }
    }

    public class GetDeletedSnapshotEntityTask implements Callable<ResponseEntity<ListSnapshotResponse>> {

        private String snapshotId;

        public GetDeletedSnapshotEntityTask(String snapshotId) {
            this.snapshotId = snapshotId;
        }

        @Override
        public ResponseEntity<ListSnapshotResponse> call() throws Exception {
            final String errorMessage = "Failed to get deleted Snapshot instance: ";
            ResponseEntity<ListSnapshotResponse> responseEntity;
            try {
                responseEntity = getSnapshotEntity(this.snapshotId);
            } catch (RestException e) {
                throw e;
            }
            if (responseEntity != null) {
                throw new RetryException(errorMessage + this.snapshotId);
            }
            return responseEntity;
        }
    }

    public List<Event> waitBillingEventByInstanceIdReachCount(String instanceId, int rowNum) throws Exception {
        return retryTask.execute(new Callable<List<Event>>() {
            @Override
            public List<Event> call() throws Exception {
                List<Event> results = TestHelper.getEventRepository().findByInstanceId(instanceId);
                if (results != null && results.size() >= rowNum) {
                    return results;
                } else {
                    logger.info("current event is: " + results.toString());
                    throw new RetryException("Failed to get billing events.");
                }
            }
        });
    }

    public void waitSnapshotLargeThanOrEqualToMin(String instanceId, int minRow) throws Exception {
        retryTask.execute(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int snapshotCount = getSnapshotsByInstanceId(instanceId);
                if (snapshotCount >= minRow) {
                    return snapshotCount;
                } else {
                    logger.info("current snapshot count is: " + snapshotCount);
                    throw new RetryException("snapshot are not all compact");
                }
            }
        });
    }

    public void deleteInstance(String instanceId) throws Exception {
        sendDeleteInstanceRequest(instanceId);
    }

    protected void sendDeleteInstanceRequest(String instanceId) throws Exception {
        deleteForDBInstance(instanceId);
    }

    public void waitInstanceDeleted(final String instanceId) throws Exception {
        this.retryTask.execute(new Callable<ListResponse>() {
            @Override
            public ListResponse call() throws Exception {
                ListResponse listResponse = listDBInstances();
                List<ListInstanceItem> listInstanceItems = (List<ListInstanceItem>) listResponse.getData();
                for (ListInstanceItem listInstanceItem : listInstanceItems) {
                    if (listInstanceItem.getId().equalsIgnoreCase(instanceId)) {
                        throw new RetryException(instanceId + " is still deleting.");
                    }
                }
                return listResponse;
            }
        });
    }

    protected AsyncResponse sendUpdateRequest(String instanceId, UpdateInstanceRequest updateInstanceRequest) throws Exception {
        return updateDBInstance(instanceId, updateInstanceRequest);
    }

    protected GetInstanceResponse updateInstanceWithRetry(String instanceId, UpdateInstanceRequest updateInstanceRequest) throws Exception {
        AsyncResponse createResponse = sendUpdateRequest(instanceId, updateInstanceRequest);
        return waitAndGetAvailableInstance(createResponse.getId());
    }

    public void waitAutoSnapshotReachLimit(String instanceId, int limit) throws Exception {
        this.retryTask.execute(new Callable<List<Data>>() {
            @Override
            public List<Data> call() throws Exception {
                List<Data> snapshots = listAvailableAndAutoSnapshots(instanceId);
                if (snapshots.size() == limit) {
                    return snapshots;
                } else {
                    throw new RetryException("Failed to get limited Snapshot instances.");
                }
            }
        });
    }

    public List<Data> listSnapshots(String instanceId) {
        List<Data> snapshotResponseListByInstanceId = listSnapshot().getData().stream().filter(x -> x.getSourceInstance().getId().equals(instanceId)).collect(Collectors.toList());
        return snapshotResponseListByInstanceId;
    }

    public List<Data> listAvailableSnapshots(String instanceId) {
        return listSnapshots(instanceId).stream().filter(x -> x.getStatus().equals(StatusCode.AVAILABLE.value())).collect(Collectors.toList());
    }

    public List<Data> listAvailableAndAutoSnapshots(String instanceId) {
        return listAvailableSnapshots(instanceId).stream().filter(x -> x.getType().equals(Constant.AUTO.value())).collect(Collectors.toList());
    }


    public class DropDatabaseTask implements Callable<Boolean> {

        private MsSqlDaoFactory jdbcClient;
        private String dbName;

        public DropDatabaseTask(MsSqlDaoFactory jdbcClient, String dbName) {
            this.jdbcClient = jdbcClient;
            this.dbName = dbName;
        }

        @Override
        public Boolean call() throws Exception {
            boolean result;
            try {
                this.jdbcClient.createSysDao().dropDatabase(this.dbName);
                result = true;
            } catch (Exception e) {
                throw new RetryException("Failed to drop database " + this.dbName);
            }
            return new Boolean(result);
        }
    }

    public class ProcessTask implements Callable<Integer> {

        private OverthereProcess overthereProcess;

        public ProcessTask(OverthereProcess overthereProcess) {
            this.overthereProcess = overthereProcess;
        }

        @Override
        public Integer call() throws Exception {
            int result;
            try {
                result = this.overthereProcess.waitFor();
            } catch (Exception e) {
                throw e;
            }
            return Integer.valueOf(result);
        }
    }

    public class MaintenanceTask implements Callable<String> {

        private String nodeId;

        public MaintenanceTask(String nodeId) {
            this.nodeId = nodeId;
        }

        @Override
        public String call() throws Exception {
            String result;
            try {
                result = TMUtils.startMaintenance(nodeId);
                logger.info(result);
            } catch (Exception e) {
                throw e;
            }
            return result;
        }
    }

    private String getNodeStatus(String nodeId, String... nodeStatus) {
        String result;
        List<String> nodeStatusList = Lists.newArrayList(nodeStatus);
        String listString = String.join(", ", nodeStatusList);
        result = new EtcdMssqlClient(configuration.getEtcd().getBaseUrl()).getNodeStatusByNodeId(nodeId);
        if (nodeStatusList.contains(result)) {
            return result;
        } else {
            throw new RetryException("Failed to get " + listString + " Status.");
        }
    }

    protected void checkFailedInstance(GetInstanceResponse getInstanceResponse) throws FailException {
        String status = getInstanceResponse.getStatus();
        if (status == null || status.isEmpty() || status.equalsIgnoreCase(StatusCode.FAILED.value())) {
            throw new FailException("Retry exited due to some failures for instance: " + getInstanceResponse.getId() + " due to reason:" + getInstanceResponse.getFailedReason());
        }
    }


    protected long getCycleAsSeconds(String cycle) {
        checkNotNull(cycle);
        String[] cycleValues = cycle.split(":");
        int length = cycleValues.length;
        Duration seconds;
        if (length == 4) {
            Duration days = Duration.ofDays(Long.parseLong(cycleValues[length - 4]));
            Duration hours = days.plusHours(Long.parseLong(cycleValues[length - 3]));
            Duration minutes = hours.plusMinutes(Long.parseLong(cycleValues[length - 2]));
            seconds = minutes.plusSeconds(Long.parseLong(cycleValues[length - 1]));
        } else {
            throw new FailException("Invalid cycle value" + cycle);
        }
        return seconds.getSeconds();
    }

    protected void dropDatabase(MsSqlDaoFactory msSqlDaoFactory, String dbName) throws Exception {
        new RetryTask(6, Lists.newArrayList(1, 2, 4, 8)).execute(new DropDatabaseTask(msSqlDaoFactory, dbName));
    }

    protected int loadDataBySize(MsSqlDataLoader msSqlDataLoader, String dbName, String tableName, long totalSizeInMB) throws Exception {
        msSqlDataLoader.useDatabase(dbName);
        msSqlDataLoader.createTable(tableName);
        int i = 1;
        Instant totalStart = Instant.now();
        while (msSqlDataLoader.getDBSize() * 8 / 1024 < totalSizeInMB) {
            Instant start = Instant.now();
            msSqlDataLoader.insert(tableName, configuration.getTestFile());
            Duration duration = Duration.between(start, Instant.now());
            logger.info("Blob " + i + " took " + duration.getSeconds() + " seconds.");
            i++;
        }
        Duration totalDuration = Duration.between(totalStart, Instant.now());
        logger.info("Totally took " + totalDuration.getSeconds() + " seconds.");
        return i;
    }

    public CreateSnapshotRequest buildSnapshotRequest(String instanceId) {
        CreateSnapshotRequest createSnapshotRequest = new CreateSnapshotRequest();
        createSnapshotRequest.setInstanceId(instanceId);
        createSnapshotRequest.setDescription("this is a snapshot request for Source Instance: " + instanceId);
        createSnapshotRequest.setName("test-snapshot");
        return createSnapshotRequest;
    }


    public GetSnapshotResponse waitAndGetAvailableSnapshot(final String snapshotId) throws Exception {
        return retryTask.execute(new Callable<GetSnapshotResponse>() {
            @Override
            public GetSnapshotResponse call() throws Exception {
                final String errorMessage = "Failed to get active Snapshot instance: ";
                logger.info("get snapshot id:" + snapshotId);
                GetSnapshotResponse getSnapshotResponse = getSnapshot(snapshotId);
                String status = getSnapshotResponse.getStatus();
                if (status == null || status.isEmpty() || status.equalsIgnoreCase(StatusCode.FAILED.value())) {
                    throw new FailException("Retry exited due to some failures for snapshot: " + getSnapshotResponse.getId());
                }
                if (status.equals(StatusCode.AVAILABLE.value())) {
                    return getSnapshotResponse;
                } else {
                    throw new RetryException(errorMessage + snapshotId);
                }
            }
        });
    }

    public GetInstanceResponse waitAndGetAvailableInstance(final String instanceId) throws Exception {
        return retryTask.execute(new Callable<GetInstanceResponse>() {
            @Override
            public GetInstanceResponse call() throws Exception {
                final String errorMessage = "Failed to get active Db instance: ";
                GetInstanceResponse getInstanceResponse = getDBInstance(instanceId);
                String status = getInstanceResponse.getStatus();
                if (status == null || status.isEmpty() || status.equalsIgnoreCase(StatusCode.FAILED.value())) {
                    throw new FailException("Retry exited due to some failures for instance: " + getInstanceResponse.getId() + " due to reason:" + getInstanceResponse.getFailedReason());
                }
                if (isStatusAvailable(getInstanceResponse.getStatus())) {
                    return getInstanceResponse;
                } else {
                    throw new RetryException(errorMessage + instanceId);
                }
            }
        });
    }

    public boolean tryCleanSnapshots(Collection<String> snapshotIds) {
        if (snapshotIds.size() > 0) {
            try {
                retryTask.execute(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        try {
                            ListSnapshotResponse listResponse = listSnapshot();
                            checkNotNull(listResponse);
                            List<String> statusCanDelete = Lists.newArrayList(StatusCode.AVAILABLE.value(), StatusCode.FAILED.value());
                            List<Data> listInstanceItems = checkNotNull(listResponse.getData());

                            boolean finished = true;
                            for (Data listInstanceItem : listInstanceItems) {
                                if (snapshotIds.contains(listInstanceItem.getId())) {
                                    if (statusCanDelete.contains(listInstanceItem.getStatus())) {
                                        finished = false;
                                        deleteSnapshot(listInstanceItem.getId());
                                        TimeUnit.SECONDS.sleep(2);
                                    } else if (listInstanceItem.getStatus().equalsIgnoreCase(StatusCode.DELETING.value())) {
                                        finished = false;
                                    }
                                }
                            }
                            if (finished) {
                                return true;
                            }
                        } catch (RestException e) {
                            logger.info("Snapshots cleanup failed, and retry it ...");
                            logger.info(e.toString());
                        }
                        throw new RetryException("Still have snapshots to be deleted.");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public void cleanSnapshots(String namePrefix) throws Exception {
        Set<String> snapshotIds = new HashSet<>();
        ListSnapshotResponse listResponse = listSnapshot();
        List<Data> listInstanceItems = checkNotNull(listResponse.getData());
        for (Data listInstanceItem : listInstanceItems) {
            if (listInstanceItem.getSourceInstance().getName().startsWith(namePrefix)) {
                snapshotIds.add(listInstanceItem.getId());
            }
        }
        tryCleanSnapshots(snapshotIds);
    }

    public void verifyUpdatedInstanceWithData(GetInstanceResponse instanceUpdated, UpdateInstanceRequest dataToBeUpdated, boolean isUpdateSuccessful) throws Exception {
        assertThat(instanceUpdated.getStatus()).isIn(getAvailableStatusList());
        assertThat(instanceUpdated).isNotNull();
        if (dataToBeUpdated != null) {
            if (isUpdateSuccessful) {
                if (dataToBeUpdated.getMasterPassword() != null) {
                    MssqlConnection connection = getMssqlConnection(instanceUpdated, dataToBeUpdated.getMasterPassword());
                    assertThat(connection.isConnected()).isTrue();
                }
                if (dataToBeUpdated.getMaintenanceTime() != null) {
                    assertThat(instanceUpdated.getMaintenanceTime()).isEqualTo(dataToBeUpdated.getMaintenanceTime());
                }
                if (dataToBeUpdated.getDiskSize() != 0) {
                    assertThat(instanceUpdated.getDiskSize()).isEqualTo(dataToBeUpdated.getDiskSize());
                }
                if (dataToBeUpdated.getPitrSettings() != null) {
                    assertThat(instanceUpdated.getPitrSettings()).isEqualTo(dataToBeUpdated.getPitrSettings());
                }
                if (dataToBeUpdated.getSnapshotSettings() != null) {
                    assertThat(instanceUpdated.getSnapshotSettings()).isEqualTo(dataToBeUpdated.getSnapshotSettings());
                }
            } else {
                if (dataToBeUpdated.getMasterPassword() != null) {
                    MssqlConnection connection = getMssqlConnection(instanceUpdated, dataToBeUpdated.getMasterPassword());
                    assertThat(connection.isConnected()).isFalse();
                }
                if (dataToBeUpdated.getMaintenanceTime() != null) {
                    assertThat(instanceUpdated.getMaintenanceTime()).isNotEqualTo(dataToBeUpdated.getMaintenanceTime());
                }
                if (dataToBeUpdated.getDiskSize() != 0) {
                    assertThat(instanceUpdated.getDiskSize()).isNotEqualTo(dataToBeUpdated.getDiskSize());
                }
                if (dataToBeUpdated.getPitrSettings() != null) {
                    assertThat(instanceUpdated.getPitrSettings()).isNotEqualTo(dataToBeUpdated.getPitrSettings());
                }
                if (dataToBeUpdated.getSnapshotSettings() != null) {
                    assertThat(instanceUpdated.getSnapshotSettings()).isNotEqualTo(dataToBeUpdated.getSnapshotSettings());
                }
            }
        }
    }

    public void verifySuccessfulUpdatedInstanceWithConcurrentData(GetInstanceResponse instanceUpdated, List<UpdateInstanceRequest> dataToBeUpdated) throws Exception {
        assertThat(instanceUpdated.getStatus()).isIn(getAvailableStatusList());
        assertThat(instanceUpdated).isNotNull();
        List<UpdateInstanceRequest> result = dataToBeUpdated.stream()
                .filter(t -> getMssqlConnection(instanceUpdated, t.getMasterPassword()).isConnected())
                .collect(Collectors.toList());
        assertThat(result.size()).isPositive();
        result = dataToBeUpdated.stream()
                .filter(t -> t.getMaintenanceTime() != null)
                .filter(t -> t.getMaintenanceTime().equalsIgnoreCase(instanceUpdated.getMaintenanceTime()))
                .collect(Collectors.toList());
        assertThat(result.size()).isNotNegative();
        result = dataToBeUpdated.stream()
                .filter(t -> t.getDiskSize() != 0)
                .filter(t -> t.getDiskSize() == (instanceUpdated.getDiskSize()))
                .collect(Collectors.toList());
        assertThat(result.size()).isNotNegative();
        result = dataToBeUpdated.stream()
                .filter(t -> t.getPitrSettings() != null)
                .filter(t -> t.getPitrSettings().equals(instanceUpdated.getPitrSettings()))
                .collect(Collectors.toList());
        assertThat(result.size()).isNotNegative();
        result = dataToBeUpdated.stream()
                .filter(t -> t.getSnapshotSettings() != null)
                .filter(t -> t.getSnapshotSettings().equals(instanceUpdated.getSnapshotSettings()))
                .collect(Collectors.toList());
        assertThat(result.size()).isNotNegative();
    }

    public static List<String> canBeDeletedStatusList = Lists.newArrayList(StatusCode.FAILED.value(), StatusCode.AVAILABLE.value(), StatusCode.PROVISIONED.value());

    public List<String> getAvailableStatusList() {
        if (configuration.isSns()) {
            return Lists.newArrayList(StatusCode.AVAILABLE.value());
        } else {
            return Lists.newArrayList(StatusCode.PROVISIONED.value(), StatusCode.AVAILABLE.value());
        }
    }

    public boolean isStatusAvailable(String status) {
        return getAvailableStatusList().contains(status);
    }

    public List<Data> getInstanceSnapshots(String instanceId) {
        ListSnapshotResponse listResponse = listSnapshot();
        checkNotNull(listResponse);
        List<Data> listInstanceItems = checkNotNull(listResponse.getData());
        List<Data> snapshots = new ArrayList<>();
        for (Data item : listInstanceItems) {
            if (item.getSourceInstance().getId().equals(instanceId)) {
                snapshots.add(item);
            }
        }
        return snapshots;
    }
}
