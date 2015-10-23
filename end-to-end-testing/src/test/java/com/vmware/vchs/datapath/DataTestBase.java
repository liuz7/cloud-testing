package com.vmware.vchs.datapath;

import com.vmware.vchs.base.BaseTest;
import com.vmware.vchs.base.DbaasApi;
import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.base.E2ETest;
import com.vmware.vchs.common.utils.Utils;
import com.vmware.vchs.constant.StatusCode;
import com.vmware.vchs.launcher.TestHelper;
import com.vmware.vchs.model.portal.common.AsyncResponse;
import com.vmware.vchs.model.portal.common.ListResponse;
import com.vmware.vchs.model.portal.instance.Connections;
import com.vmware.vchs.model.portal.instance.CreateInstanceRequest;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.model.portal.instance.ListInstanceItem;
import com.vmware.vchs.model.portal.snapshot.CreateSnapshotRequest;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import com.vmware.vchs.pipeline.SQLServerClient;
import com.vmware.vchs.utils.CommonUtils;
import org.springframework.dao.DataAccessException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by fanz on 9/17/15.
 */
public class DataTestBase extends BaseTest implements E2ETest  {


    public static GetInstanceResponse getInstanceResponsePipeline;
    public static GetInstanceResponse getNewInstanceResponsePipeline;

    public static CreateSnapshotRequest createSnapshotRequestPipeline;
    public static GetSnapshotResponse getSnapshotResponsePipeline;

    public static ListResponse listResponse;

    /**
     *  Database : username, password, DBname, tableName,
     */
    public static String DBuserName;
    public static String DBuserPassword;

    private String password = MASTER_PASSWORD;
    public static final String MASTER_PASSWORD = "ca$hc0w";

    public static String databaseName = "testDatabase";
    public static String tableName = "testTable";

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

    public static String sourceInstanceId;
    public static String sourceInstanceIPAddress;
    public static String sourceInstanceMasterUser;

    public static String instanceCreatorName;


    protected static final com.vmware.vchs.test.config.Configuration configuration = TestHelper.getConfiguration();
    public static DbaasApi dbaasApi;

    protected static DbaasInstance.DbaasInstanceBuilder builder;

    public static final String version = configuration.getDbEngineVersion();

    public static Map<String, String> dbEngineVersionMap = new HashMap<>();

    /**
     *  Properties for DataTest
     */

    public static CreateInstanceRequest createInstanceRequestDataPath;
    public static GetInstanceResponse getInstanceResponseDataPath;
    public static GetInstanceResponse getNewInstanceResponseDataPath;

    public static CreateSnapshotRequest createSnapshotRequestDataPath;
    public static GetSnapshotResponse getSnapshotResponseDataPath;

    public static ListResponse listResponseDataPath;

    // Verify instance  + snapshot
    public static String verifyInstanceId;
    public static String verifyInstanceIPAddress;
    public static int verifyInstancePort;
    public static String newPassword;

    public static SQLServerClient.TableData verifyTableData;
    public static SQLServerClient.TableData insertedTableData;
    public static SQLServerClient.TableData insertTableData;
    public static SQLServerClient.TableData testData;

    public static String LoginUser = "testUser" ;
    public static String LoginUserPassword = "Password#1";

    public static SQLServerClient sqlServerClient ;
    public static SQLServerClient newLoginUserConn;
    public static SQLServerClient loginUserConnection;

    public static List<String> SQLEngineVerionList = new ArrayList<>();
    public static final String REMOTE_IP = "107.189.88.123"; // 192.168.253.4


    @BeforeClass(alwaysRun = true)
    public void setUpClass() throws Exception {
        try {
            super.setUpClass();
            logger.info("Start Data Path Base test class...");
        } catch (Exception e) {
            logger.info(Utils.getStackTrace(e));
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod(Method method) throws Exception {
        try {
            super.setUpMethod(method);
            logger.info("Setup for Data Path test method...");
            dbaasApi = new DbaasApi(TestHelper.getConfiguration());
            builder = new DbaasInstance.DbaasInstanceBuilder(dbaasApi);
        } catch (Exception e) {
            logger.info(Utils.getStackTrace(e));
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownMethod(Method method) throws Exception {
        try {
            logger.info("Tear down for data path test method...");
            dbaasApi.cleanInstance(CommonUtils.generateNamePrefix(this.getClass().getName(), method.getName()));
            dbaasApi.cleanSnapshots(CommonUtils.generateNamePrefix(this.getClass().getName(), method.getName()));
        } catch (Exception e) {
            logger.info(Utils.getStackTrace(e));
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() throws Exception {
        try {
            super.tearDownClass();
            logger.info("Tear down data path test class...");
/*            dbaasApi.cleanInstance(this.getClass().getName());
            dbaasApi.cleanSnapshots(this.getClass().getName());*/
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.toString());
        }
    }



    public void provisionInstance(String namePrefix) {
        try {
            logger.info("provisionInstance - Start to provision instance...");
            DbaasInstance instance = builder.createInstanceWithRetry(namePrefix);
            instanceId = instance.getInstanceid();
            logger.info("Instance ID is : " + instanceId);
            dbaasInstance = instance;

            getInstanceResponsePipeline = instance.getInstanceResponse();
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
            logger.info("#### DBuserName of instance : " + DBuserName);
            logger.info("#### DBuserPassword of instance : " +DBuserPassword);

            logger.info(" Provision instance ID : " + instanceId);
            logger.info(" Provision instance IP Address : " + instanceIPAddress);
            logger.info(" Provision instance port : " + instancePort );
            logger.info("Instance DB username : " + DBuserName + ", password is : " + DBuserPassword);
            logger.info("Instance is created by creator :" + instanceCreatorName);

        } catch ( Exception e) {
            logger.info(" ERROR : can not provisionInstance ! ");
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
            } else {
                throw new Exception("provisionSnapshot - Can not find instanceId. Can not provision snapshot!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void restoreFromSnapshot(String namePrefix, DbaasInstance instance) throws Exception {
        try {
            logger.info("restoreFromSnapshot - restore instance from Snapshot");
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
            logger.info(" ERROR : can not restoreFromSnapshot");
            e.printStackTrace();
        }
    }

    public void listAllInstance() {
        listResponse = dbaasApi.listDBInstances();
        int size = listResponse.getTotal();
        logger.info(" " + size + " numbers of instances found");
        assertThat(listResponse).isNotNull();
    }


    public static boolean verifyTable(SQLServerClient connection) {
        try {
            if (connection.isConnected()) {
                assertThat(connection.isDatabaseExists(databaseName));
                assertThat(connection.isTableExists(databaseName, tableName));
                if (connection.isTableExists(databaseName, tableName)) {
                    logger.info("Verified Table exists correctly ...");
                    return true;
                }
            } else {
                logger.info("(Verify Instance) Can not connect to SQL server");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean verifyTableData(SQLServerClient connection, String database, String tableName) {
        try {
            insertedTableData = connection.setInitalTableData();
            verifyTableData = connection.findData(database,tableName);
            assertThat(verifyTableData).isNotNull();
            return insertedTableData.equals(verifyTableData);
        } catch (Exception e) {
            logger.info("Can not verify Table Data !!!!  ");
            e.printStackTrace();
        }
        return false;
    }


    public boolean verifyDataCRUD(SQLServerClient connection, String database, String table) throws Exception {
        try {
            if (connection.isConnected()) {
                if (!connection.isDatabaseExists(database)) {
                    connection.createDatabase(database);
                }
                if (!connection.isTableExists(database,table)) {
                    logger.info(" Starting to test CREATE Permission.... ");
                    connection.createTable(connection, database, table);
                    logger.info(" set inital TableData instance...");
                    insertTableData = connection.setInitalTableData();
                    logger.info(" Insert this inital TableData instance into (table)" + database + ".dbo." + table);
                    connection.insertData(insertTableData, database, table);
                    logger.info(" Starting to test READ Permission.... ");
                    assertThat(connection.isDatabaseExists(database));
                    assertThat(connection.isTableExists(database, table));
                    logger.info(" Starting to test UPDATE Permission.... ");
                    logger.info("Get Pre-count numeber of table : " + database + ".dbo." + table);
                    int preCount = connection.getCount(database, table);
                    logger.info("Pre-count numeber is : " + preCount);
                    logger.info("Generate new data...");
                    testData = connection.generateSimpleData();
                    logger.info("Insert new data to table");
                    connection.updateTable(database, table);
                    int postCount = connection.getCount(database, table);
                    logger.info("POST-count numeber is : " + postCount);
                    assertThat(postCount > preCount);
                    connection.updateTable(database, table);
                    logger.info(" Starting to test DELETE Permission.... ");
                    connection.dropTable(database, table);
                    assertThat(!connection.isTableExists(database,table));
                    connection.dropDatabase(database);
                    assertThat(!connection.isDatabaseExists(database));
                    return true;
                } else {
                    logger.info("Table %s is existed already! ", table);
                }
            } else {
                throw new Exception("Can not connect to SQL server, IP address is : " );
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean verifyRUDInstance(DbaasInstance instance) {
        try {
            logger.info("verifyRUDInstance, start to delete instance....");
            instance.delete();
            logger.info("deleting instance....");
            List<ListInstanceItem> listInstanceItems = (List<ListInstanceItem>) dbaasApi.listDBInstances().getData();
            for (ListInstanceItem listInstanceItem : listInstanceItems) {
                if(listInstanceItem.getId().equalsIgnoreCase(instance.getInstanceid())){
                    assertThat(listInstanceItem.getStatus()).isEqualToIgnoringCase(StatusCode.DELETING.value());
                    logger.info("Find the status is deleting...");
                    break;
                }
            }
            instance.waitInstanceDeleted();
            logger.info("Waited for deletion is completed...");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean verifyChangePassword(String host, String username, String password, String port) throws Exception {
        try {
            SQLServerClient conn = new SQLServerClient(host, username, password, port);
            if (conn.isConnected()) {
                return true;
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

}
