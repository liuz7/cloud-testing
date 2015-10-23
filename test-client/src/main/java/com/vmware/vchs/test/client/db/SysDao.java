/*
 * ******************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ******************************************************
 */
package com.vmware.vchs.test.client.db;

import org.springframework.dao.DataAccessException;


/**
 * @author liuda
 */
public interface SysDao
{
    boolean isTableExists(String tableName) throws DataAccessException;
    boolean isDatabaseExists(String dbName) throws Exception;
    String getCurrentTime() throws DataAccessException;
    boolean testConnection(String testQuery) throws Exception;
    void changePassword(String userName, String newPassword) throws Exception;
    void dropDatabase(String dbName) throws Exception;
    void createDatabase(String dbName) throws Exception;
    
}
