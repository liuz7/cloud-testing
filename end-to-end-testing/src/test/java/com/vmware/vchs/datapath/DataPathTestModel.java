package com.vmware.vchs.datapath;

import com.google.common.base.MoreObjects;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by sjun on 8/12/15.
 */
public class DataPathTestModel {
    public static void createTable(MssqlConnection connection, String database, String table) throws DataAccessException {
        if (!connection.isTableExists(database, table)) {
            StringBuffer sb = new StringBuffer();
            sb.append(String.format("CREATE TABLE %s.dbo.%s (", database, table));
            sb.append(" id int IDENTITY(1,1) PRIMARY KEY,");
            sb.append(" timestamp datetime NOT NULL DEFAULT (getdate()),");
            sb.append(" data text,");
            sb.append(")");
            connection.getTemplate().execute(sb.toString());
        }
    }

    public void save(MssqlConnection connection, String database, String table) {
        if (id != null) {
            connection.getTemplate().execute(String.format("UPDATE %s.dbo.%s SET data='%s' WHERE id=%d",
                    database, table, data, id));
        } else {
            String insertSql = String.format("INSERT %s.dbo.%s (data) VALUES ('%s')", database, table, data);
            connection.getTemplate().execute(insertSql);
        }
    }

    public static List<DataPathTestModel> query(MssqlConnection connection, String sql) {
        return connection.getTemplate().query(sql, new RowMapper<DataPathTestModel>() {
            @Override
            public DataPathTestModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new DataPathTestModel(rs);
            }
        });
    }


    private Integer id;
    private Timestamp timestamp;
    private String data;
    public DataPathTestModel(String data) {
        this.data = data;
        this.id = null;
        this.timestamp = null;
    }

    public DataPathTestModel(ResultSet rs) throws SQLException {
        this.id = rs.getInt("id");
        this.timestamp = rs.getTimestamp("timestamp");
        this.data = rs.getString("data");
    }

    public Integer getId() {
        return id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("timestamp", timestamp)
                .add("sizeOfData", data.length())
                .toString();
    }
}
