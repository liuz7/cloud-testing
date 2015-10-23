/*
 * ******************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ******************************************************
 */
package com.vmware.vchs.test.client.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;


/**
 * @author liuda
 */
public class MySysDaoImpl implements SysDao {

    private static final Logger LOG = LoggerFactory.getLogger(MySysDaoImpl.class);

    private JdbcTemplate jdbcTemplate;



    public MySysDaoImpl(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createDatabase(String dbName) throws DataAccessException {
        String query = "IF NOT EXISTS ( SELECT [name] FROM sys.databases WHERE [name] = '" + dbName + "' ) CREATE DATABASE " + dbName;
        this.jdbcTemplate.execute(query);
        LOG.info("Database " + dbName + " is created.");
    }

    @Override
    public void dropDatabase(String dbName) throws DataAccessException {
        this.jdbcTemplate.execute("DROP DATABASE " + dbName);
        LOG.info("Database " + dbName + " is dropped.");
    }

    @Override
    public boolean testConnection(String testQuery) throws DataAccessException {
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
    public boolean isDatabaseExists(String dbName) throws DataAccessException {
        boolean result = false;
        String query = "SHOW databases";
        List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(query);
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                if (row.containsValue(dbName)) {
                    result = true;
                    LOG.info("Database " + dbName + " already exists.");
                }
            }
        }
        return result;
    }
    @Override
    public String getCurrentTime() throws DataAccessException {
        return this.jdbcTemplate.queryForObject("SELECT UTC_TIMESTAMP()", String.class);
    }

    @Override
    public void changePassword(String userName, String newPassword) {
        //TODO
    }

    @Override
    public boolean isTableExists(String tableName) throws DataAccessException {
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
    
}
