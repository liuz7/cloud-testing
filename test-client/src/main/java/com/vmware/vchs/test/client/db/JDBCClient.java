package com.vmware.vchs.test.client.db;

import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Map;

/**
 * The JDBC client interface to interact with the databases.
 */
public interface JDBCClient {

    boolean testConnection(String testQuery) throws Exception;

    <T> void insert(T instance) throws Exception;

    <T> List<Map<String, Object>> findAllRows(Class<T> klazz) throws Exception;

    <T> List<T> findClassRows(String dbName, Class<T> klazz, RowMapper mapper) throws Exception;

    <T> boolean createTable(Class<T> klazz) throws Exception;

    void createDatabase(String dbName) throws Exception;

    boolean isTableExists(String tableName) throws Exception;

    void useDatabase(String dbName) throws Exception;

    void dropDatabase(String dbName) throws Exception;

    boolean isDatabaseExists(String dbName) throws Exception;

    String getCurrentTime() throws Exception;

    void changePassword(String userName, String newPassword) throws Exception;

    void truncateTable(String tableName) throws Exception;

    int rownum(String tableName) throws Exception;
}
