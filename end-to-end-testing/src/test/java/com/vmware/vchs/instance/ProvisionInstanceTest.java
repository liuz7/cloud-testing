package com.vmware.vchs.instance;

import com.vmware.vchs.base.BaseDataProvider;
import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.common.utils.Utils;
import com.vmware.vchs.common.utils.exception.RestException;
import com.vmware.vchs.constant.StatusCode;
import com.vmware.vchs.link.LinkBuilder;
import com.vmware.vchs.model.constant.PlanModel;
import com.vmware.vchs.model.portal.common.ListResponse;
import com.vmware.vchs.model.portal.instance.Connections;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.model.portal.instance.Plan;
import com.vmware.vchs.test.client.vcloud.LoginAdapter;
import com.vmware.vchs.test.client.vcloud.VMAdapter;
import com.vmware.vcloud.sdk.Vdc;
import org.springframework.dao.TransientDataAccessResourceException;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import static com.vmware.vchs.test.client.db.SQLStatements.DB_TESTDB;
import static org.assertj.core.api.Assertions.*;

/**
 * Created by georgeliu david on 14/11/4.
 */
public class ProvisionInstanceTest extends InstanceTest {


    @Test(groups = {"sanity"}, priority = 0)
    public void testProvisionDB() throws Exception {
        final String queryCpu="SELECT COUNT( distinct cpu_id) FROM sys.dm_os_schedulers";
        final String queryMemory="SELECT cntr_value/1024/1024\n" +
                "FROM sys.dm_os_performance_counters\n" +
                "WHERE  counter_name = 'Target Server Memory (KB)'";
        DbaasInstance instance = builder.createInstanceEntity(getNamePrefix());
        assertThat(instance.getStatusCode() / 100 == 2).isTrue();
        assertThat(instance.getInstanceid()).isNotNull();
        assertThat(instance.getStatus()).contains(StatusCode.CREATING.value());
        GetInstanceResponse instanceCreated = instance.waitAndGetAvailableInstance();
        assertThat(instanceCreated.getStatus()).isIn(dbaasApi.getAvailableStatusList());
        assertThat(instanceCreated.getIpAddress()).isNotNull();
        int actualCpu=instance.getMssqlConnection().getTemplate().queryForObject(queryCpu,Integer.class);
        int actualMemoryInGB=instance.getMssqlConnection().getTemplate().queryForObject(queryMemory,Integer.class);
        logger.info("actual cpu for mssql is "+actualCpu);
        logger.info("actual cpu for mssql is " +actualMemoryInGB);
        assertThat(instanceCreated.getPlan().getVcpu()).isEqualTo(actualCpu);
        int expectMemoryInGB=instanceCreated.getPlan().getMemory()/1024;
        //actual memory should be larger than half of expect memory
        assertThat(expectMemoryInGB/2).isLessThan(actualMemoryInGB);
        assertThat(instance.isDatabaseExists(DB_TESTDB)).isTrue();
        instance.dropDatabase(DB_TESTDB);
        assertThat(instance.isDatabaseExists(DB_TESTDB)).isFalse();
    }



    /*@Test
    public void testRebootVMAfterProvisionDB() throws Exception {
        String adminVdcName = "dbaas_" + configuration.getVdc().getAdminVdcName();
        LoginAdapter loginAdapter = new LoginAdapter(configuration.getVdc().getOrgUrl(), configuration.getVdc().getOrgName(), configuration.getVdc().getAdminVdcName(), configuration.getVdc().getPassword());
        loginAdapter.login();
        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);
        VMAdapter vmAdapter = new VMAdapter("cds-vapp-eb691b7dcaea497caba255a311e07381", "vm0", loginAdapter.getVCloudClient(), vdc);
        vmAdapter.reboot();
        loginAdapter.logout();
    }*/

    /*@Test(groups = {"sanity"})
    public void testProvisionDBWithLargeData() throws Exception {
        ResponseEntity<AsyncResponse> responseEntity = testClient.createDBInstanceEntity(this.createInstanceRequest);
        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpURLConnection.HTTP_OK);
        AsyncResponse createResponse = responseEntity.getBody();
        assertThat(createResponse).isNotNull();
        assertThat(createResponse.getId()).isNotNull();
        assertThat(createResponse.getStatus()).contains(StatusCode.CREATING.value());
        GetInstanceResponse instanceCreated = this.retryTask.execute(new GetActiveDBInstanceTask(createResponse.getId()));
        assertThat(instanceCreated.getStatus()).isEqualTo(StatusCode.AVAILABLE.value());
        assertThat(instanceCreated.getIpAddress()).isNotNull();
        MsSqlDaoFactory jdbcClient = getDbConnection(testClient.getDBInstance(instanceCreated.getId()));
        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
        jdbcClient.createSysDao().createDatabase(DB_TESTDB);
        assertThat(jdbcClient.createSysDao().isDatabaseExists(DB_TESTDB)).isTrue();
        MsSqlDataLoader msSqlDataLoader = jdbcClient.createMsSqlDataLoader();
        loadDataBySize(msSqlDataLoader, DB_TESTDB, LARGE_DATA_TABLE_NAME, 350);
    }*/

    @Test
    public void testCreateMultipleDBAfterProvisionDB() throws Exception {
        DbaasInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        for (int i = 1; i < 8; i++) {
            String dbName = DB_TESTDB + i;
            instance.createDatabase(dbName);
            assertThat(instance.isDatabaseExists(dbName)).isTrue();
        }
        try {
            instance.createDatabase(DB_TESTDB + 8);
            assertThat(instance.isDatabaseExists(DB_TESTDB + 8)).isTrue();
            failBecauseExceptionWasNotThrown(TransientDataAccessResourceException.class);
        } catch (TransientDataAccessResourceException e) {
            Utils.getStackTrace(e);
        }
    }

    @Test(groups = {"snsonly"})
    public void testProvisionDBWithCustomPort() throws Exception {
        Connections connections = dbaasApi.generateConnections();
        connections.getDataPath().setDestPort(1543);
        builder.setConnections(connections).createInstanceWithRetry(getNamePrefix());
    }

    @Test(dataProvider = "invalid port", dataProviderClass = BaseDataProvider.class)
    public void testProvisionDBWithInvalidCustomPort(int port) throws Exception {
        Connections connections = dbaasApi.generateConnections();
        connections.getDataPath().setDestPort(port);
        try {
            builder.setConnections(connections).createInstanceWithRetry(getNamePrefix());
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
//            assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.INVALID_ARGUMENT.getTitle());
        }
    }

    @Test
    public void testProvisionDBWithNonRequiredPropertiesSetToNull() throws Exception {
        builder.setDescription(null).createInstanceWithRetry(getNamePrefix());
    }

    @Test(groups = {"nodeConsume"})
    public void testProvisionDBWithoutAvailableNodes() throws Exception {
        //FIXME: magic number here to prevent create many instances if there are enough nodes.
        // Usually we have 6 nodes deployed in test env.
        final int maxNodes = 10;
        List<DbaasInstance> instanceIds = new ArrayList<>();
        DbaasInstance instance;
        while (instanceIds.size() < maxNodes) {
            try {
                instance = builder.createInstance(getNamePrefix());
                if (instance != null) {
                    instanceIds.add(instance);
                }
            } catch (RestException e) {
                assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_INTERNAL_ERROR);
//                assertThat(((PortalError) e.getError()).getCode()).contains(PortalErrorMap.PortalStatus.UNABLE_TO_ALLOCATE_RESOURCE.getCode());
                //assertThat(((PortalError) e.getError()).getMessage()).isEqualTo(Error.RESOURCE_UNAVAILABLE.value());
                break;
            }
        }
        for (DbaasInstance instanceId : instanceIds) {
            instanceId.waitAndGetAvailableInstance();
        }

        if (instanceIds.size() >= maxNodes) {
            fail(String.format("It should not be able to create %d instances.", maxNodes));
        }
    }

    @Test
    public void testProvisionDBWithIncorrectPlan() throws Exception {
        PlanModel planData = dbaasApi.getPlans().get(configuration.getPlanName());
        int originalCpu = planData.getCpu();
        planData.setCpu(1000);
        try {
            builder.setPlan(planData.toPlan()).createInstance(getNamePrefix());
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
//            assertThat(((PortalError) e.getError()).getCode()).contains(PortalErrorMap.PortalStatus.INVALID_ARGUMENT.getCode());
        } finally {
            planData.setCpu(originalCpu);
        }
    }

    @Test
    public void testProvisionMultipleInstanceWithSamePayload() throws Exception {
        DbaasInstance instance1 = builder.createInstanceWithRetry(getNamePrefix());
        DbaasInstance instance2 = builder.createInstanceWithRetry(getNamePrefix());
        instance1.deleteWithRetry();
        ListResponse listResponse = dbaasApi.listDBInstances();
        assertThat(listResponse.getTotal()).isGreaterThanOrEqualTo(1);
        assertThat(instance1.getInstanceid()).isNotEqualTo(instance2.getInstanceid());
        sleepBySeconds(5);
        assertThat(instance1.isConnected()).isFalse();
    }

    @Test
    public void testProvisionWithInvalidJsonString() throws Exception {
        try {
            dbaasApi.getRestClient().postForString(LinkBuilder.getInstancePath(), "invalidJsonString");
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
//            assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.INVALID_REQUEST.getCode());
            //assertThat(((PortalError) e.getError()).getMessage()).isEqualTo(Error.INVALID_JSON.value());
        }
    }

    @Test
    public void testProvisionDBDuringUnProvision() throws Exception {
        DbaasInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        instance.delete();
        try {
            DbaasInstance instance2 = builder.createInstance(getNamePrefix());
            dbaasApi.listDBInstances();
            GetInstanceResponse instanceCreated2 = instance2.waitAndGetAvailableInstance();
            assertThat(instanceCreated2.getStatus()).isIn(dbaasApi.getAvailableStatusList());
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
//            assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.RESOURCE_NOT_FOUND.getTitle());
        }
    }

    @Test
    public void testProvisionDBAfterUnProvision() throws Exception {
        DbaasInstance instance1 = builder.createInstanceWithRetry(getNamePrefix());
        instance1.deleteWithRetry();
        DbaasInstance instance2 = builder.createInstanceWithRetry(getNamePrefix());
        assertThat(instance2.getStatus()).isIn(dbaasApi.getAvailableStatusList());
    }

    @Test
    public void testProvisionDBWithRequiredPropertiesSetToNull() throws Exception {
        try {
            builder.setIsPlanEmpty(true).createInstance(getNamePrefix());
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
//            assertThat(((PortalError) e.getError()).getCode()).contains(PortalErrorMap.PortalStatus.INVALID_ARGUMENT.getCode());
        }
    }

    @Test
    public void testConcurrentProvisionDB() throws Exception {
        int oldSize = dbaasApi.listDBInstances().getTotal();
        int threadSize = getThreadSizeForInstance();
        List<DbaasInstance> instanceList = new ArrayList<>();
        for (int i = 0; i < threadSize; i++) {
            instanceList.add(builder.createInstance(getNamePrefix()));
        }
        for (int i = 0; i < threadSize; i++) {
            GetInstanceResponse response = instanceList.get(i).waitAndGetAvailableInstance();
            assertThat(response.getStatus()).isIn(dbaasApi.getAvailableStatusList());
        }
        assertThat(threadSize).isEqualTo(instanceList.size());
        int size = dbaasApi.listDBInstances().getTotal();
        assertThat(size).isEqualTo(oldSize + threadSize);
    }

    @Test
    public void testConcurrentProvisionDBandUnProvisionDB() throws Exception {
        int oldSize = dbaasApi.listDBInstances().getTotal();
        DbaasInstance unprovisionInstance = builder.createInstanceWithRetry(getNamePrefix());
        DbaasInstance provisionInstance = builder.createInstance(getNamePrefix());
        unprovisionInstance.delete();
        GetInstanceResponse instanceNewCreated = provisionInstance.waitAndGetAvailableInstance();
        assertThat(instanceNewCreated.getStatus()).isIn(dbaasApi.getAvailableStatusList());
        unprovisionInstance.waitInstanceDeleted();
        int size = dbaasApi.listDBInstances().getTotal();
        assertThat(size).isEqualTo(oldSize + 1);
    }

//    @Ignore(reasons = "move it to performance test")
//    @Test
//    public void testDataPathAfterProvisionDB() throws Exception {
//        GetInstanceResponse instanceCreated = createInstanceWithRetry(this.createInstanceRequest);
//        MsSqlDaoFactory jdbcClient = getDbConnection(testClient.getDBInstance(instanceCreated.getId()));
//        assertThat(testDbConnectionWithRetry(jdbcClient)).isTrue();
//        int count = 0;
//        long startTime = System.currentTimeMillis();
//        long endTime = startTime;
//        while (endTime - startTime < 5 * 60 * 1000) {
//            Employee employee = new Employee(count, "aaa" + count, "bbb" + count);
//            insertData(jdbcClient, DB_TESTDB, employee);
//            count++;
//            endTime = System.currentTimeMillis();
//        }
//        assertThat(jdbcClient.createEmployeeDao().findAllRows().size()).isEqualTo(count);
//    }

    @Test
    public void testProvisionDBWithIncorrectPlanName() throws Exception {
        try {
            builder.setPlan(new Plan()).createInstance(getNamePrefix());
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
//            assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.INVALID_ARGUMENT.getCode());
        }
    }

//    @Test
//    @Ignore(reasons = "https://vchs-eng.atlassian.net/browse/PORT-614")
//    public void testProvisionDBWithIncorrectDisk() throws Exception {
//        this.createInstanceRequest.setDiskSize(20480);
//        try {
//            sendCreateInstanceRequest(this.createInstanceRequest);
//            failBecauseExceptionWasNotThrown(RestException.class);
//        } catch (RestException e) {
//            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
//            assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.INVALID_ARGUMENT.getCode());
//        }
//    }
}
