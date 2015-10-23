/*
 * ******************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ******************************************************
 */
package com.vmware.vchs.test.client.db;

import com.vmware.vchs.test.client.db.basic.BasicDao;
import com.vmware.vchs.test.client.db.model.Employee;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Map;


/**
 * @author liuda
 */
public interface EmployeeDao
{

    boolean testConnection(String testQuery) throws Exception;

    void insert(Employee instance) throws Exception;

    List<Map<String, Object>> findAllRows() throws Exception;

    List<Employee> findClassRows() throws Exception;

    boolean createTable() throws Exception;

    void truncateTable() throws Exception;

    int rownum() throws Exception;


    void useDatabase(String dbName) throws Exception;

}
