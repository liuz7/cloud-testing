package com.vmware.vchs.test.client.db.basic;

import com.vmware.vchs.common.utils.ObjectUtils;
import com.vmware.vchs.common.utils.Utils;
import com.vmware.vchs.test.client.db.MsSysDaoImpl;
import com.vmware.vchs.test.client.db.SysDao;
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
public class BasicDaoImpl implements BasicDao {

    private static final Logger logger = LoggerFactory.getLogger(BasicDaoImpl.class);
    private JdbcTemplate jdbcTemplate;
    private SysDao sysDao;

    public BasicDaoImpl(JdbcTemplate jdbcTemplate, SysDao sysDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.sysDao = sysDao;
        logger.info("jdbcTemplate is created.");
    }

    @Override
    public void useDatabase(String dbName) throws DataAccessException {
        this.jdbcTemplate.execute("USE " + dbName);
        logger.info("Database " + dbName + " is used.");
    }


    @Override
    public boolean testConnection(String testQuery) throws DataAccessException {
        boolean result = false;
        if (this.jdbcTemplate != null) {
            List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(testQuery);
            if (rows != null) {
                result = true;
                logger.info("Sql Server Connected.");
            }
        }
        return result;
    }

    @Override
    public <T> void insert(T instance) throws DataAccessException {
        String tableName = instance.getClass().getSimpleName().toLowerCase();
        StringBuffer sb = new StringBuffer("");
        sb.append("INSERT INTO " + tableName + " (");
        List<String> properties = ObjectUtils.getProperty(instance.getClass());
        for (String key : properties) {
            if (key.equalsIgnoreCase(properties.get(properties.size() - 1))) {
                sb.append(key);
            } else {
                sb.append(key + ",");
            }
        }
        sb.append(") VALUES (");
        for (String key : ObjectUtils.getProperty(instance.getClass())) {
            if (key.equalsIgnoreCase(properties.get(properties.size() - 1))) {
                sb.append("?");
            } else {
                sb.append("?,");
            }
        }
        sb.append(")");
        this.jdbcTemplate.update(sb.toString(), (Object[]) ObjectUtils.getPropertyValue(instance).toArray());
        logger.info("New " + tableName + " inserted at " + this.sysDao.getCurrentTime());
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
    public <T> List<T> findClassRows(String dbName, Class<T> klazz, RowMapper mapper) throws Exception {
        //TODO
        return null;
    }

    @Override
    public <T> boolean createTable(Class<T> klazz) throws DataAccessException {
        boolean result = false;
        String tableName = klazz.getSimpleName().toLowerCase();
        if (!this.sysDao.isTableExists(tableName)) {
            this.jdbcTemplate.update(generateCreateTableSql(klazz));
            logger.info("Table " + tableName + " is created.");
            result = true;
        }
        return result;
    }

    private <T> String generateCreateTableSql(Class<T> klazz) {
        StringBuffer sb = new StringBuffer("");
        sb.append("CREATE TABLE " + klazz.getSimpleName().toLowerCase() + " (");
        for (String key : ObjectUtils.getProperty(klazz)) {
            if (key.equalsIgnoreCase("id")) {
                sb.append(" id int NOT NULL,");
            } else {
                sb.append("" + key + " varchar(255) DEFAULT '',");
            }
        }
        sb.append(" PRIMARY KEY (id))");
        return sb.toString();
    }

    @Override
    public String toString() {
        String url = null;
        try {
            url = jdbcTemplate.getDataSource().getConnection().getMetaData().getURL();
        } catch (SQLException e) {
            logger.info(Utils.getStackTrace(e));
        }
        return url;
    }

    @Override
    public int rownum(String tableName) throws Exception {
        //TODO
        return 0;
    }
}
