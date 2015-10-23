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
public class MsSqlDaoFactoryImpl implements MsSqlDaoFactory {

    private static final String EMPLOYEEDAO = "EmployeeDao";

    private static final String SYSDAO = "SysDao";

    private static final String MsSqlDataLoader = "MsSqlDataLoader";


    private JdbcTemplate jdbcTemplate;

    private ConcurrentMap daoCache = new ConcurrentHashMap();

    public MsSqlDaoFactoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public EmployeeDao createEmployeeDao() {

        if (!this.daoCache.containsKey(this.EMPLOYEEDAO)) {
            EmployeeDao dao = new EmployeeDaoImpl(this.jdbcTemplate, createSysDao());
            this.daoCache.putIfAbsent(this.EMPLOYEEDAO, dao);
            return dao;
        }
        return (EmployeeDao) this.daoCache.get(this.EMPLOYEEDAO);
    }

    @Override
    public SysDao createSysDao() {

        if (!this.daoCache.containsKey(this.SYSDAO)) {
            SysDao dao = new MsSysDaoImpl(this.jdbcTemplate);
            this.daoCache.putIfAbsent(this.SYSDAO, dao);
            return dao;
        }
        return (SysDao) this.daoCache.get(this.SYSDAO);
    }

    @Override
    public MsSqlDataLoader createMsSqlDataLoader() {

        if (!this.daoCache.containsKey(this.MsSqlDataLoader)) {
            MsSqlDataLoader msSqlDataLoader = new MsSqlDataLoaderImpl(this.jdbcTemplate);
            this.daoCache.putIfAbsent(this.MsSqlDataLoader, msSqlDataLoader);
            return msSqlDataLoader;
        }
        return (MsSqlDataLoader) this.daoCache.get(this.MsSqlDataLoader);
    }

}
