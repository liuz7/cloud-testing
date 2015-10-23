/*
 * ******************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ******************************************************
 */
package com.vmware.vchs.test.client.db;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author liuda
 */
public class MySqlDaoFactoryImpl implements MySqlDaoFactory {

    private static final String SYSDAO = "SysDao";

    private JdbcTemplate jdbcTemplate;

    private ConcurrentMap daoCache = new ConcurrentHashMap();

    public MySqlDaoFactoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SysDao createSysDao() {

        if (!this.daoCache.containsKey(this.SYSDAO)) {
            SysDao dao = new MySysDaoImpl(this.jdbcTemplate);
            this.daoCache.putIfAbsent(this.SYSDAO, dao);
            return dao;
        }
        return (SysDao) this.daoCache.get(this.SYSDAO);
    }


}
