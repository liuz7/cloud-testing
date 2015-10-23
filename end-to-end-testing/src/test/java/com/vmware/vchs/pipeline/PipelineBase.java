package com.vmware.vchs.pipeline;

import com.vmware.vchs.base.DbaasApi;
import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.common.utils.Utils;
import com.vmware.vchs.datapath.MssqlConnection;
import com.vmware.vchs.launcher.TestHelper;
import com.vmware.vchs.model.portal.common.AsyncResponse;
import com.vmware.vchs.model.portal.common.ListResponse;
import com.vmware.vchs.model.portal.instance.Connections;
import com.vmware.vchs.model.portal.instance.CreateInstanceRequest;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.model.portal.instance.ListInstanceItem;
import com.vmware.vchs.model.portal.snapshot.CreateSnapshotRequest;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import com.vmware.vchs.test.config.Configuration;
import com.vmware.vchs.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.assertj.core.api.Assertions.assertThat;



/**
 * Created by fanz on 8/19/15.
 */
public class PipelineBase {

    protected static final Logger logger = LoggerFactory.getLogger(PipelineBase.class);

    public static CreateInstanceRequest createInstanceRequestPipeline;
    public static GetInstanceResponse getInstanceResponsePipeline;
    public static GetInstanceResponse getNewInstanceResponsePipeline;   // Get new instance response

    public static CreateSnapshotRequest createSnapshotRequestPipeline;
    public static GetSnapshotResponse getSnapshotResponsePipeline;

    public static ListResponse listResponse;

    /**
     *  Database : username, password, DBname, tableName,
     */

    private String password = MASTER_PASSWORD;
    public static final String MASTER_PASSWORD = "ca$hc0w";

    public static String databaseName = "testDatabase";
    public static String tableName = "testTable";

    public String DBuserName;
    public String DBuserPassword = MASTER_PASSWORD;

    /**
     *  DBaas instance : instanceId, instanceIPAddress, instancePort,
     *                   snapshotId,
     *                   newInstanceId, newInstanceIPAddress, newInstancePort
     */
    public static DbaasInstance dbaasInstance;
    public static String instanceId;
    public static String instanceIPAddress;
    public static String instancePort = "1433";

    public static String snapshotId;
    public static DbaasInstance restoredInstance;
    public static String newInstanceId;
    public static String newInstanceIPAddress;
    public static String newInstancePort = "1433";

    /**
     *  verify instanceId, IpAddress, port. Used for Pipeline DataVarification
     */
    public static String verifyInstanceId;
    public static String verifyInstanceIPAddress;
    public static String verifyInstanceCreatorEmail;
    public static String verifyInstancePort = "1433";
    public static String verifyUsername;

    public static String sourceInstanceId;
    public static String sourceInstanceIPAddress;
    public static String sourceInstanceMasterUser;

    public static String instanceCreatorName;


    public static SQLServerClient.TableData verifyTableData;
    public static SQLServerClient.TableData insertedTableData;

    /**
     *  DBaasApi
     */

    protected static final Configuration configuration = TestHelper.getConfiguration();
    public static DbaasApi dbaasApi = new DbaasApi(configuration);   // new api
    public static MssqlConnection mssqlConnection;

    protected DbaasInstance.DbaasInstanceBuilder builder = new DbaasInstance.DbaasInstanceBuilder(dbaasApi);;

    public static final String version = configuration.getDbEngineVersion();

    public static Map<String, String> dbEngineVersionMap = new HashMap<>();

    protected String methodName;
    protected String getNamePrefix(){
        return CommonUtils.generateNamePrefix(this.getClass().getName(), methodName);
    }



    @BeforeClass(alwaysRun = true)
    public void setUpClass() throws Exception {
        try {
            //super.setUpClass();
            logger.info("Setup for Pipeline Base class...");
        } catch (Exception e) {
            logger.info(Utils.getStackTrace(e));
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod(Method method) throws Exception {
        try {
            logger.info("Setup for Data Path test method...");
            logger.info("iam configuration is " + this.configuration.isAuthentication());
            logger.info("class name is " + this.getClass());
            logger.info("method name is " + method.getName());
            methodName = method.getName();
            dbaasApi = new DbaasApi(TestHelper.getConfiguration());
            builder = new DbaasInstance.DbaasInstanceBuilder(dbaasApi);
        } catch (Exception e) {
            logger.info(Utils.getStackTrace(e));
        }
    }

    public void provisionInstance(String namePrefix) {
        try {
            logger.info("provisionInstance - Start to provision instance...");
            DbaasInstance instance = builder.createInstanceWithRetry(namePrefix);  // dbaasApi (master username, password fixed!)
            instanceId = instance.getInstanceid();
            logger.info("Instance ID is : " + instanceId);
            dbaasInstance = instance;

            getInstanceResponsePipeline = instance.getInstanceResponse();
            //logger.info("InstanceResponse is : " + getNewInstanceResponsePipeline.toString());
            instanceCreatorName = getInstanceResponsePipeline.getCreatorName();
            instanceIPAddress = checkNotNull(getInstanceResponsePipeline.getIpAddress());

            Connections connections = getInstanceResponsePipeline.getConnections();
            checkNotNull(connections.getDataPath());
            checkNotNull(connections.getDataPath().getDestPort());
            String port = String.valueOf(connections.getDataPath().getDestPort());
            if (instancePort != port) {
                instancePort = port;
            }

            DBuserName = getInstanceResponsePipeline.getMasterUsername();
            DBuserPassword = MASTER_PASSWORD;

            //mssqlConnection = dbaasApi.getMssqlConnection(getInstanceResponsePipeline, DBuserPassword);

            logger.info(" Provision instance ID : " + instanceId);
            logger.info(" Provision instance IP Address : " + instanceIPAddress);
            logger.info(" Provision instance port : " + instancePort );
            logger.info("Instance DB username : " + DBuserName + ", password is : " + DBuserPassword);
            logger.info("Instance is created by creator :" + instanceCreatorName);

        } catch ( Exception e) {
            e.printStackTrace();
        }

    }



    public void provisionSnapshot(String instanceId) {
        try{
            logger.info("provisionSnapshot - start to provision Snapshot");
            if (instanceId != null) {
                dbaasApi.getIamAuthInfo();
                createSnapshotRequestPipeline = dbaasApi.buildSnapshotRequest(instanceId);
                AsyncResponse createResponse = dbaasApi.createSnapshot(createSnapshotRequestPipeline);
                snapshotId = createResponse.getId();
                GetSnapshotResponse response = dbaasApi.waitAndGetAvailableSnapshot(snapshotId);
                getSnapshotResponsePipeline = response;
                logger.info("Snapshot ID is : " + snapshotId);
                checkNotNull(getSnapshotResponsePipeline, "Can not create snapshot for instance : " + instanceId);
                checkNotNull(snapshotId, "Can not get snapshot ID, pleaes check if snapshot is created correctly !");
                checkNotNull(getSnapshotResponsePipeline.getSourceInstance(), "Can not get source instance of snapshot " + snapshotId );
                sourceInstanceId = getSnapshotResponsePipeline.getSourceInstance().getId();
                sourceInstanceIPAddress = getSnapshotResponsePipeline.getSourceInstance().getIpAddress();
                sourceInstanceMasterUser = getSnapshotResponsePipeline.getSourceInstance().getMasterUsername();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void restoreFromSnapshot(String namePrefix, DbaasInstance instance) throws Exception {
        try {
            GetSnapshotResponse snapshotResponse = instance.createSnapshotWithRetry();
            restoredInstance = builder.restoreFromSnapshot(namePrefix, snapshotResponse.getId());
            getNewInstanceResponsePipeline = restoredInstance.getInstanceResponse();
            checkNotNull(getNewInstanceResponsePipeline, "Can not create instance for sapshot: " + snapshotId);
            newInstanceId = checkNotNull(getNewInstanceResponsePipeline.getId());
            newInstanceIPAddress = checkNotNull(getNewInstanceResponsePipeline.getIpAddress());
            Connections connections = getNewInstanceResponsePipeline.getConnections();
            checkNotNull(connections.getDataPath());
            checkNotNull(connections.getDataPath().getDestPort());
            String port = String.valueOf(connections.getDataPath().getDestPort());
            if (newInstancePort != port) {
                newInstancePort = port;
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    public void listAllInstance() {
        listResponse = dbaasApi.listDBInstances();
        int size = listResponse.getTotal();
        logger.info(" " + size + " numbers of instances found");
        assertThat(listResponse).isNotNull();
    }

    public void getProvisionedInstance(String namePrefix) throws Exception{
        logger.info("Traverse to list instance with namePrefix : " + namePrefix);
        //List<String> instanceIds = new ArrayList<>();
        ListResponse listResponse = dbaasApi.listDBInstances();
        List<ListInstanceItem> listInstanceItems = checkNotNull((List<ListInstanceItem>) listResponse.getData());
        if (StringUtils.isEmpty(namePrefix)) {
            //instanceIds.addAll(listInstanceItems.stream().map(src -> src.getId()).collect(Collectors.toList()));
            throw new Exception("namePrefix is null..., Please specify which instance");
        } else {
            for (ListInstanceItem listInstanceItem : listInstanceItems) {
                if (listInstanceItem.getName().startsWith(namePrefix)) {
                    verifyInstanceId = listInstanceItem.getId();
                    verifyInstanceIPAddress = listInstanceItem.getIpAddress();
                    verifyInstanceCreatorEmail = listInstanceItem.getCreatorEmail();
                    //instanceIds.add(listInstanceItem.getId());
                }
            }
        }
    }

    public String getInstanceUser(String instanceId) throws Exception {
        GetInstanceResponse response = dbaasApi.getDBInstance(instanceId);
        if (response != null) {
            verifyUsername = response.getMasterUsername();
        } else {
            throw new Exception("Can not get Instance response by instanceId: " + instanceId);
        }
        return verifyUsername;
    }

    public static void verifyInstanceAdded() {
        listResponse = dbaasApi.listDBInstances();
        int size = listResponse.getTotal();
        assertThat(listResponse).isNotNull();
        List<ListInstanceItem> responseInstanceList = (List<ListInstanceItem>) listResponse.getData();
        assertThat(responseInstanceList).hasSize(size + 1);

    }

    public static void storeInstanceIP(String instanceId) throws Exception {
        try {
            logger.info(" Start to store instanceIP into file, filename is : instanceIP.txt ");
            BufferedWriter bw = Files.newBufferedWriter(Paths.get("instanceIP.txt"), StandardOpenOption.WRITE);
            logger.info("instance IP is :" + instanceId);
            bw.write(instanceId);
            bw.newLine();
            bw.flush();
            bw.close();
            logger.info("Instance IP has been written into file, Completed! ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> retrieveInstanceIP() throws Exception {
        try {
            BufferedReader br = Files.newBufferedReader(Paths.get("instanceIP.txt"));
            List<String> list = new ArrayList<>();
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                list.add(line.trim());
            }
            br.close();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> getDbEngineVersionMap() {
        return dbEngineVersionMap;
    }

    public static void setDbEngineVersionMap(Map<String, String> dbEngineVersionMap) {
        PipelineBase.dbEngineVersionMap = dbEngineVersionMap;
    }


    public boolean verifyTable(SQLServerClient connection ) throws Exception {
        if (connection.isConnected()) {
            assertThat(connection.isDatabaseExists(databaseName));
            assertThat(connection.isTableExists(databaseName, tableName));
            logger.info("Verified Table exists correctly ...");
            if (connection.isDatabaseExists(databaseName) && connection.isTableExists(databaseName, tableName))
                return true;
        }
        logger.info("(Verify Instance) Can not connect to SQL server, IP address is : " +  verifyInstanceIPAddress);
        return false;
    }

    public boolean verifyTableData(SQLServerClient connection, String database, String tableName) throws Exception {
        logger.info("Start tot verify Data in table: " + tableName);
        insertedTableData = SQLServerClient.TableData.builder().setId(1).setName("Frank").setAge(38).setAddress("Oracle").build();
        logger.info("Provisioned inserted data.");
        if (connection.isConnected()) {
            logger.info("Start to retrieve data from table");
            verifyTableData = connection.findData(database,tableName);
        }
        logger.info("Verify equality of provision data & inserted data");
        assertThat(verifyTableData).isNotNull();
        assertThat(insertedTableData).isNotNull();
        return verifyTableData.equals(insertedTableData);
    }


}
