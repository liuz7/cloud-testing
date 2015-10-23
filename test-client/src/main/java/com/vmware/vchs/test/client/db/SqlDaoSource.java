/*
 * ******************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ******************************************************
 */
package com.vmware.vchs.test.client.db;

import com.vmware.vchs.taskservice.common.util.ClassUtil;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;


/**
 * @author liuda
 */
//TODO change to hibernate
public class SqlDaoSource {

    static final Logger LOG = LoggerFactory.getLogger(
            ClassUtil.getSimpleName(SqlDaoSource.class));

    static public MsSqlDaoFactory createMsSqlDaoFactory(String url, String username, String password) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("net.sourceforge.jtds.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setValidationQuery("select 1");
        LOG.info("url is " + url);
        LOG.info("username is "+username);
        LOG.info("password is "+password);
        MsSqlDaoFactory daoFactory = new MsSqlDaoFactoryImpl(new JdbcTemplate(dataSource));
        LOG.info(url + " is created.");
        return daoFactory;
    }

    static public MySqlDaoFactory createMysqlDaoFactory(String url, String username, String password) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://" + url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setValidationQuery("select 1");
        LOG.info("url is " + url);
        LOG.info("username is "+username);
        LOG.info("password is "+password);
        MySqlDaoFactory daoFactory = new MySqlDaoFactoryImpl(new JdbcTemplate(dataSource));
        LOG.info(url + " is created.");
        return daoFactory;
    }

}
