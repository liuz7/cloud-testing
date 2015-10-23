package com.vmware.vchs.test.client.db;

/**
 * Created by georgeliu on 15/8/12.
 */
public interface MsSqlDataLoader {

    boolean testConnection(String testQuery) throws Exception;

    boolean insert(String tableName, String filePath) throws Exception;

    boolean createTable(String tableName) throws Exception;

    void useDatabase(String dbName) throws Exception;

    long getDBSize() throws Exception;

}
