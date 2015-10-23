package com.vmware.vchs.pipeline;

import com.vmware.vchs.base.BaseTest;
import com.vmware.vchs.base.DbaasApi;
import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.base.E2ETest;
import com.vmware.vchs.datapath.MssqlConnection;
import com.vmware.vchs.launcher.TestHelper;
import com.vmware.vchs.model.portal.common.AsyncResponse;
import com.vmware.vchs.model.portal.common.ListResponse;
import com.vmware.vchs.model.portal.instance.Connections;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.model.portal.snapshot.CreateSnapshotRequest;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import org.springframework.dao.DataAccessException;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by fanz on 9/7/15.
 */

public class DataPathBase extends BaseTest implements E2ETest {


    public static GetInstanceResponse getInstanceResponsePipeline;
    public static GetInstanceResponse getNewInstanceResponsePipeline;   // Get new instance response

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
    //public static DbaasApi dbaasApi = new DbaasApi(configuration);   // new api
    public static DbaasApi dbaasApi;
    public static MssqlConnection mssqlConnection;

    protected static DbaasInstance.DbaasInstanceBuilder builder;

    public static final String version = configuration.getDbEngineVersion();

    public static Map<String, String> dbEngineVersionMap = new HashMap<>();


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
            logger.info("#### DBuserName of instance : " + DBuserName);
            logger.info("#### DBuserPassword of instance : " +DBuserPassword);

            //listAllInstance();
            //mssqlConnection = dbaasApi.getMssqlConnection(getInstanceResponsePipeline, DBuserPassword);

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
}
