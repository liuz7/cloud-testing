package com.vmware.vchs.load.testcase;

import com.vmware.vchs.InstanceBaseTest;
import com.vmware.vchs.base.impl.TestClientImpl;
import com.vmware.vchs.constant.StatusCode;
import com.vmware.vchs.model.portal.common.AsyncResponse;
import org.springframework.http.ResponseEntity;

import java.net.HttpURLConnection;

//public class ProvisionTest implements TestCase{

public class ProvisionTest extends InstanceBaseTest implements TestCase {

    public ProvisionTest() {
        try {
            setUpSuite();
            this.createInstanceRequest = buildInstanceRequest("mssql_2008R2", "Small");
            if (testClient == null) {
                testClient = new TestClientImpl();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public TestResult runTest() {
        TestResult r = new TestResult();
        ResponseEntity<AsyncResponse> responseEntity = testClient.createDBInstanceEntity(this.createInstanceRequest);

        if (responseEntity.getStatusCode().value() != HttpURLConnection.HTTP_OK) {
            return r;
        }

        AsyncResponse createResponse = responseEntity.getBody();
        if (createResponse == null || createResponse.getId() == null) {
            return r;
        }

        if (!createResponse.getStatus().contains(StatusCode.CREATING.value())) {
            return r;
        }

        System.out.println("Success!");
        r.setRequestId("");
        r.setSuccess(true);

        return r;
    }
}
