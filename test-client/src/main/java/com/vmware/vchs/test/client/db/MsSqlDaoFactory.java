/*
 * ******************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ******************************************************
 */
package com.vmware.vchs.test.client.db;


/**
 * @author liuda
 */
public interface MsSqlDaoFactory {

    EmployeeDao createEmployeeDao();

    SysDao createSysDao();

    MsSqlDataLoader createMsSqlDataLoader();
}
