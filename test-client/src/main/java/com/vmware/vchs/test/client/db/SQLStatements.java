/*
 * ******************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ******************************************************
 */
package com.vmware.vchs.test.client.db;

/**
 * @author liuda
 */
public class SQLStatements {
    public static final String DB_TESTDB = "testdb";
    public static final String DB_TESTDB2 = "testdb2";
    public static final String TBL_EMPLOYEE = "employee";

    static final String QTBL_EMPLOYEE = DB_TESTDB + "." + TBL_EMPLOYEE;
    static final String SQL_SELECT_ALL_EMPLOYEE = "SELECT * FROM " + QTBL_EMPLOYEE;

    static final String SQL_LIMIT_CLAUSE = " LIMIT %d";

}
