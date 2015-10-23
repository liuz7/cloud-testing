package com.vmware.vchs.test.client.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

import java.io.File;
import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by georgeliu on 15/8/12.
 */
public class MsSqlDataLoaderImpl implements MsSqlDataLoader {


    private static final Logger LOG = LoggerFactory.getLogger(MsSqlDataLoaderImpl.class);

    //private static final String TABLE_NAME = "largeData";

    private JdbcTemplate jdbcTemplate;

    public MsSqlDataLoaderImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean testConnection(String testQuery) throws Exception {
        boolean result = false;
        if (this.jdbcTemplate != null) {
            List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(testQuery);
            if (rows != null) {
                result = true;
                LOG.info("Sql Server Connected.");
            }
        }
        return result;
    }

    @Override
    public boolean insert(String tableName, String filePath) throws Exception {
        int len;
        String query;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            len = (int) file.length();
            query = ("insert into " + tableName + " (data) VALUES(?)");
            return jdbcTemplate.execute(query, new PreparedStatementCallback<Boolean>() {
                @Override
                public Boolean doInPreparedStatement(PreparedStatement ps)
                        throws SQLException, DataAccessException {
                    //ps.setInt(1, id);
                    //ps.setTimestamp(2, new Timestamp(new Date().getTime()));
                    ps.setBinaryStream(1, fis, len);
                    boolean result = ps.execute();
                    LOG.info("File name: " + filePath + " is ingested.");
                    return result;
                }
            });
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public boolean createTable(String tableName) throws Exception {
        boolean result = false;
        if (!isTableExists(tableName)) {
            this.jdbcTemplate.update(generateCreateTableSql(tableName));
            LOG.info("Table " + tableName + " is created.");
            result = true;
        }
        return result;
    }

    @Override
    public long getDBSize() throws Exception {
        String query = "select size FROM sys.database_files(NOLOCK) where type=0";
        long result = this.jdbcTemplate.queryForLong(query);
        LOG.info("The current DB storage size is: " + result);
        return result;
    }

    @Override
    public void useDatabase(String dbName) throws Exception {
        this.jdbcTemplate.execute("USE " + dbName);
        LOG.info("Database " + dbName + " is used.");
    }

    private boolean isTableExists(String tableName) throws DataAccessException {
        boolean result = false;
        String query = "SELECT * FROM information_schema.tables;";
        List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(query);
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                if (row.containsValue(tableName)) {
                    result = true;
                    LOG.info("Table " + tableName + " already exists.");
                }
            }
        }
        return result;
    }

    private String generateCreateTableSql(String tableName) {
        StringBuffer sb = new StringBuffer("");
        sb.append("CREATE TABLE " + tableName + " (");
        sb.append(" id int IDENTITY(1,1) PRIMARY KEY NOT NULL,");
        sb.append(" timestamp datetime NOT NULL DEFAULT (getdate()),");
        sb.append(" data varbinary(MAX),");
        sb.append(" )");
        return sb.toString();
    }
}
