package com.vmware.vchs.pipeline;

import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.constant.StatusCode;
import com.vmware.vchs.model.portal.common.ListResponse;
import com.vmware.vchs.model.portal.instance.CreateInstanceRequest;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.model.portal.instance.ListInstanceItem;
import com.vmware.vchs.model.portal.snapshot.CreateSnapshotRequest;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by fanz on 8/27/15.
 */
public class DataPathTest extends DataPathBase {

    protected static final Logger logger = LoggerFactory.getLogger(DataPathTest.class);

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


    /**
     *  Test cases
     */
    @Test(groups={"datatest"})
    public void testDataRetainedFromSnapshot() throws Exception {
        try {
            logger.info("testDataRetainedFromSnapshot");
            String name = getNamePrefix();
            provisionInstance(name);           // static dbaasInstance, getInstanceResponsePipeline, instanceId, instanceIPAddress, DBuserName, DBuserPassword
            logger.info(" Provision instance from snapshot Successful.");
            logger.info(" Provision instance Instance ID : " + instanceId);
            logger.info(" Provision instance IP address: " + instanceIPAddress);
            logger.info(" Provision instance port: " + instancePort);
            provisionSnapshot(checkNotNull(instanceId));   // static getSnapshotResponsePipeline, snapshotId, sourceInstanceId, sourceInstanceIPAddress, sourceInstanceMasterUser
            restoreFromSnapshot(name, dbaasInstance);  // static getNewInstanceResponsePipeline, newInstanceId, newInstanceIPAddress, newInstancePort
            logger.info(" Restored instance from snapshot Successful.");
            logger.info(" Restored Instance ID : " + newInstanceId);
            logger.info(" Restored instance IP address: " + newInstanceIPAddress);
            logger.info(" Restored instance port: " + newInstancePort);

            //newSQLServerConn = mssqlConnection;
            sqlServerClient = new SQLServerClient(newInstanceIPAddress, DBuserName, MASTER_PASSWORD, newInstancePort);

            // test CRUD on data
            assertThat(verifyDataCRUD(sqlServerClient, "newDatabase", "newTable"));

            // test RUD on new instance
            assertThat(verifyRUDInstance(restoredInstance));

        } catch ( DataAccessException e) {
            logger.info("Can not verify Data in retained test, test failed !!!!  ");
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test(groups={"datatest"})
    public void testUserPermissionCRUD() throws Exception {
        try {
            // TEST CREATE
            logger.info("testUserPermissionCRUD") ;
            String name = getNamePrefix();
            provisionInstance(name);
            logger.info(" Provision instance from snapshot Successful.");
            logger.info(" Provision instance Instance ID : " + instanceId);
            logger.info(" Provision instance IP address: " + instanceIPAddress);
            logger.info(" Provision instance port: " + instancePort);

            sqlServerClient = new SQLServerClient(instanceIPAddress, DBuserName, DBuserPassword, instancePort); // SQL Connection

            if (sqlServerClient.isConnected()) {
                if (!sqlServerClient.isDatabaseExists(databaseName)) {
                    sqlServerClient.createDatabase(databaseName);
                }
                if (!sqlServerClient.isTableExists(databaseName,tableName)) {
                    logger.info("DatabaseName arg is : " + databaseName);
                    logger.info("tableName arg is : " + tableName);
                    logger.info("Start to create table...");
                    sqlServerClient.createTable(sqlServerClient, databaseName, tableName);   // CREATE TABLE
                    logger.info("Start to initialize data...");
                    sqlServerClient.initialTableData(databaseName, tableName);
                    //insertTableData = sqlServerClient.setInitalTableData();
                    //logger.info("Start to insert data into table");
                    //sqlServerClient.insertData(insertTableData, databaseName, tableName);    // INSERT DATA
                    logger.info(" Starting to verify data Exists ");
                    assertThat(sqlServerClient.isDatabaseExists(databaseName));
                    logger.info("Start to verify table Exists");
                    assertThat(sqlServerClient.isTableExists(databaseName, tableName));
                    logger.info(" Completed CREATE Permission test. ");
                } else {
                    logger.info("Table %s is existed already! ", tableName);
                }
            } else {
                throw new Exception("Can not connect to SQL server, IP address is : " +  instanceIPAddress);
            }


/*            listAllInstance();
            List<ListInstanceItem> listInstanceItems = checkNotNull((List<ListInstanceItem>) listResponse.getData());
            for (ListInstanceItem listInstanceItem : listInstanceItems) {
                if (instanceId == listInstanceItem.getId()) {                 // Find the correct(same) IP
                    logger.info(" Starting to test READ Permission.... ");
                    verifyInstanceId = listInstanceItem.getId();
                    verifyInstanceIPAddress = listInstanceItem.getIpAddress();
                    logger.info("READ TEST: Start to verify table");
                    if (verifyTable(sqlServerClient)) {  // use same connection
                        logger.info("READ TEST : Start to verify Table Data");
                        assertThat(verifyTableData(sqlServerClient, databaseName, tableName));
                    } else {
                        throw new Exception("Can not find Table for instance : " + verifyInstanceId );
                    }
                }
            }*/

            // TEST UPDATE
            logger.info(" Starting to test UPDATE Permission.... ");
            if (instanceIPAddress == null) {
                throw new Exception("Can not connect to SQL server, IP address is : " +  instanceIPAddress);
            }
            if (!sqlServerClient.isConnected() || sqlServerClient == null) {
                sqlServerClient = new SQLServerClient(instanceIPAddress, DBuserName, DBuserPassword, instancePort);
            }
                logger.info("Get Pre-count numeber of table : " + databaseName + ".dbo." +tableName);
                int preCount = sqlServerClient.getCount(databaseName, tableName);
                logger.info("Pre-count numeber is : " + preCount);
                logger.info("Start to create table");
                logger.info("Generate new data...");
                testData = sqlServerClient.generateSimpleData();
                logger.info("Insert new data to table");
                sqlServerClient.insertData(testData, databaseName, tableName);
                int postCount = sqlServerClient.getCount(databaseName, tableName);
                logger.info("POST-count numeber is : " + postCount);
                assertThat(preCount < postCount);
                logger.info("update database+table");
                sqlServerClient.updateTable(databaseName, tableName);
                //assertThat();

            //TEST DELETE
            logger.info(" Starting to test DELETE Permission.... ");
            if (instanceIPAddress == null) {
                throw new Exception("Can not connect to SQL server, IP address is : " +  instanceIPAddress);
            }
            if (!sqlServerClient.isConnected() || sqlServerClient == null) {
                sqlServerClient = new SQLServerClient(instanceIPAddress, DBuserName, DBuserPassword, instancePort);
            }
                logger.info("start to drop table: " + databaseName + ".dbo." +tableName);
                sqlServerClient.dropTable(databaseName, tableName);
                logger.info("verify if table: " + databaseName + ".dbo." +tableName + " still exists" );
                assertThat(!sqlServerClient.isTableExists(databaseName, tableName));
                logger.info("start to drop table: " + databaseName );
                sqlServerClient.dropDatabase(databaseName);
                logger.info("verify if table: " + databaseName + " still exists" );
                assertThat(!sqlServerClient.isDatabaseExists(databaseName));

        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Test(groups={"datatest"})
    public void testDBPermission() throws Exception {
        try {
            logger.info("testDBPermission");
            String name = getNamePrefix();
            provisionInstance(name);  // static dbaasInstance, instanceId, instanceIPAddress,
            logger.info(" Provision instance from snapshot Successful.");
            logger.info("Show IP :" + instanceIPAddress);
            sqlServerClient = new SQLServerClient(instanceIPAddress, DBuserName, DBuserPassword, instancePort); // SQL Connection

            if (sqlServerClient.isConnected()) {
                if (!sqlServerClient.isDatabaseExists(databaseName)) {
                    sqlServerClient.createDatabase(databaseName);
                    logger.info(" Created database : " + databaseName);
                } else {
                    logger.info(" Database " + databaseName + " exists already! ");
                }
                if (!sqlServerClient.isTableExists(databaseName,tableName)) {
                    logger.info("Start to create table: " + tableName);
                    sqlServerClient.createTable(sqlServerClient, databaseName, tableName);   // CREATE TABLE
                } else {
                    logger.info("Table %s exists already! ", tableName);
                }
                logger.info("Creating Login User with Login SQL Server permission...");
                sqlServerClient.createLoginUser(LoginUser, LoginUserPassword, databaseName);

                logger.info("Verify Login User has login permission...");
                assertThat(sqlServerClient.isLoginUserExist(LoginUser));

                logger.info("Creating DB User with access DB permission...");
                sqlServerClient.createDBUser(LoginUser, databaseName);

                logger.info("Verify DB User's role...");
                String roleName = sqlServerClient.findDBUserRole(databaseName,LoginUser);
                assertThat(roleName).isNotNull();
                assertThat(roleName.equals("public"));

                logger.info("Verify DB User has DB Access permission...");
                assertThat(sqlServerClient.isDBUserExist(LoginUser, databaseName));

                logger.info("Verify Login User connect SQL Server");
                loginUserConnection = new SQLServerClient(instanceIPAddress, LoginUser, LoginUserPassword, instancePort);
                assertThat(loginUserConnection != null);
                assertThat(loginUserConnection.isConnected());

            } else {
                throw new Exception("Can not connect to SQL server, IP address is : " +  instanceIPAddress);
            }

        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Test(groups={"datatest"})
    public void testUpdatePasswordSSMSAndConnect() throws Exception{
        try {
            logger.info("testUpdatePasswordSSMSAndConnect");
            String name = getNamePrefix();
            provisionInstance(name);  // static dbaasInstance, instanceId, instanceIPAddress,
            sqlServerClient = new SQLServerClient(instanceIPAddress, DBuserName, DBuserPassword, instancePort);
            String newPassword = "Password#2";

            if (sqlServerClient.isConnected()) {
                if (!sqlServerClient.isDatabaseExists(databaseName)) {
                    sqlServerClient.createDatabase(databaseName);
                    logger.info(" Created Database " + databaseName + " successfully ! ");
                } else {
                    logger.info(" Database " + databaseName + " exists already! ");
                }
                if (!sqlServerClient.isTableExists(databaseName,tableName)) {
                    sqlServerClient.createTable(sqlServerClient, databaseName, tableName);   // CREATE TABLE
                } else {
                    logger.info("Table %s exists already! ", tableName);
                }
                sqlServerClient.createLoginUser(LoginUser, LoginUserPassword, databaseName);
                sqlServerClient.createDBUser(LoginUser, databaseName);
                logger.info("Start to change password for use:" + LoginUser);
                sqlServerClient.changePassword(LoginUser, newPassword);
                assertThat(verifyChangePassword(instanceIPAddress, LoginUser, newPassword, instancePort));
            } else {
                throw new Exception("Can not connect to SQL server, IP address is : " +  instanceIPAddress);
            }
            loginUserConnection = new SQLServerClient(instanceIPAddress, LoginUser, newPassword, instancePort);
            assertThat(loginUserConnection.isConnected());
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }

    @Test(groups={"datatest"})
    public  void testGrandUserRoleDB() throws Exception {
        try {
            logger.info("testGrandUserRoleDB");
            String name = getNamePrefix();
            provisionInstance(name);  // static dbaasInstance, instanceId, instanceIPAddress,
            sqlServerClient = new SQLServerClient(instanceIPAddress, DBuserName, DBuserPassword, instancePort);

            if (sqlServerClient.isConnected()) {
                if (!sqlServerClient.isDatabaseExists(databaseName)) {
                    sqlServerClient.createDatabase(databaseName);
                    logger.info(" Created Database " + databaseName + " successfully ! ");
                } else {
                    logger.info(" Database " + databaseName + " exists already! ");
                }
                if (!sqlServerClient.isTableExists(databaseName,tableName)) {
                    sqlServerClient.createTable(sqlServerClient, databaseName, tableName);   // CREATE TABLE
                } else {
                    logger.info("Table %s exists already! ", tableName);
                }
                sqlServerClient.createLoginUser(LoginUser, LoginUserPassword, databaseName);
                sqlServerClient.createDBUser(LoginUser, databaseName);
                assertThat(sqlServerClient.grantDBUserPermission(LoginUser, "db_owner", databaseName));
                assertThat(sqlServerClient.verifyDBUserPermission(LoginUser,"db_owner", databaseName));

            } else {
                throw new Exception("Can not connect to SQL server, IP address is : " +  instanceIPAddress);
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }


    @Test(groups={"datatest"})
    public  void testSQLSeverEngineCheck() throws Exception {
        try {
            dbEngineVersionMap.put("11.0.2100.60", "2012");
            dbEngineVersionMap.put("10.50.4000.0", "2008R2 SP2");
            dbEngineVersionMap.put("10.52.4000.0", "2008R2 SP2");
            // (11.2.5058.0)  10.52.4000.0  11.0.2100.60
            SQLEngineVerionList.add("11.0.2100.60");
            SQLEngineVerionList.add("10.50.4000.0");
            SQLEngineVerionList.add("10.52.4000.0");
            logger.info("testSQLSeverEngineCheck");
            String name = getNamePrefix();
            logger.info(name);
            String editVersion = null;
            provisionInstance(name);  // static dbaasInstance, instanceId, instanceIPAddress,
            sqlServerClient = new SQLServerClient(instanceIPAddress, DBuserName, DBuserPassword, instancePort);
            if (sqlServerClient.isConnected()) {
                String curVersion = sqlServerClient.getSQLEngineVersion();
                logger.info("Current SQL Version is : " + curVersion);

                if (curVersion.contains("Standard")) {
                    editVersion = "Standard Edition";
                }
                if (curVersion.contains("Enterprise")) {
                    editVersion = "Enterprise Edition";
                }
                if (curVersion.contains("11.0.2100.60") && curVersion.contains("Standard") ) {
                    logger.info(" Using version SQL Server 2012 " + editVersion);
                    assertThat(true);
                } else if (curVersion.contains("10.50.4000.0") || curVersion.contains("10.52.4000.0") ){
                    logger.info(" Using version SQL Server 2008R2 SP2" + editVersion);
                    if (editVersion == "Standard") {
                        assertThat(true);
                    } else {
                        assertThat(false);
                    }
                } else {
                    logger.info(" Current version is not supported by DBaaS...");
                    assertThat(false);
                }
                // Support SQL Server 2012, SQL Server 2008R2 SP2
            } else {
                throw new Exception("Can not connect to SQL server, IP address is : " +  instanceIPAddress);
            }

        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }


    // TODO
/*    public void testDataImportExport() throws Exception {
        try {
            String name = "Test Import and Export DB ";
            provisionInstance(name);
            sqlServerClient = new SQLServerClient(instanceIPAddress, DBuserName, DBuserPassword, instancePort);
            String database = "testImportDB";

            if (sqlServerClient.isConnected()){
                if (!sqlServerClient.isDatabaseExists(database)) {
                    sqlServerClient.createDatabase(database);
                    logger.info(" Created database %s !", database);
                } else {
                    logger.info(" Database %s exists already! ", database);
                }
                insertTableData = sqlServerClient.setInitalTableData();
                sqlServerClient.insertData(insertTableData, databaseName, tableName);    // INSERT DATA

            } else {
                throw new Exception("Can not connect to SQL server, IP address is : " +  instanceIPAddress);
            }

        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/


    @Test(groups={"datatest"})
    public void testMaxDBonInstance() throws Exception {
        try {
            logger.info("testMaxDBonInstance");
            String name = getNamePrefix();
            provisionInstance(name);
            sqlServerClient = new SQLServerClient(instanceIPAddress, DBuserName, DBuserPassword, instancePort);
            for (int i = 0; i < 8; i++) {
                if (sqlServerClient.isConnected()) {
                    String t_name = "test" + Integer.valueOf(i).toString();
                    sqlServerClient.createDatabase(t_name);
                } else {
                    throw new Exception("Can not connect to SQL server, IP address is : " +  instanceIPAddress);
                }
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            sqlServerClient.createDatabase("test_extra");
        } catch (Exception e) {
            assertThat(true);
            //assertThat(!sqlServerClient.isDatabaseExists("test_extra"));
            logger.info("Reach to the maximum DB numbers on this instance !");
        }
    }

/*
    // TODO
    public void testSAUserPermissionDisable() throws Exception {
        try {

        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }
*/

/*
    public void verifyInstanceData(SQLServerClient connection) throws Exception {
        try {
            listAllInstance();
            List<ListInstanceItem> listInstanceItems = checkNotNull((List<ListInstanceItem>) listResponse.getData());
            checkNotNull(listInstanceItems);
            logger.info("List instances not null, start to iterator instances...");
            for (ListInstanceItem listInstanceItem : listInstanceItems) {
                if (listInstanceItem.getId() != null) {
                    verifyInstanceId = listInstanceItem.getId();
                    verifyInstanceIPAddress = listInstanceItem.getIpAddress();
                    //verifyInstancePort = getInstancePort();
                    if (verifyTable(connection)) {
                        assertThat(verifyTableData(connection, databaseName, tableName));
                    } else {
                        break;
                    }
                } else {
                    throw new Exception("Can not find instance !!! ");
                }
            }
        } catch (DataAccessException e) {
            logger.info("Can not verify Data Test, test failed !!!!  ");
            e.printStackTrace();
        }
    }*/



    public static boolean verifyTable(SQLServerClient connection) {   // pass sqlServerClientDataVerification
        try {
            if (connection.isConnected()) {
                assertThat(connection.isDatabaseExists(databaseName));
                assertThat(connection.isTableExists(databaseName, tableName));
                if (connection.isTableExists(databaseName, tableName)) {
                    logger.info("Verified Table exists correctly ...");
                    return true;
                }
            } else {
                logger.info("(Verify Instance) Can not connect to SQL server, IP address is : " +  verifyInstanceIPAddress);
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

/*    public static void insertData(SQLServerClient connection, SQLServerClient.TableData tableData) {
        try {
            //listAllInstance();
            List<ListInstanceItem> listInstanceItems = checkNotNull((List<ListInstanceItem>) listResponse.getData());
            for (ListInstanceItem listInstanceItem : listInstanceItems) {
                if (listInstanceItem.getId() != null) {
                    verifyInstanceId = listInstanceItem.getId();
                    verifyInstanceIPAddress = listInstanceItem.getIpAddress();
                    verifyTable(connection);
                } else {
                    throw new Exception("Can not find instance !!! ");
                }
            }
        } catch (Exception e) {

        }
    }*/


    public boolean verifyDataCRUD(SQLServerClient connection, String database, String table) throws Exception {
        try {
            if (connection.isConnected()) {
                if (!connection.isDatabaseExists(database)) {
                    connection.createDatabase(database);      // CREATE dababase
                }
                if (!connection.isTableExists(database,table)) {
                    logger.info(" Starting to test CREATE Permission.... ");
                    connection.createTable(connection, database, table);   // CREATE TABLE
                    logger.info(" set inital TableData instance...");
                    insertTableData = connection.setInitalTableData();
                    logger.info(" Insert this inital TableData instance into (table)" + database + ".dbo." + table);
                    connection.insertData(insertTableData, database, table);    // INSERT DATA
                    logger.info(" Starting to test READ Permission.... ");
                    assertThat(connection.isDatabaseExists(database));     // READ DB
                    assertThat(connection.isTableExists(database, table));     // READ TABLE
                    logger.info(" Starting to test UPDATE Permission.... ");
                    logger.info("Get Pre-count numeber of table : " + database + ".dbo." + table);
                    int preCount = connection.getCount(database, table);
                    logger.info("Pre-count numeber is : " + preCount);
                    logger.info("Generate new data...");
                    testData = connection.generateSimpleData();
                    logger.info("Insert new data to table");
                    connection.updateTable(database, table);       // Insert/update
                    int postCount = connection.getCount(database, table);
                    logger.info("POST-count numeber is : " + postCount);
                    assertThat(postCount > preCount);
                    connection.updateTable(database, table);              // update
                    logger.info(" Starting to test DELETE Permission.... ");
                    connection.dropTable(database, table);                // drop table
                    assertThat(!connection.isTableExists(database,table));
                    connection.dropDatabase(database);                        // drop database
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
            //assertThat(instance.isConnected()).isFalse();
            //logger.info("Instance can not be connected...");
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


    // TODO   persistance
    public static void persistantData(SQLServerClient connection, String database, String table) throws Exception {
        try {
            logger.info(" Start to store Data from database: %s table: %s into file: dataPersistance.txt ", database, table);
            BufferedWriter bw = Files.newBufferedWriter(Paths.get("dataPersistance.txt"), StandardOpenOption.WRITE);
            if (connection.isConnected()) {
                if (connection.isTableExists(database, table)) {


                }
            }
            bw.write(instanceId);
            bw.newLine();
            bw.flush();
            bw.close();
            logger.info("Instance IP has been written into file, Completed! ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // TODO retrieve from persistance
    public static List<String> retrieveData(SQLServerClient connection) throws Exception {
        try {
            String database;
            String tablename;

            BufferedReader br = Files.newBufferedReader(Paths.get("dataPersistance.txt"));
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

    public SQLServerClient getSQLServerConnection() {
        return this.sqlServerClient;
    }
}

