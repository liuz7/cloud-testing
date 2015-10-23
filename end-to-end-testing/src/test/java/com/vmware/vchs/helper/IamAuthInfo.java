package com.vmware.vchs.helper;

import java.util.Calendar;

/**
 * Created by sjun on 7/28/15.
 */
public class IamAuthInfo {
    private static final long EXPIRED_TIME = 12 * 60 * 1000;

    private String serviceGroupId = null;
    private String token = null;
    private long expiredTime;

    public IamAuthInfo(String token, String serviceGroupId) {
        this.token = token;
        this.serviceGroupId = serviceGroupId;
        this.expiredTime = EXPIRED_TIME + Calendar.getInstance().getTimeInMillis();
    }

    public String getToken() {
        return token;
    }

    public String getServiceGroupId() {
        return serviceGroupId;
    }

    public boolean isExpired() {
        long now = Calendar.getInstance().getTimeInMillis();
        return now >= expiredTime;
    }
}
