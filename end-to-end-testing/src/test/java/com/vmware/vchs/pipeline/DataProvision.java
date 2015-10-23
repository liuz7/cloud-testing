package com.vmware.vchs.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by fanz on 8/20/15.
 */
public class DataProvision extends PipelineBase {
    private static final Logger logger = LoggerFactory.getLogger(DataProvision.class);

    public static SQLServerClient sqlServerClient;
    public static SQLServerClient.TableData insertTableData;
    public final static String namePrefix = "ProvisionDataForPipelineTest" ;

    @Test(groups={"pipeline", "populateData"})
    public void provisionData() throws Exception {
        try {
            logger.info("Start Pipeline Test - Populate Data");
            provisionInstance(namePrefix);
            logger.info(" Provision instance from snapshot Successful.");
            logger.info(" Database name is : " + databaseName);
            logger.info(" Table name is : " + tableName);
            logger.info(" DB user name is : " + DBuserName);

            sqlServerClient = new SQLServerClient(instanceIPAddress, DBuserName, DBuserPassword, instancePort);

//            if (instanceId != null) {
//                storeInstanceIP(instanceId);
//                logger.info("Instance ID which is stored is : " + instanceId);
//            }

            logger.info("File path is : " + this.getClass().getClassLoader().getResource("instanceIP.txt").toString());

            if (sqlServerClient.isConnected()) {
                if (!sqlServerClient.isDatabaseExists(databaseName)) {
                    logger.info("No database found. Start to create database...");
                    sqlServerClient.createDatabase(databaseName);
                }
                if (!sqlServerClient.isTableExists(databaseName,tableName)) {
                    logger.info("No table found. Start to create table...");
                    sqlServerClient.createTable(sqlServerClient, databaseName, tableName);
                    logger.info("Start to initialize data...");
                    sqlServerClient.initialTableData(databaseName, tableName);
                    logger.info(" Starting to verify data Exists ");
                    assertThat(sqlServerClient.isDatabaseExists(databaseName));
                    logger.info("Start to verify table Exists");
                    assertThat(sqlServerClient.isTableExists(databaseName, tableName));
                    logger.info(" Completed CREATE Permission test. ");
                } else {
                    logger.info("Table " + tableName + " is existed already! ");
                }
            } else {
                throw new Exception("Can not connect to SQL server, IP address is : " +  instanceIPAddress);
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
    }


    public SQLServerClient getSQLServerConnection() {
        return this.sqlServerClient;
    }


}
