package com.vmware.vchs.base;

import org.testng.annotations.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.assertj.core.api.Assertions.fail;

/**
 * The InitTest to check the precondition is met for testing.
 */
@Test(groups = "initPortal", timeOut = 5000)
public class InitTest extends BaseTest {

    @Test
    public void serverStartedOk() throws Exception {
        String baseUrl = dbaasApi.getRestClient().getBaseUrl();
        try {
            URL url = new URL(baseUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();
        } catch (IOException e) {
            fail("Error creating HTTP connection to " + baseUrl);
        }
    }
}
