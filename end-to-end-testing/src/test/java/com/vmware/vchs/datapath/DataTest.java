package com.vmware.vchs.datapath;

import com.vmware.vchs.pipeline.DataPathTest;
import com.vmware.vchs.pipeline.SQLServerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.testng.annotations.Test;

import java.sql.SQLException;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by fanz on 9/17/15.
 */
public class DataTest extends DataTestBase {

    protected static final Logger logger = LoggerFactory.getLogger(DataPathTest.class);

    /**
     *  Test cases
     */
    @Test(groups={"datapath", "full"})
    public void testDataRetainedFromSnapshot() throws Exception {
        try {
            logger.info("testDataRetainedFromSnapshot");
            String name = getNamePrefix();
            provisionInstance(name);
            logger.info(" Provision instance from snapshot Successful.");
            provisionSnapshot(checkNotNull(instanceId));
            restoreFromSnapshot(name, dbaasInstance);
            logger.info(" Restored instance from snapshot Successful.");
            logger.info(" Restored Instance ID : " + newInstanceId);
            logger.info(" Restored instance IP address: " + newInstanceIPAddress);
            logger.info(" Restored instance port: " + newInstancePort);

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

    @Test(groups={"datapath", "full"})
    public void testUserPermissionCRUD() throws Exception {
        try {
            // TEST CREATE
            logger.info("testUserPermissionCRUD") ;
            String name = getNamePrefix();
            provisionInstance(name);
            logger.info(" Provision instance from snapshot Successful.");

            sqlServerClient = new SQLServerClient(instanceIPAddress, DBuserName, DBuserPassword, instancePort);

            if (sqlServerClient.isConnected()) {
                if (!sqlServerClient.isDatabaseExists(databaseName)) {
                    sqlServerClient.createDatabase(databaseName);
                }
                if (!sqlServerClient.isTableExists(databaseName,tableName)) {
                    logger.info("DatabaseName arg is : " + databaseName);
                    logger.info("tableName arg is : " + tableName);
                    logger.info("Start to create table...");
                    sqlServerClient.createTable(sqlServerClient, databaseName, tableName);
                    logger.info("Start to initialize data...");
                    sqlServerClient.initialTableData(databaseName, tableName);
                    //insertTableData = sqlServerClient.setInitalTableData();
                    //logger.info("Start to insert data into table");
                    //sqlServerClient.insertData(insertTableData, databaseName, tableName);
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


    @Test(groups={"datapath", "full"})
    public void testDBPermission() throws Exception {
        try {
            logger.info("testDBPermission");
            String name = getNamePrefix();
            provisionInstance(name);
            logger.info(" Provision instance from snapshot Successful.");
            logger.info("Show IP :" + instanceIPAddress);
            sqlServerClient = new SQLServerClient(instanceIPAddress, DBuserName, DBuserPassword, instancePort);

            if (sqlServerClient.isConnected()) {
                if (!sqlServerClient.isDatabaseExists(databaseName)) {
                    sqlServerClient.createDatabase(databaseName);
                    logger.info(" Created database : " + databaseName);
                } else {
                    logger.info(" Database " + databaseName + " exists already! ");
                }
                if (!sqlServerClient.isTableExists(databaseName,tableName)) {
                    logger.info("Start to create table: " + tableName);
                    sqlServerClient.createTable(sqlServerClient, databaseName, tableName);
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


    @Test(groups={"datapath", "full"})
    public void testUpdatePasswordSSMSAndConnect() throws Exception{
        try {
            logger.info("testUpdatePasswordSSMSAndConnect");
            String name = getNamePrefix();
            provisionInstance(name);
            logger.info(" Provision instance from snapshot Successful.");
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
                    logger.info("Table " + tableName + "exists already! ");
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

    @Test(groups={"datapath", "full"})
    public  void testGrandUserRoleDB() throws Exception {
        try {
            logger.info("testGrandUserRoleDB");
            String name = getNamePrefix();
            provisionInstance(name);

            sqlServerClient = new SQLServerClient(instanceIPAddress, DBuserName, DBuserPassword, instancePort);

            if (sqlServerClient.isConnected()) {
                if (!sqlServerClient.isDatabaseExists(databaseName)) {
                    sqlServerClient.createDatabase(databaseName);
                    logger.info(" Created database :" + databaseName);
                } else {
                    logger.info(" Database " + databaseName + " exists already! ");
                }
                if (!sqlServerClient.isTableExists(databaseName,tableName)) {
                    sqlServerClient.createTable(sqlServerClient, databaseName, tableName);
                } else {
                    logger.info("Table " + tableName + "exists already! ");
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


    @Test(groups={"datapath", "full"})
    public  void testSQLSeverEngineCheck() throws Exception {
        try {
            dbEngineVersionMap.put("2012", "11.0.2100.60");
            dbEngineVersionMap.put("2008R2", "10.50.4000.0");
            // (11.2.5058.0)  10.52.4000.0  11.0.2100.60
            SQLEngineVerionList.add("11.0.2100.60");
            SQLEngineVerionList.add("10.50.4000.0");
            SQLEngineVerionList.add("10.52.4000.0");
            logger.info("testSQLSeverEngineCheck");
            String name = getNamePrefix();
            logger.info(name);
            String editVersion = null;
            provisionInstance(name);
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


    @Test(groups={"datapath", "full"})
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
            logger.info("Reach to the maximum DB numbers on this instance !");
        }
    }


}
