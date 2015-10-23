package com.vmware.vchs.test.client.db.mysql;

import com.vmware.vchs.common.utils.ObjectUtils;
import com.vmware.vchs.test.client.db.JDBCClient;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Map;

/**
 * The mysql client.
 */
public class MySqlJDBCClient implements JDBCClient {

    private JdbcTemplate jdbcTemplate;
    private ObjectUtils objectUtils;
    private static final Logger logger = LoggerFactory.getLogger(MySqlJDBCClient.class);

    public MySqlJDBCClient(String url, String username, String password) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.objectUtils = new ObjectUtils();
        logger.info(url + " is created.");
    }

    @Override
    public boolean testConnection(String testQuery) throws DataAccessException {
        boolean result = false;
        if (this.jdbcTemplate != null) {
            List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(testQuery);
            if (rows != null) {
                result = true;
                logger.info("Mysql Server Connected.");
            }
        }
        return result;
    }

    @Override
    public <T> void insert(T instance) throws DataAccessException {
        String tableName = instance.getClass().getSimpleName().toLowerCase();
        StringBuffer sb = new StringBuffer("");
        sb.append("INSERT INTO " + tableName + " (");
        List<String> properties = this.objectUtils.getProperty(instance.getClass());
        for (String key : properties) {
            if (key.equalsIgnoreCase(properties.get(properties.size() - 1))) {
                sb.append(key);
            } else {
                sb.append(key + ",");
            }
        }
        sb.append(") VALUES (");
        for (String key : this.objectUtils.getProperty(instance.getClass())) {
            if (key.equalsIgnoreCase(properties.get(properties.size() - 1))) {
                sb.append("?");
            } else {
                sb.append("?,");
            }
        }
        sb.append(")");
        this.jdbcTemplate.update(sb.toString(), (Object[]) this.objectUtils.getPropertyValue(instance).toArray());
        logger.info("New " + tableName + " inserted at " + getCurrentTime());
    }

    @Override
    public <T> List<Map<String, Object>> findAllRows(Class<T> klazz) throws DataAccessException {
        String tableName = klazz.getSimpleName().toLowerCase();
        StringBuffer sb = new StringBuffer("");
        sb.append("SELECT * FROM " + tableName);
        return jdbcTemplate.queryForList(sb.toString());
    }

    @Override
    public void truncateTable(String tableName) throws Exception {
        jdbcTemplate.execute("DELETE FROM " + tableName);
    }

    @Override
    public <T> List<T> findClassRows(String dbName, Class<T> klazz, RowMapper mapper) throws DataAccessException {
        String tableName = klazz.getSimpleName().toLowerCase();
        StringBuffer sb = new StringBuffer("");
        sb.append("SELECT * FROM " + dbName+"."+tableName);
        return jdbcTemplate.query(sb.toString(), mapper);
    }

    @Override
    public <T> boolean createTable(Class<T> klazz) throws DataAccessException {
        boolean result = false;
        String tableName = klazz.getSimpleName().toLowerCase();
        if (!isTableExists(tableName)) {
            this.jdbcTemplate.update(generateCreateTableSql(klazz));
            logger.info("Table " + tableName + " is created.");
            result = true;
        }
        return result;
    }

    @Override
    public void createDatabase(String dbName) throws DataAccessException {
        String query = "CREATE DATABASE IF NOT EXISTS " + dbName;
        this.jdbcTemplate.execute(query);
        useDatabase(dbName);
        logger.info("Database " + dbName + " is created.");
    }

    @Override
    public boolean isTableExists(String tableName) throws DataAccessException {
        boolean result = false;
        String query = "SHOW tables";
        List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(query);
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                if (row.containsValue(tableName)) {
                    result = true;
                    logger.info("Table " + tableName + " already exists.");
                }
            }
        }
        return result;
    }

    @Override
    public boolean isDatabaseExists(String dbName) throws DataAccessException {
        boolean result = false;
        String query = "SHOW databases";
        List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(query);
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                if (row.containsValue(dbName)) {
                    result = true;
                    logger.info("Database " + dbName + " already exists.");
                }
            }
        }
        return result;
    }

    @Override
    public void useDatabase(String dbName) throws DataAccessException {
        this.jdbcTemplate.execute("USE " + dbName);
        logger.info("Database " + dbName + " is used.");
    }

    @Override
    public void dropDatabase(String dbName) throws DataAccessException {
        this.jdbcTemplate.execute("DROP DATABASE " + dbName);
        logger.info("Database " + dbName + " is dropped.");
    }

    @Override
    public String getCurrentTime() throws DataAccessException {
        return this.jdbcTemplate.queryForObject("SELECT UTC_TIMESTAMP()", String.class);
    }

    @Override
    public void changePassword(String userName, String newPassword) {
        //TODO
    }

    private <T> String generateCreateTableSql(Class<T> klazz) {
        StringBuffer sb = new StringBuffer("");
        sb.append("CREATE TABLE `" + klazz.getSimpleName().toLowerCase() + "` (");
        for (String key : this.objectUtils.getProperty(klazz)) {
            if (key.equalsIgnoreCase("id")) {
                sb.append(" `id` int(11) NOT NULL AUTO_INCREMENT,");
            } else {
                sb.append("`" + key + "` varchar(255) DEFAULT '',");
            }
        }
        sb.append(" PRIMARY KEY (`id`)");
        sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        return sb.toString();
    }

    @Override
    public int rownum(String tableName) throws Exception {
        int i= jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName, Integer.class);
        return i;

    }

}
