package com.vmware.vchs.base.impl;

import com.vmware.vchs.helper.IamAuthInfo;
import com.vmware.vchs.launcher.TestHelper;
import com.vmware.vchs.test.client.rest.RestClient;
import com.vmware.vchs.test.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;

/**
 * Created by sjun on 7/28/15.
 */
public class DBaasRestClient extends RestClient {
    protected static final Logger logger = LoggerFactory.getLogger(IamAuthService.class);

    protected static Configuration configuration = TestHelper.getConfiguration();
    protected static IamAuthService iamAuthService = new IamAuthService(configuration.getPraxisServerConnection().getPraxisConnectUrl());

    private static IamAuthInfo fakeAuthInfo = new IamAuthInfo("fakeToken", "fakeServiceGroupId");

    private String username = configuration.getPraxisServerConnection().getDbadminUsername();
    private String password = configuration.getPraxisServerConnection().getDbadminPassword();

    public DBaasRestClient(String baseUrl) {
        super(baseUrl);
    }

    public DBaasRestClient() {
    }

    public DBaasRestClient(Configuration configuration) {
        super(configuration);
    }

    public void setAuthentication(String username, String password) {
        logger.info("Set IAM User: username={}, password={}", username, password);
        this.username = username;
        this.password = password;
    }

    public IamAuthInfo getIamAuthInfo() {
        if (configuration.isAuthentication()) {
            return iamAuthService.getToken(username, password);
        } else {
            return fakeAuthInfo;
        }
    }

    @Override
    protected HttpEntity createHttpEntity(Object request) {
        if (configuration.isAuthentication()) {
            String vcaToken = getIamAuthInfo().getToken();
            logger.info("Create Http Entity: user={}, vca-token={}", username, vcaToken);
            this.getHttpHeaders().set("vca-token", vcaToken);
        }
        return super.createHttpEntity(request);
    }
}
