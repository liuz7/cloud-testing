/*
 * ******************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ******************************************************
 */
package com.vmware.vchs.test.client.db;

import com.vmware.vchs.test.client.db.basic.BasicDao;
import com.vmware.vchs.test.client.db.basic.BasicDaoImpl;
import com.vmware.vchs.test.client.db.model.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.vmware.vchs.test.client.db.SQLStatements.SQL_SELECT_ALL_EMPLOYEE;


/**
 * @author @author liuda
 */
public class EmployeeDaoImpl implements EmployeeDao {

    BasicDao basicDao;

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeDaoImpl.class);

    private static final String TABLENAME = "employee";

    private JdbcTemplate jdbcTemplate;



    public EmployeeDaoImpl(JdbcTemplate jdbcTemplate, SysDao sysdao)
    {
        this.basicDao = new BasicDaoImpl(jdbcTemplate, sysdao);
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public boolean testConnection(String testQuery) throws Exception {
        return this.basicDao.testConnection(testQuery);
    }

    @Override
    public void insert(Employee instance) throws Exception {
        this.basicDao.insert(instance);
    }

    @Override
    public List<Map<String, Object>> findAllRows() throws Exception {
        return this.basicDao.findAllRows(Employee.class);
    }

    @Override
    public List<Employee> findClassRows() throws Exception {
        return this.jdbcTemplate.query(SQL_SELECT_ALL_EMPLOYEE, new EmployeeRowMapper());
    }

    private class EmployeeRowMapper implements RowMapper<Employee>
    {
        public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
            Employee event = new Employee();
            event.setId(Integer.valueOf(rs.getString("id")));
            event.setName(rs.getString("name"));
            event.setRole(rs.getString("role"));
            return event;
        }
    }

    @Override
    public boolean createTable() throws Exception {
        return this.basicDao.createTable(Employee.class);
    }

    @Override
    public void truncateTable() throws Exception {
        this.basicDao.truncateTable(this.TABLENAME);
    }

    @Override
    public int rownum() throws Exception {
        return 0;
    }


    @Override
    public void useDatabase(String dbName) throws Exception{
        this.basicDao.useDatabase(dbName);
    }

}
