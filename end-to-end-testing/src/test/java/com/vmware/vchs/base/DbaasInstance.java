package com.vmware.vchs.base;

import com.google.common.collect.Lists;
import com.vmware.vchs.billing.model.Event;
import com.vmware.vchs.common.utils.RetryTask;
import com.vmware.vchs.common.utils.exception.RetryException;
import com.vmware.vchs.constant.Constant;
import com.vmware.vchs.constant.Constants;
import com.vmware.vchs.constant.StatusCode;
import com.vmware.vchs.datapath.DataGenerator;
import com.vmware.vchs.datapath.DataPathTestModel;
import com.vmware.vchs.datapath.MssqlConnection;
import com.vmware.vchs.gateway.model.BackupResource;
import com.vmware.vchs.gateway.model.SnapshotResource;
import com.vmware.vchs.launcher.TestHelper;
import com.vmware.vchs.model.constant.PlanModel;
import com.vmware.vchs.model.portal.common.AsyncResponse;
import com.vmware.vchs.model.portal.common.Data;
import com.vmware.vchs.model.portal.instance.*;
import com.vmware.vchs.model.portal.networks.UpdateConnectionRequest;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import com.vmware.vchs.test.client.db.MsSqlDaoFactory;
import com.vmware.vchs.test.client.db.SQLStatements;
import com.vmware.vchs.test.client.db.model.Employee;
import com.vmware.vchs.test.config.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by liuda on 8/26/15.
 */
public class DbaasInstance {
    private String password = MASTER_PASSWORD;
    public static final String MASTER_PASSWORD = "ca$hc0w";
    protected static AtomicInteger instanceIndex = new AtomicInteger(1);

    public static class DbaasInstanceBuilder {
        private boolean autoSnapshot = false;
        private boolean backup = false;
        private Connections connections;
        private SnapshotSettings snapshotSettings;
        private DebugProperties debugProperties;
        private PitrSettings pitrSettings;
        private Plan plan;
        private String description;
        private DbaasApi dbaasApi;
        private String maintenanceTime;
        private String version;
        private Configuration configuration = TestHelper.getConfiguration();
        private boolean isPlanEmpty = false;

        public boolean isPlanEmpty() {
            return isPlanEmpty;
        }

        public DbaasInstanceBuilder setIsPlanEmpty(boolean isPlanEmpty) {
            this.isPlanEmpty = isPlanEmpty;
            return this;
        }

        public String getVersion() {
            return version;
        }

        public DbaasInstanceBuilder setVersion(String version) {
            this.version = version;
            return this;
        }

        public String getMaintenanceTime() {
            return maintenanceTime;
        }

        public DbaasInstanceBuilder setMaintenanceTime(String maintenanceTime) {
            this.maintenanceTime = maintenanceTime;
            return this;
        }

        public DbaasInstanceBuilder(DbaasApi dbaasApi) {
            this.dbaasApi = dbaasApi;
        }

        private void setBasicAttribute(BaseRequest createInstanceRequest, String namePrefix, String newPassword, int planDiskSize) {
            int instindx = instanceIndex.getAndIncrement();
            createInstanceRequest.setName(namePrefix + " Instance_" + instindx);
            String desc = (description == null ? "this is a DBaaS Instance " + instindx + " for test " : description);
            createInstanceRequest.setDescription(desc);
            createInstanceRequest.setLicenseType(configuration.getLicenseType());
            createInstanceRequest.setServiceGroupId(dbaasApi.getIamAuthInfo().getServiceGroupId());
            String maintenance = (maintenanceTime == null) ? "Sun:00:00" : maintenanceTime;
            createInstanceRequest.setMaintenanceTime(maintenance);
            createInstanceRequest.setMasterPassword(newPassword);
            createInstanceRequest.setDiskSize(dbaasApi.generateDiskSize(Integer.valueOf(configuration.getDiskSize()), planDiskSize));

        }

        public DataPathInstance createInstance(String namePrefix) throws Exception {
            CreateInstanceRequest createInstanceRequest = buildInstanceRequest(namePrefix, MASTER_PASSWORD);
            AsyncResponse getInstanceResponse = dbaasApi.sendCreateInstanceRequest(createInstanceRequest);
            return new DataPathInstance(getInstanceResponse, createInstanceRequest);
        }

        public CreateInstanceRequest buildInstanceRequest(String namePrefix, String masterPassword) throws Exception {
            PlanModel planData = dbaasApi.getPlans().get(configuration.getPlanName());
            CreateInstanceRequest createInstanceRequest = new CreateInstanceRequest();
            setBasicAttribute(createInstanceRequest, namePrefix, masterPassword, planData.getDisk());
            createInstanceRequest.setConnections(dbaasApi.generateConnections());
            createInstanceRequest.setPlan(generatePlan(planData));
            String dbVersion = this.version == null ? configuration.getDbEngineVersion() : version;
            createInstanceRequest.setVersion(dbVersion);
            createInstanceRequest.setEdition(configuration.getEdition());
            createInstanceRequest.setMasterUsername("johnsmith" + instanceIndex.getAndIncrement());
            createInstanceRequest.setPitrSettings(pitrSettings == null ? dbaasApi.generatePitrSettings(backup) : pitrSettings);
            createInstanceRequest.setSnapshotSettings(snapshotSettings == null ? dbaasApi.generateSnapshotSettings(this.autoSnapshot) : snapshotSettings);
            createInstanceRequest.setDebugProperties(debugProperties == null ? dbaasApi.generateDebugProperties("00:00:01:00", "00:00:04:00") : debugProperties);
            return checkNotNull(createInstanceRequest);
        }

        public Plan generatePlan(PlanModel planData) {
            Plan requestPlan = null;
            if (!isPlanEmpty) {
                requestPlan = plan == null ? planData.toPlan() : plan;
            }
            return requestPlan;
        }

        public DataPathInstance createInstanceWithRetry(String namePrefix) throws Exception {
            CreateInstanceRequest createInstanceRequest = buildInstanceRequest(namePrefix, MASTER_PASSWORD);
            GetInstanceResponse getInstanceResponse = dbaasApi.createInstanceWithRetry(createInstanceRequest);
            DataPathInstance dataPathInstance = new DataPathInstance(getInstanceResponse, createInstanceRequest);
            //dataPathInstance.setInstanceResponse(dataPathInstance.updatePreferredStartTime());
            return dataPathInstance;
        }

        public DataPathInstance createInstanceEntity(String namePrefix) throws Exception {
            CreateInstanceRequest createInstanceRequest = buildInstanceRequest(namePrefix, MASTER_PASSWORD);
            ResponseEntity<AsyncResponse> response = dbaasApi.createDBInstanceEntity(createInstanceRequest);
            DataPathInstance dataPathInstance = new DataPathInstance(response, createInstanceRequest);
            //dataPathInstance.setInstanceResponse(dataPathInstance.updatePreferredStartTime());
            return dataPathInstance;
        }

        public DataPathInstance restoreFromSnapshot(String namePrefix, String snapshotid) throws Exception {
            return restoreFromSnapshot(namePrefix, snapshotid, MASTER_PASSWORD);
        }

        public DataPathInstance restoreFromSnapshot(String namePrefix, String snapshotid, String password) throws Exception {
            RestoreFromSnapshotRequest createInstanceRequest = buildInstanceRequestFromSnapshot(namePrefix, snapshotid, password);
            GetInstanceResponse getInstanceResponse = dbaasApi.restoreFromSnapshotWithRetry(createInstanceRequest);
            DataPathInstance dataPathInstance = new DataPathInstance(getInstanceResponse, password, false);
            //dataPathInstance.setInstanceResponse(dataPathInstance.updatePreferredStartTime());
            return dataPathInstance;
        }

        protected CreatePitrRequest buildPitrInstanceRequest(String namePrefix, String masterPassword, String restoreTime) {
            CreatePitrRequest createInstanceRequest = new CreatePitrRequest();
            PlanModel planData = dbaasApi.getPlans().get(configuration.getPlanName());
            setBasicAttribute(createInstanceRequest, namePrefix, masterPassword, planData.getDisk());
            createInstanceRequest.setConnections(dbaasApi.generateConnections());
            createInstanceRequest.setPlan(generatePlan(planData));
            createInstanceRequest.setRestoreTime(restoreTime);

            createInstanceRequest.setPitrSettings(dbaasApi.generatePitrSettings(false));
            createInstanceRequest.setSnapshotSettings(dbaasApi.generateSnapshotSettings(false));
            createInstanceRequest.setDebugProperties(dbaasApi.generateDebugProperties("00:00:02:00", "00:00:04:00"));
            return checkNotNull(createInstanceRequest);
        }

        protected CreateInstanceRequest buildInstanceRequest(String namePrefix) throws Exception {
            return buildInstanceRequest(namePrefix, MASTER_PASSWORD);
        }

        public RestoreFromSnapshotRequest buildInstanceRequestFromSnapshot(String namePrefix, String snapshotId, String newPassword) {
            PlanModel planData = dbaasApi.getPlans().get(configuration.getPlanName());
            RestoreFromSnapshotRequest request = new RestoreFromSnapshotRequest();
            request.setSnapshotId(snapshotId);
            setBasicAttribute(request, namePrefix, newPassword, planData.getDisk());

            request.setConnections(dbaasApi.generateConnections());
            request.setPlan(generatePlan(planData));
            request.setPitrSettings(dbaasApi.generatePitrSettings(false));
            request.setSnapshotSettings(dbaasApi.generateSnapshotSettings(false));
            return request;
        }

        protected DbaasInstance restoreFromBackup(String namePrefix, GetInstanceResponse getInstanceResponse, String restoreTime, String password) throws Exception {
            CreatePitrRequest requestInstanceFromBackup = buildPitrInstanceRequest(namePrefix, password, restoreTime);
            GetInstanceResponse activeInstanceFromBackup = dbaasApi.createPitrInstanceWithRetry(requestInstanceFromBackup, getInstanceResponse.getId());
            DbaasInstance dataPathInstance = new DbaasInstance(activeInstanceFromBackup, password, false);
            //dataPathInstance.setInstanceResponse(dataPathInstance.updatePreferredStartTime());
            return dataPathInstance;
        }

        protected DbaasInstance createInstanceFromBackup(GetInstanceResponse getInstanceResponse, CreatePitrRequest createPitrRequest) throws Exception {
            GetInstanceResponse activeInstanceFromBackup = dbaasApi.createPitrInstanceWithRetry(createPitrRequest, getInstanceResponse.getId());
            DbaasInstance dataPathInstance = new DbaasInstance(activeInstanceFromBackup, createPitrRequest.getMasterPassword(), false);
            //dataPathInstance.setInstanceResponse(dataPathInstance.updatePreferredStartTime());
            return dataPathInstance;
        }

        public String getDescription() {
            return description;
        }

        public DbaasInstanceBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public boolean isAutoSnapshot() {
            return autoSnapshot;
        }

        public DbaasInstanceBuilder setAutoSnapshot(boolean autoSnapshot) {
            this.autoSnapshot = autoSnapshot;
            return this;
        }

        public boolean isBackup() {
            return backup;
        }

        public DbaasInstanceBuilder setBackup(boolean backup) {
            this.backup = backup;
            return this;
        }

        public Plan getPlan() {
            return plan;
        }

        public DbaasInstanceBuilder setPlan(Plan plan) {
            this.plan = plan;
            return this;
        }

        public Connections getConnections() {
            return connections;
        }

        public DbaasInstanceBuilder setConnections(Connections connections) {
            this.connections = connections;
            return this;
        }

        public SnapshotSettings getSnapshotSettings() {
            return snapshotSettings;
        }

        public DbaasInstanceBuilder setSnapshotSettings(SnapshotSettings snapshotSettings) {
            this.snapshotSettings = snapshotSettings;
            return this;
        }

        public DebugProperties getDebugProperties() {
            return debugProperties;
        }

        public DbaasInstanceBuilder setDebugProperties(DebugProperties debugProperties) {
            this.debugProperties = debugProperties;
            return this;
        }

        public PitrSettings getPitrSettings() {
            return pitrSettings;
        }

        public DbaasInstanceBuilder setPitrSettings(PitrSettings pitrSettings) {
            this.pitrSettings = pitrSettings;
            return this;
        }


    }

    protected final Logger logger = LoggerFactory.getLogger(DbaasInstance.class);
    private GetInstanceResponse instanceResponse;
    private AsyncResponse instanceInCreatingResponse;
    private ResponseEntity<AsyncResponse> instanceInCreatingEntityResponse;
    private CreateInstanceRequest createInstanceRequest;
    protected MssqlConnection mssqlConnection;
    private final DbaasApi dbaasApi = new DbaasApi(TestHelper.getConfiguration());
    public GetSnapshotResponse snapshotResponse;
    public ResponseEntity<AsyncResponse> snapshotResponseEntity;
    private String instanceid;
    private String snapshotid;

    public MssqlConnection getMssqlConnection() {
        return mssqlConnection;
    }

    public void setMssqlConnection(MssqlConnection mssqlConnection) {
        this.mssqlConnection = mssqlConnection;
    }

    public String getStatus() {
        if (this.instanceResponse != null) {
            return this.instanceResponse.getStatus();
        }
        if (this.instanceInCreatingResponse != null) {
            return this.instanceInCreatingResponse.getStatus();
        }
        if (instanceInCreatingEntityResponse != null) {
            return this.instanceInCreatingEntityResponse.getBody().getStatus();
        }
        return null;
    }

    public int getStatusCode() {
        return this.instanceInCreatingEntityResponse.getStatusCode().value();
    }

    public GetSnapshotResponse getSnapshotResponse() {
        return snapshotResponse;
    }

    public void setSnapshotResponse(GetSnapshotResponse snapshotResponse) {
        this.snapshotResponse = snapshotResponse;
    }

    public AsyncResponse getInstanceInCreatingResponse() {
        return instanceInCreatingResponse;
    }

    public void setInstanceInCreatingResponse(AsyncResponse instanceInCreatingResponse) {
        this.instanceInCreatingResponse = instanceInCreatingResponse;
    }

    public GetInstanceResponse getInstanceResponse() {
        return instanceResponse;
    }

    public void setInstanceResponse(GetInstanceResponse instanceResponse) {
        this.instanceResponse = instanceResponse;
    }

    public CreateInstanceRequest getCreateInstanceRequest() {
        return createInstanceRequest;
    }

    public void setCreateInstanceRequest(CreateInstanceRequest createInstanceRequest) {
        this.createInstanceRequest = createInstanceRequest;
    }

    public String getSnapshotid() {
        return snapshotid;
    }

    public GetInstanceResponse waitAndGetAvailableInstance() throws Exception {
        GetInstanceResponse response = dbaasApi.waitAndGetAvailableInstance(this.instanceid);
        mssqlConnection = dbaasApi.getMssqlConnection(response, this.password);
        this.instanceResponse = response;
        dbaasApi.waitDbConnected(mssqlConnection);
        mssqlConnection.createDatabase(SQLStatements.DB_TESTDB);
        createTable(SQLStatements.DB_TESTDB, SQLStatements.TBL_EMPLOYEE);
        return response;
    }

    public void createTable(String database, String table) throws DataAccessException {
        if (!mssqlConnection.isTableExists(database, table)) {
            StringBuffer sb = new StringBuffer();
            sb.append(String.format("CREATE TABLE %s.dbo.%s (", database, table));
            sb.append(" id int PRIMARY KEY,");
            sb.append(" name varchar(255),");
            sb.append(" role varchar(255),");
            sb.append(")");
            mssqlConnection.getTemplate().execute(sb.toString());
        }
    }

    public boolean isConnected() throws Exception {
        return this.mssqlConnection.isConnected();
    }

    public void waitInstanceDeleted(final String instanceId) throws Exception {
        dbaasApi.waitInstanceDeleted(this.instanceid);
    }

    public void setSnapshotid(String snapshotid) {
        this.snapshotid = snapshotid;
    }

    public String getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(String instanceid) {
        this.instanceid = instanceid;
    }

    public boolean checkAvailability() {
        return dbaasApi.checkAvailability(this.instanceResponse.getId());
    }

    public DbaasInstance(GetInstanceResponse instanceResponse, CreateInstanceRequest createInstanceRequest) throws Exception {
        this(instanceResponse, createInstanceRequest, MASTER_PASSWORD, true);
    }


    public void recursiveDeleteInstanceWithRetry() throws Exception {
        for (int num = 0; num < 10; num++) {
            try {
                GetInstanceResponse getInstanceResponse = waitAndGetAvailableInstance();
                if (getInstanceResponse != null) {
                    delete();
                    waitInstanceDeleted();
                    break;
                }
            } catch (Exception e) {
                logger.info("The" + num + "time to delete auto snapshot failed");
            }
        }
    }

    public List<Event> waitBillingEventByInstanceIdReachCount(int minRow) throws Exception {
        return dbaasApi.waitBillingEventByInstanceIdReachCount(this.instanceid, minRow);
    }

    public void waitSnapshotLargeThanOrEqualToMin(int minRow) throws Exception {
        dbaasApi.waitSnapshotLargeThanOrEqualToMin(this.instanceid, minRow);
    }

    public void waitSnapshotEmpty() throws Exception {
        dbaasApi.waitSnapshotEmpty(this.instanceid);
    }


    public DbaasInstance(AsyncResponse instanceResponse, CreateInstanceRequest createInstanceRequest) throws Exception {
        this.instanceid = instanceResponse.getId();
        this.instanceInCreatingResponse = instanceResponse;
        this.createInstanceRequest = createInstanceRequest;
    }

    public DbaasInstance(ResponseEntity<AsyncResponse> instanceResponse, CreateInstanceRequest createInstanceRequest) throws Exception {
        this.instanceInCreatingEntityResponse = instanceResponse;
        this.instanceid = instanceResponse.getBody().getId();
        this.createInstanceRequest = createInstanceRequest;
    }

    public GetSnapshotResponse createSnapshotWithRetry() throws Exception {
        AsyncResponse createResponse = dbaasApi.createSnapshot(dbaasApi.buildSnapshotRequest(this.instanceid));
        waitSnapshotAvailable(createResponse.getId());
        return this.snapshotResponse;
    }

    public void deleteSnapshotWithRetry(String snapshotId) throws Exception {
        dbaasApi.deleteSnapshotWithRetry(snapshotId);
    }

    public void deleteSnapshot(String snapshotId) {
        dbaasApi.deleteSnapshot(snapshotId);
    }

    public void waitAllSnapshotCompact() throws Exception {
        dbaasApi.waitAllSnapshotCompact(this.instanceid);
    }

    public AsyncResponse createSnapshot() throws Exception {
        AsyncResponse createResponse = dbaasApi.createSnapshot(dbaasApi.buildSnapshotRequest(this.instanceid));
        this.snapshotid = createResponse.getId();
        return createResponse;
    }

    public AsyncResponse createSnapshotFromNonExistInstance() {
        AsyncResponse createResponse = dbaasApi.createSnapshot(dbaasApi.buildSnapshotRequest(Constants.INVALID_STRING));
        this.snapshotid = createResponse.getId();
        return createResponse;
    }


    public ResponseEntity<AsyncResponse> createSnapshotEntity() throws Exception {
        this.snapshotResponseEntity = dbaasApi.createSnapshotEntity(dbaasApi.buildSnapshotRequest(this.instanceid));
        this.snapshotid = this.snapshotResponseEntity.getBody().getId();
        return this.snapshotResponseEntity;
    }

    public DbaasInstance(GetInstanceResponse instanceResponse, String password, boolean prepardData) throws Exception {
        this(instanceResponse, null, password, prepardData);
    }


    public DbaasInstance(GetInstanceResponse instanceResponse, CreateInstanceRequest createInstanceRequest, String password, boolean prepardData) throws Exception {
        this.instanceResponse = instanceResponse;
        this.createInstanceRequest = createInstanceRequest;
        this.instanceid = this.instanceResponse.getId();
        mssqlConnection = dbaasApi.getMssqlConnection(instanceResponse, password);
        dbaasApi.waitDbConnected(mssqlConnection);
        if (prepardData) {
            mssqlConnection.createDatabase(SQLStatements.DB_TESTDB);
            createTable(SQLStatements.DB_TESTDB, SQLStatements.TBL_EMPLOYEE);
        }
    }
//
//    public static DbaasInstance createInstanceWithRetry(String namePrefix) throws Exception {
//        return createInstanceWithRetry(namePrefix);
//    }
//
//    public static DbaasInstance createInstance(String namePrefix) throws Exception {
//        return createInstance(namePrefix);
//    }
//
//    public static DbaasInstance createInstanceWithInvalidPlan(String namePrefix) throws Exception {
//        createInstanceRequest = dbaasApi.buildInstanceRequest(namePrefix, MASTER_PASSWORD, false, false, null, null, null);
//        createInstanceRequest.setPlan(null);
//        AsyncResponse getInstanceResponse = dbaasApi.sendCreateInstanceRequest(createInstanceRequest);
//        return new DbaasInstance(getInstanceResponse);
//    }
//
//    public static DbaasInstance createInstance(String namePrefix) throws Exception {
//        createInstanceRequest = dbaasApi.buildInstanceRequest(namePrefix, MASTER_PASSWORD, autoSnapshot, backup, snapshotSettings, pitrSettings, debugProperties);
//        AsyncResponse getInstanceResponse = dbaasApi.sendCreateInstanceRequest(createInstanceRequest);
//        return new DbaasInstance(getInstanceResponse);
//    }
//
//    public static DbaasInstance createInstanceWithRetry(String namePrefix) throws Exception {
//        createInstanceRequest = dbaasApi.buildInstanceRequest(namePrefix, MASTER_PASSWORD, autoSnapshot, backup, snapshotSettings, pitrSettings, debugProperties);
//        GetInstanceResponse getInstanceResponse = dbaasApi.createInstanceWithRetry(createInstanceRequest);
//        return new DbaasInstance(getInstanceResponse);
//    }
//
//    public static DbaasInstance createInstanceEntity(String namePrefix)throws Exception{
//    createInstanceRequest = dbaasApi.buildInstanceRequest(namePrefix, MASTER_PASSWORD, autoSnapshot, backup, snapshotSettings, pitrSettings, debugProperties);
//        ResponseEntity<AsyncResponse> response=dbaasApi.createDBInstanceEntity(createInstanceRequest);
//        return new DbaasInstance(response);
//    }

    public void waitAutoSnapshotReachLimit() throws Exception {
        dbaasApi.waitAutoSnapshotReachLimit(this.instanceid, this.getCreateInstanceRequest().getSnapshotSettings().getLimit());
    }

    public void waitInstanceDeleted() throws Exception {
        dbaasApi.waitInstanceDeleted(this.instanceid);
    }

    public DbaasInstance deleteWithRetry() throws Exception {
        dbaasApi.deleteInstanceWithRetry(this.instanceid);
        return this;
    }

    public DbaasInstance delete() throws Exception {
        dbaasApi.deleteInstance(this.instanceid);
        return this;
    }

//    public static DbaasInstance restoreFromSnapshot(String namePrefix, String snapshotid) throws Exception {
//        return restoreFromSnapshot(namePrefix, snapshotid, MASTER_PASSWORD);
//    }
//
//    public static DbaasInstance restoreFromSnapshot(String namePrefix, String snapshotid, String password) throws Exception {
//        RestoreFromSnapshotRequest createInstanceRequest = dbaasApi.buildInstanceRequestFromSnapshot(namePrefix, snapshotid, password);
//        GetInstanceResponse getInstanceResponse = dbaasApi.restoreFromSnapshotWithRetry(createInstanceRequest);
//        return new DbaasInstance(getInstanceResponse,password);
//    }
//
//    protected static DbaasInstance restoreFromBackup(String namePrefix, GetInstanceResponse getInstanceResponse, String restoreTime, String password) throws Exception {
//        CreatePitrRequest requestInstanceFromBackup = dbaasApi.buildPitrInstanceRequest(namePrefix, password, restoreTime);
//        GetInstanceResponse activeInstanceFromBackup = dbaasApi.createPitrInstanceWithRetry(requestInstanceFromBackup, getInstanceResponse.getId());
//        return new DbaasInstance(activeInstanceFromBackup,password);
//    }
//
//    protected GetInstanceResponse createInstanceFromBackup(GetInstanceResponse getInstanceResponse, CreatePitrRequest createPitrRequest) throws Exception {
//        GetInstanceResponse activeInstanceFromBackup = dbaasApi.createPitrInstanceWithRetry(createPitrRequest, getInstanceResponse.getId());
//        return activeInstanceFromBackup;
//    }


    public void insertData(String dbName, Employee employee) throws Exception {
        mssqlConnection.getTemplate().execute("INSERT INTO " + dbName + ".dbo.employee VALUES (" + employee.getId() + ",'" + employee.getName() + "','" + employee.getRole() + "')");
        logger.info("Insert Data to DB {}: {}", dbName, employee);
    }

    public void loadDataBySize(String database, String table, int totalSizeInKB) throws Exception {
        if (totalSizeInKB <= 0) {
            return;
        }
        if (totalSizeInKB <= 10240) {
            directLoadDataBySize(database, table, totalSizeInKB);
            return;
        }

        String tmpTable = "temp_data";
        mssqlConnection.createDatabase(database);
        DataPathTestModel.createTable(mssqlConnection, database, tmpTable);
        DataPathTestModel.createTable(mssqlConnection, database, table);

        logger.info("Write 10M data in tmp table.");
        String text = DataGenerator.getData(1024 * 1024 * 10);
        DataPathTestModel model = new DataPathTestModel(text);
        model.save(mssqlConnection, database, tmpTable);
        logger.info("Write completed in tmp table.");
        String sql = String.format("INSERT INTO %s.dbo.%s (data) SELECT data FROM %s.dbo.%s",
                database, table, database, tmpTable);
        int count = (totalSizeInKB - 1) / 10240 + 1;
        logger.info("Start insert data.");
        for (int i = 0; i < count; i++) {
            mssqlConnection.getTemplate().execute(sql);
            if (i % 10 == 9) {
                logger.info("Inserted 100M to table.");
            }
        }
        logger.info("Completed insert data.");
    }

    private void directLoadDataBySize(String database, String table, int size) throws Exception {
        logger.info("Start write {} kb to {}.dbo.{}", size, database, table);
        mssqlConnection.createDatabase(database);
        DataPathTestModel.createTable(mssqlConnection, database, table);
        String text = DataGenerator.getData(size * 1024);
        DataPathTestModel model = new DataPathTestModel(text);
        model.save(mssqlConnection, database, table);
        logger.info("Write completed.");
    }

    public List<Employee> query(String sql) {
        logger.info("sql is " + sql);
        return mssqlConnection.getTemplate().query(sql, new RowMapper<Employee>() {
            @Override
            public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Employee(rs);
            }
        });
    }

    public GetInstanceResponse updateSnapshotSettingsWithRetry(SnapshotSettings snapshotSettings) throws Exception {
        UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
        updateInstanceRequest.setSnapshotSettings(snapshotSettings);
        return dbaasApi.updateInstanceWithRetry(instanceid, updateInstanceRequest);
    }

    public boolean isDataExists(String dbName, Employee employee) {
        List<Employee> employeeList = null;
        try {
            employeeList = query("select * from " + dbName + ".dbo.employee where id=" + employee.getId() + " and name='" + employee.getName() + "' and role='" + employee.getRole() + "'");
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return employeeList.size() == 0 ? false : true;
    }


    public Date getCurrentTime() {
        String currentTime = mssqlConnection.getTemplate().queryForObject("SELECT GETUTCDATE()", String.class);
        logger.info("current time is: " + currentTime);
        return getCurrentDate(currentTime);
    }

    public String getOriginalCurrentTime() {
        String currentTime = mssqlConnection.getTemplate().queryForObject("SELECT GETUTCDATE()", String.class);
        logger.info("current time is: " + currentTime);
        return currentTime;
    }

    private Date getCurrentDate(String time) {
        if (StringUtils.isNotEmpty(time)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            try {
                return sdf.parse(time); // You get a Java Util Date object(Fri Mar 08 15:40:33 IST 2013)
            } catch (ParseException pe) {
            }
        }
        return null;
    }

    public boolean isDatabaseExists(String dbName) {
        boolean result = false;
        List<Map<String, Object>> rows = mssqlConnection.getTemplate().queryForList("SELECT [name] FROM sys.databases;");
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                if (row.containsValue(dbName)) {
                    result = true;
                    logger.info("Database " + dbName + " already exists.");
                }
            }
        }
        return result;
    }

    public void dropDatabase(String dbName) {
        mssqlConnection.dropDatabase(dbName);
    }

    public void createDatabase(String dbName) {
        mssqlConnection.createDatabase(dbName);
    }

    public List<Event> getEvents() throws Exception {
        return dbaasApi.waitBillingEventByInstanceIdReachCount(this.instanceid, 1);
    }

    public List<Event> getEvents(int numberOfEvents) throws Exception {
        return dbaasApi.waitBillingEventByInstanceIdReachCount(this.instanceid, numberOfEvents);
    }

    public void getSnapshot(String snapshotId) {
        dbaasApi.getSnapshot(snapshotId);
    }

    public GetSnapshotResponse waitSnapshotAvailable(String snapshotId) throws Exception {
        GetSnapshotResponse response = dbaasApi.waitAndGetAvailableSnapshot(snapshotId);
        this.snapshotResponse = response;
        return response;
    }


    public int getSnapshots() {
        return TestHelper.getSnapshotRepository().getCount(this.instanceid);
    }

    public int getCompactSnapshots() {
        return TestHelper.getSnapshotRepository().getCompactCount(this.instanceid);
    }

    public void deleteEvents() {
        TestHelper.getEventRepository().deleteByInstanceId(this.instanceid);
    }

    public List<BackupResource> getBackups() {
        return TestHelper.getInstanceRepository().findByGuid(this.instanceid).getBackups();
    }

    public List<SnapshotResource> getSnapshotIds() {
        return TestHelper.getSnapshotRepository().listSnapshots(this.instanceid);
    }

    protected boolean testDbConnectionWithRetry(MsSqlDaoFactory daoFactory) throws Exception {
        return new RetryTask(32, Lists.newArrayList(1, 2, 4, 8, 16)).execute(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                boolean result = mssqlConnection.isConnected();
                if (!result) {
                    throw new RetryException("Retry db connection ...");
                }
                return new Boolean(result);
            }
        });
    }

    public List<Data> listSnapshots() {
        List<Data> snapshotResponseListByInstanceId = dbaasApi.listSnapshot().getData()
                .stream()
                .sorted((e1, e2) -> e1.getCreatedAt()
                        .compareTo(e2.getCreatedAt()))
                .filter(x -> x.getSourceInstance().getId().equals(this.instanceid))
                .collect(Collectors.toList());
        return snapshotResponseListByInstanceId;
    }

    public List<Data> listAvailableSnapshots() {
        return listSnapshots().stream().filter(x -> x.getStatus().equals(StatusCode.AVAILABLE.value())).collect(Collectors.toList());
    }

    public List<Data> listAvailableAndAutoSnapshots() {
        return listAvailableSnapshots().stream().filter(x -> x.getType().equals(Constant.AUTO.value())).collect(Collectors.toList());
    }

    public GetInstanceResponse updateInstanceWithRetry(UpdateInstanceRequest updateInstanceRequest) throws Exception {
        dbaasApi.sendUpdateRequest(this.instanceid, updateInstanceRequest);
        if (updateInstanceRequest.getMasterPassword() != null) {
            password = updateInstanceRequest.getMasterPassword();
        }
        return waitAndGetAvailableInstance();
    }

    public GetInstanceResponse updatePreferredStartTime() throws Exception {
        UpdateInstanceRequest updateInstanceRequest = new UpdateInstanceRequest();
        SnapshotSettings snapshotSettings = this.instanceResponse.getSnapshotSettings();
        if (dbaasApi.DEFAULT_PREFERRED_START_TIME.equalsIgnoreCase(snapshotSettings.getPreferredStartTime())) {
            snapshotSettings.setPreferredStartTime(0, 2);
            updateInstanceRequest.setSnapshotSettings(snapshotSettings);
            return updateInstanceWithRetry(updateInstanceRequest);
        } else {
            //if some test case do not need update preferred start time, so just retrun getinstance response
            return getInstanceResponse();
        }
    }

    public AsyncResponse updateInstance(UpdateInstanceRequest updateInstanceRequest) throws Exception {
        return dbaasApi.sendUpdateRequest(this.instanceid, updateInstanceRequest);
    }

    public DbaasApi getDbaasApi() {
        return dbaasApi;
    }

    public DataPath updateConnection(UpdateConnectionRequest updateConnectionRequest) {
        return dbaasApi.updateConnection(this.instanceid, updateConnectionRequest);
    }

    public Connections getDataPath() {
        return dbaasApi.getDataPath(this.instanceid);
    }

    public GetSnapshotResponse waitAutoSnapshot() throws Exception {
        List<Data> snapshots = dbaasApi.getInstanceSnapshots(this.instanceid);
        List<String> snapshotIds = new ArrayList<>();
        for (Data snapshot : snapshots) {
            snapshotIds.add(snapshot.getId());
        }
        long endTime = 180 * 1000 + System.currentTimeMillis();
        while (true) {
            snapshots = dbaasApi.getInstanceSnapshots(this.instanceid);
            for (Data snapshot : snapshots) {
                if (!snapshotIds.contains(snapshot.getId())) {
                    this.snapshotResponse = dbaasApi.waitAndGetAvailableSnapshot(snapshot.getId());
                    return this.snapshotResponse;
                }
            }
            if (System.currentTimeMillis() > endTime) {
                throw new Exception("Could not get auto snapshot for " + this.instanceid + ".");
            }
            TimeUnit.SECONDS.sleep(15);
        }
    }
}