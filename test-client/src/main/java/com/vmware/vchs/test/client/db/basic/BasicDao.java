package com.vmware.vchs.test.client.db.basic;

import com.vmware.vchs.common.utils.ObjectUtils;
import com.vmware.vchs.test.client.db.JDBCClient;
import com.vmware.vchs.test.client.db.SysDao;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * The MS sql server client.
 */
public interface BasicDao {


    boolean testConnection(String testQuery) throws Exception;

    <T> void insert(T instance) throws Exception;

    <T> List<Map<String, Object>> findAllRows(Class<T> klazz) throws Exception;

    <T> List<T> findClassRows(String dbName, Class<T> klazz, RowMapper mapper) throws Exception;

    <T> boolean createTable(Class<T> klazz) throws Exception;



    void useDatabase(String dbName) throws Exception;







    void truncateTable(String name) throws Exception;

    int rownum(String tableName) throws Exception;
}
