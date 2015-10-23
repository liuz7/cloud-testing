package com.vmware.vchs.performance.instance;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.vmware.vchs.common.utils.RetryTask;
import com.vmware.vchs.constant.StatusCode;
import com.vmware.vchs.model.portal.common.AsyncResponse;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.performance.PerformanceBaseTest;
import com.vmware.vchs.performance.model.Request;
//import com.vmware.vchs.performance.model.TestCase;
import com.vmware.vchs.performance.repository.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by georgeliu on 14/11/24.
 */
public class GetInstanceTest extends PerformanceBaseTest {

    private static GetInstanceResponse getInstanceResponse;
//    private TestCase currentTest;
    private long started = -1;

    @Autowired
    TestCaseRepository testCaseRepository;
    @BeforeClass
//    @BeforeMethod
    public void startTest(Method method) throws Exception {
//        this.currentTest = new TestCase(method.getName());
    }

    @Test
    public void a(){

    }

//    @BeforeClass(alwaysRun = true)
//    @Test(groups = {"setUp"})
//    @Parameters({"dbEngineVersion", "planName"})
//    public void setUp(@Optional("mssql_2008R2") String dbEngineVersion, @Optional("Small") String planName) throws Exception {
//        this.createInstanceRequest = buildInstanceRequest(dbEngineVersion, planName);
//        AsyncResponse createResponse = testClient.createDBInstance(this.createInstanceRequest);
//        RetryTask<GetInstanceResponse> retryTask = new RetryTask<>(new GetActiveDBInstanceTask(createResponse.getId()));
//        this.getInstanceResponse = retryTask.call();
//    }
//
//    @Test
//    @Parameters({"virtualUserSize", "totalRequestSize"})//TODO Follow the apache benchmark model
//    public void testMultipleGetDBInstance(@Optional("5") String virtualUserSize, @Optional("200") String totalRequestSize) throws Exception {
//        Multimap<Long, ListenableFuture<ResponseEntity<GetInstanceResponse>>> results = ArrayListMultimap.create();
//        int totalRequests = Integer.parseInt(totalRequestSize);
//        int concurrencySize = Integer.parseInt(virtualUserSize);
//        while (totalRequests > 0) {
//            if (totalRequests < concurrencySize) {
//                concurrencySize = totalRequests;
//            }
//            started = System.currentTimeMillis();
//            for (int i = 0; i < concurrencySize; i++) {
//                httpRequestMetrics.getScheduledConnections().incrementAndGet();
//                ListenableFuture<ResponseEntity<GetInstanceResponse>> result = testClient.getAsyncDBInstanceEntity(this.getInstanceResponse.getId());
//                results.put(new Long(started), result);
//            }
//            results.values().parallelStream().forEach(e -> verifyGetResponse(e, this.currentTest));
//            totalRequests = totalRequests - concurrencySize;
//            results.clear();
//        }
//    }
//
//    private void verifyGetResponse(ListenableFuture<ResponseEntity<GetInstanceResponse>> getInstanceResponseFuture, TestCase currentTest) {
//        GetInstanceResponse getInstanceResponse = null;
//        long ended = -1;
//        try {
//            getInstanceResponse = getInstanceResponseFuture.get().getBody();
//            if (getInstanceResponse != null) {
//                ended = System.currentTimeMillis();
//                httpRequestMetrics.getSuccessfulConnections().increment(started);
//            }
//        } catch (InterruptedException | ExecutionException e) {
//            httpRequestMetrics.getFailedConnections().increment(started);
//            ended = System.currentTimeMillis();
//            e.printStackTrace();
//        } finally {
//            Request request = new Request(ended - started, testClient.getRestClient().getRequestInfo().getMethod(), testClient.getRestClient().getRequestInfo().getPath());
//            request.setTestCase(currentTest);
//            currentTest.getRequests().add(request);
//            httpRequestMetrics.getRequests().increment(started);
//            httpRequestMetrics.getTasks().increment(started);
//            httpRequestMetrics.getActiveConnections().decrementAndGet();
//        }
//        assertThat(getInstanceResponse).isNotNull();
//        assertThat(getInstanceResponse.getStatus()).isEqualTo(StatusCode.RUNNING.value());
//        //assertThat(testDbConnection(getDbConnection((InstanceResource) getInstanceResponse.getInstanceDetails()))).isTrue();
//    }

    @AfterMethod(alwaysRun = true)
    @Test(groups = {"tearDown"})
    public void tearDownMethod(ITestResult testResult) throws Exception {
//        currentTest.setExecutedAt(new Date(testResult.getStartMillis()));
//        testCaseRepository.save(currentTest);
    }

//    @AfterClass(alwaysRun = true)
//    @Test(groups = {"tearDown"})
//    public void tearDownClass() throws Exception {
//        super.tearDownClass();
//        clearInstances();
//    }
}
