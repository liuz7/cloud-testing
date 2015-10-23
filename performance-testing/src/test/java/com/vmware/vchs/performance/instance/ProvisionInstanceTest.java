package com.vmware.vchs.performance.instance;

import com.vmware.vchs.common.utils.RetryTask;
import com.vmware.vchs.constant.StatusCode;
import com.vmware.vchs.model.portal.common.AsyncResponse;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.performance.PerformanceBaseTest;
import com.vmware.vchs.test.client.db.MsSqlDaoFactory;
import com.vmware.vchs.testng.Ignore;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;

import static com.vmware.vchs.test.client.db.SQLStatements.DB_TESTDB;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by georgeliu on 14/11/24.
 */
public class ProvisionInstanceTest extends PerformanceBaseTest {

    @Test
    @Ignore(reasons = "not ready")
    public void testAsyncProvisionDB() throws Exception {
        ListenableFuture<ResponseEntity<AsyncResponse>> responseEntityListenableFuture = testClient.createAsyncDBInstanceEntity(this.createInstanceRequest);
        ResponseEntity<AsyncResponse> responseEntity = responseEntityListenableFuture.get();
        assertThat(responseEntity.getStatusCode().value()).isEqualTo(HttpURLConnection.HTTP_OK);
        AsyncResponse createResponse = responseEntity.getBody();
        assertThat(createResponse).isNotNull();
        assertThat(createResponse.getId()).isNotNull();
        assertThat(createResponse.getStatus()).contains(StatusCode.CREATING.value());
        RetryTask<GetInstanceResponse> retryTask = new RetryTask<>(new GetActiveDBInstanceTask(createResponse.getId()));
        GetInstanceResponse instanceCreated = retryTask.call();
        assertThat(instanceCreated.getStatus()).isEqualTo(StatusCode.RUNNING.value());
        MsSqlDaoFactory jdbcClient = getDbConnection(testClient.getDBInstance(instanceCreated.getId()));
        assertThat(testDbConnection(jdbcClient)).isTrue();
        assertThat(testDbConnection(jdbcClient)).isTrue();
        jdbcClient.createSysDao().createDatabase(DB_TESTDB);
        assertThat(jdbcClient.createSysDao().isDatabaseExists(DB_TESTDB)).isTrue();
        jdbcClient.createSysDao().dropDatabase(DB_TESTDB);
        assertThat(jdbcClient.createSysDao().isDatabaseExists(DB_TESTDB)).isFalse();
    }
}
