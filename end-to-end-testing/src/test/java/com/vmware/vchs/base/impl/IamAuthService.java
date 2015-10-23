package com.vmware.vchs.base.impl;

import com.vmware.vchs.common.utils.Utils;
import com.vmware.vchs.helper.IamAuthInfo;
import com.vmware.vchs.iam.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sjun on 7/28/15.
 */
public class IamAuthService {
    protected static final Logger logger = LoggerFactory.getLogger(IamAuthService.class);

    private String praixUrl;
    private Map<String, IamAuthInfo> tokenMap;
    private Object lock = new Object();
    public static int GET_TOKEN_TIMEOUT=3600;

    public IamAuthService(String praixUrl) {
        this.tokenMap= new HashMap<>();
        this.praixUrl = praixUrl;
    }

    private IamAuthInfo getAccessToken(String username, String password){
        logger.info("Get IAM token for user " + username);
        if(GET_TOKEN_TIMEOUT<=0) {
            throw new RuntimeException("GET IAM token total timeout exceeds 3600s");
        }
        HttpClient.Response response = null;
        int retries = 3;
        while (retries>0) {
            try {
                response = HttpClient.getAccessToken(username, password, praixUrl);
                String token = response.getToken();
                if (token != null) {
                    logger.info("IAM token:" + token);
                    GET_TOKEN_TIMEOUT=3600;
                    return new IamAuthInfo(token, response.getServiceGroupId());
                }
            } catch (Exception e) {
                logger.warn(Utils.getStackTrace(e));
            }
            retries--;
            GET_TOKEN_TIMEOUT-=300;
            try {
                Thread.sleep(300*1000);
            } catch (InterruptedException e) {
            }
        }
        logger.warn("Get IAM token failed for user " + username);

        //TODO: change the runtime exception to normal exception
        throw new RuntimeException("Get IAM token failed for user " + username);
    }

    public IamAuthInfo getToken(String username, String password) {
        synchronized (lock) {
            IamAuthInfo tokenInfo = tokenMap.get(username);
            if( tokenInfo == null || tokenInfo.isExpired()) {
                tokenInfo = getAccessToken(username, password);
                tokenMap.put(username, tokenInfo);
            }
            return tokenInfo;
        }
    }
}
