package com.vmware.vchs.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by liuda on 6/15/15.
 */
public class Constants {
    public static final String DBA_ADMIN_USER_NAME = "dbadminUsername";
    public static final String DBA_ADMIN_PASSWORD = "dbadminPassword";
    public static final String SECURE_RABBITMQ = "securerabbitmq";
    public static final String BILLING_PLAN_LICENSE = "dbaas.plan.mssql";
    public static final String BILLING_STORAGE = "dbaas.storage.ssd.provisioned";
    public static final String BILLING_BACKUP = "dbaas.storage.backup";
    public final static String BILLING_IOPS = "dbaas.iops.standard";
    public final static String BILLING_INGRESS = "dbaas.network.ingress";
    public final static String BILLING_EGRESS = "dbaas.network.egress";
    public static final String SERVICE_NAME = "com.vmware.vchs.dbaas";
    public final static Map<String, String> LICENSETYPE = new HashMap<>();
    public final static Map<String, String> EDITIONTYPE = new HashMap<>();
    public final static String DELIMITER="_";
    public final static String INVALID_STRING=UUID.randomUUID().toString();
    public final static String INVALID_FORMAT="invalid";
    static {
        LICENSETYPE.put("LI", "license");
        LICENSETYPE.put("BYOL", "byol");
        EDITIONTYPE.put("enterprise", "ee");
        EDITIONTYPE.put("standard", "se");
    }

}
