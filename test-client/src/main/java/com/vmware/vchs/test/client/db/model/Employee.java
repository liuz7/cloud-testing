package com.vmware.vchs.test.client.db.model;

import com.google.common.base.MoreObjects;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The model class used for testing the DB related tests.
 */
public class Employee {

    private int id;
    private String name;
    private String role;

    public Employee(int id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public Employee(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.name=rs.getString("name");
        this.role=rs.getString("role");
    }

    public Employee(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("role", role)
                .toString();
    }
}
