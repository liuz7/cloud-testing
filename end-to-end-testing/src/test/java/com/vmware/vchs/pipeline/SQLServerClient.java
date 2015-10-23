package com.vmware.vchs.pipeline;

import com.vmware.vchs.common.utils.ObjectUtils;
import com.vmware.vchs.common.utils.Utils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by fanz on 8/19/15.
 */
public class SQLServerClient {

    private static final Logger logger = LoggerFactory.getLogger(SQLServerClient.class);

    private static JdbcTemplate jdbcTemplate;
    private ObjectUtils objectUtils;
    private String username;
    private String password;
    private String host;
    private String port = "1433";
    private String url;

    public static TableData tableData;
    public static String databaseName = "testDatabase";
    public static String tableName = "testTable";

    public SQLServerClient(String host, String username, String password, String port) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        logger.info("Start to connect SQL Server");
        logger.info("SQL Server connection {}:{}@{}:{}",
                username, password,
                host, port);

        String connString = "jdbc:jtds:sqlserver://" + host + ":" + port;
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("net.sourceforge.jtds.jdbc.Driver");
        dataSource.setUrl(connString);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setValidationQuery("select 1");
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.objectUtils = new ObjectUtils();
        logger.info(connString + " is created.");
    }


    public JdbcTemplate getTemplate() {
        return jdbcTemplate;
    }

    public boolean isConnected() {
        try {
            jdbcTemplate.queryForObject("select 1", Integer.class);
            return true;
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createLoginUser(String username, String password, String databaseName) throws DataAccessException {
        String query = "CREATE LOGIN " + username + " WITH PASSWORD='" + password + "', default_database=" + databaseName;
        jdbcTemplate.execute(query);
        logger.info(" Created login user : " + username + " with login permission.");
    }

    public void createDBUser(String username, String databaseName) throws DataAccessException {
        String query = "USE " + databaseName  + "; " + "CREATE USER " + username + " FOR LOGIN " + username ;
        logger.info(query);
        jdbcTemplate.execute(query);
        logger.info(" Created DB user : " + username + " with login database permission.");
    }

    public boolean grantDBUserPermission(String username, String rolePermission, String database) throws DataAccessException {
        logger.info("Start to grant user " + username + " with permission of " + rolePermission);
        // exec sp_addrolemember 'db_owner', 'dba'
        final String sql = "call " + database + ".sys.sp_addrolemember(?,?)";
        int res = jdbcTemplate.update(sql, rolePermission, username);
        logger.info("Granted user: " + username + " with role permission of " + rolePermission + " on database " + database);
        return (res == 0) ? true : false;
    }


    public boolean verifyDBUserPermission(String username, String rolePermission, String database) throws DataAccessException {
        logger.info("Start to verify DB user :" + username + "'s permission - " + rolePermission);
        List<String> res = jdbcTemplate.execute(
                new CallableStatementCreator() {
                    @Override
                    public CallableStatement createCallableStatement(Connection con) throws SQLException {
                        String sp = "{call " + database + ".sys." + "sp_helprolemember(？)}";
                        CallableStatement cs = con.prepareCall(sp);
                        cs.setString(1, rolePermission);
                        return cs;
                    }
                }, new CallableStatementCallback<List<String>>() {
                    public List<String> doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                        List<String> list = new ArrayList<>();
                        cs.execute();
                        ResultSet rs = cs.getResultSet();
                        while (rs.next()) {
                            list.add(rs.getString("MemberName"));
                            logger.info("find one user:" + rs.getString("MemberName") + " has DBRole:" + rolePermission);
                        }
                        rs.close();
                        return list;
                    }
                }

        );
        if (res.contains(username)) {
            return true;
        }
        return false;
    }

    public List listDBUserRole(String databaseName) throws DataAccessException {
        List resultList = jdbcTemplate.execute(
            new CallableStatementCreator() {
                @Override
                public CallableStatement createCallableStatement(Connection con) throws SQLException {
                    String sp = "{call " + databaseName  + ".sys.sp_helpuser}";
                    CallableStatement cs = con.prepareCall(sp);
                    return cs;
                }
            }, new CallableStatementCallback<List>() {
                @Override
                public List doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                    List resultList = new ArrayList();
                    cs.execute();
                    ResultSet rs = cs.getResultSet();
                    while (rs.next()) {
                        Map rowMap = new HashMap();
                        rowMap.put(rs.getString("UserName"), rs.getString("RoleName"));
                        resultList.add(rowMap);
                    }
                    rs.close();
                    return resultList;
                }
            });
        return resultList;
    }

    public String findDBUserRole(String databaseName, String username) throws DataAccessException {
        logger.info("Start to find role name for user:" + username + " on database:" + databaseName);
        Map<String, String> userRoleMap = new HashMap<>();
        userRoleMap = jdbcTemplate.execute(
                new CallableStatementCreator() {
                    @Override
                    public CallableStatement createCallableStatement(Connection con) throws SQLException {
                        String sp = "{call " + databaseName  + ".sys.sp_helpuser}";
                        CallableStatement cs = con.prepareCall(sp);
                        return cs;
                    }
                }, new CallableStatementCallback<Map<String, String>>() {
                    @Override
                    public Map<String, String> doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                        Map<String, String> map = new HashMap<>();
                        cs.execute();
                        ResultSet rs = cs.getResultSet();
                        while (rs.next()) {
                            map.put(rs.getString("UserName"), rs.getString("RoleName"));
                            logger.info("User name:" + rs.getString("UserName") + " has RoleName:" + rs.getString("RoleName"));
                        }
                        rs.close();
                        return map;
                    }
                });
        return userRoleMap.get(username);
    }

    public boolean isDBUserExist(String username, String databaseName) throws DataAccessException {
        List<String> resultList = jdbcTemplate.execute(
                new CallableStatementCreator() {
                    @Override
                    public CallableStatement createCallableStatement(Connection con) throws SQLException {
                        String sp = "{call " + databaseName  + ".sys.sp_helpuser}";
                        CallableStatement cs = con.prepareCall(sp);
                        return cs;
                    }
                }, new CallableStatementCallback<List<String>>() {
                    @Override
                    public List<String> doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                        List<String> resultList = new ArrayList<>();
                        cs.execute();
                        ResultSet rs = cs.getResultSet();
                        while (rs.next()) {
                            resultList.add(rs.getString("UserName"));
                        }
                        rs.close();
                        return resultList;
                    }
                });
        if (resultList.contains(username)) {
            logger.info("DB user:" + username +" exists! ");
            return true;
        }
        return false;
    }

    public boolean isLoginUserExist(String username) throws DataAccessException {
        String sql = "SELECT * FROM master.sys.syslogins";
        List<String> resultList = new ArrayList<>();
                resultList = jdbcTemplate.query(sql, new RowMapper<String>() {
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("name");
            }
        });
        if (resultList.contains(username)) {
            return true;
        }
        return false;
    }

    /**
     *  GRANT "server permission" TO "userName"
     *
     * @param username
     * @param rolePermission: CREATE ANY DATABASE, ALTER ANY LOGIN, VIEW ANY DATABASE
     *
     * @throws DataAccessException
     */
    public void grantUserServerPermission(String username, String rolePermission) throws DataAccessException {
        String sql = "GRANT " + rolePermission + " TO" + username;
        jdbcTemplate.execute(sql);
    }


    /**
     * @param permission :  disable, enable
     */
    public void alterUserLogin(String username, String permission) throws DataAccessException {
        String query = "alter login " + username + " " + permission;
        jdbcTemplate.execute(query);
        logger.info("Changed user : " + username + " login permission to " + permission + " login");
    }

    public void alterUserPassword(String username, String password) throws DataAccessException {
        String query = "alter login " + username + " with password='" + password + "'";
        jdbcTemplate.execute(query);
        logger.info("Changed password of user:" + username + " to " + password);
    }

    /**
     *   T-SQL  check version :   seletc @@version
     */
    public String getSQLEngineVersion() throws DataAccessException {
        String sql = "SELECT @@version AS version";
        final List<String> resultList = new ArrayList<>();
        jdbcTemplate.query(sql,new RowCallbackHandler(){
            public void processRow(ResultSet rs) throws SQLException{
                String sql_version = rs.getString("version");
                resultList.add(sql_version);
            }
        });
        return resultList.get(0);
    }

    public void insertData(TableData instance, String database, String table) throws DataAccessException {
        final String sql = "INSERT INTO " + database + ".dbo." + table + " (id, name, age, address) VALUES (?, ?, ?, ?)";
        Object[] param = { instance.getId(),
                           instance.getName(),
                           instance.getAge(),
                           instance.getAddress()};
        jdbcTemplate.update(sql, param);
        logger.info("Insert data into " + tableName + " inserted at " + getCurrentTime());
    }

    public void initialTableData(String databaseName, String tableName) throws DataAccessException {
        //String query = "INSERT INTO " + databaseName + ".dbo." + tableName +" ("
        StringBuffer sb = new StringBuffer("");
        // "INSERT INTO tableName VALUE (  ";
        sb.append(String.format("INSERT INTO %s.dbo.%s (", databaseName, tableName));
        sb.append("id, name, age, address) VALUES (");
        sb.append("?, ?, ?, ?) ");
        jdbcTemplate.update(sb.toString(),Integer.valueOf(1), "Frank", Integer.valueOf(38), "Oracle");
        logger.info("Inserted initial data record into table:" + tableName);
    }

    public int getRowNum(String databaseName, String tableName) throws DataAccessException {
        String query = String.format("SELECT count(*) FROM %s.information_schema.tables where table_name='%s';",
                databaseName, tableName);
        int count = jdbcTemplate.queryForObject(query, Integer.class);
        return count;
    }

    public TableData findData(String database, String table) throws DataAccessException {
        useDatabase(database);
        TableData result = (TableData) jdbcTemplate.queryForObject("SELECT * FROM ?.dbo.?",
                new Object[]{database, table},
                new RowMapper() {
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        TableData resultData = new TableData();
                        resultData.setId(rs.getInt("id"));
                        resultData.setName(rs.getString("name"));
                        resultData.setAge(rs.getInt("age"));
                        resultData.setAddress(rs.getString("address"));
                        return resultData;
                    }
                });
        return result;
    }

    public void createTable(SQLServerClient connection, String database, String table) throws DataAccessException {
        if (!connection.isTableExists(database, table)) {
            StringBuffer sb = new StringBuffer();
            sb.append(String.format("CREATE TABLE %s.dbo.%s (", database, table));
            sb.append(" id int NOT NULL,");
            sb.append(" name varchar(50),");
            sb.append(" age int ,");
            sb.append(" address varchar(60),");
            sb.append(")");
            connection.getTemplate().execute(sb.toString());
            logger.info("Created table : " + table);
        } else {
            logger.info("Table " + table +" is existed already! ");
        }
    }

    public void createDatabase(String databaseName) throws DataAccessException {
        String query = "IF NOT EXISTS ( SELECT [name] FROM sys.databases WHERE [name] = '" + databaseName + "' ) CREATE DATABASE " + databaseName;
        jdbcTemplate.execute(query);
        logger.info("Database " + databaseName + " is created.");
    }

    public void useDatabase(String databaseName) throws DataAccessException {
        jdbcTemplate.execute("USE " + databaseName);
        logger.info("Database " + databaseName + " is used.");
    }

    public void dropDatabase(String databaseName) throws DataAccessException {
        this.jdbcTemplate.execute("DROP DATABASE " + databaseName);
        logger.info("Database " + databaseName + " is dropped.");
    }

    public void dropTable(String database, String table) throws DataAccessException {
        if (isTableExists(database, table)) {
            this.jdbcTemplate.execute(String.format("DROP TABLE %s.dbo.%s;", database, table));
            logger.info("Drop Table " + database + "." + table + ".");
        }
    }


    public boolean isTableExists(String databaseName, String tableName) throws DataAccessException {
        String query = String.format("SELECT count(*) FROM %s.information_schema.tables where table_name='%s';",
                databaseName, tableName);
        int count = jdbcTemplate.queryForObject(query, Integer.class);
        return count > 0;
    }

    public void updateTable(String database, String table) throws DataAccessException {
        StringBuffer sb = new StringBuffer("");
        sb.append(String.format("update %s.dbo.%s ", database, table));
        sb.append("set address = ? where id = ? ");
        jdbcTemplate.update(sb.toString(),
                new Object[] {"facebook", new Integer(1)});
        logger.info("update data into " + tableName + " updated at " + getCurrentTime());
    }

    public boolean isDatabaseExists(String database) throws DataAccessException {
        String query = String.format("SELECT count(*) FROM sys.databases where name='%s';",
                database);
        int count = jdbcTemplate.queryForObject(query, Integer.class);
        return count > 0;
    }


    public int getCount(String database, String tableName) throws DataAccessException {
        return jdbcTemplate.queryForInt(String.format("select count(*) from %s.dbo.%s;", database, tableName));
    }

    public List<Integer> getLastRows(String database, String table, int count, String time) {
        return jdbcTemplate.queryForList(
                String.format("select top %d id from %s.dbo.%s where timestamp<'%s' order by timestamp desc;",
                        count, database, table, time), Integer.class);
    }


    public void truncateTable(String databaseName, String tableName) throws Exception {
        jdbcTemplate.execute("DELETE FROM " + tableName);
    }


    public boolean testConnection(String testQuery) {
        try {
            if (this.jdbcTemplate != null) {
                List<Map<String, Object>> rows = this.jdbcTemplate.queryForList(testQuery);
                if (rows != null) {
                    logger.info("MS SQL Server Connected.");
                    return true;
                }
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getCurrentTime() throws DataAccessException {
        return jdbcTemplate.queryForObject("SELECT GETUTCDATE()", String.class);
    }


    public void changePassword(String userName, String newPassword) throws DataAccessException {
        jdbcTemplate.execute("ALTER LOGIN " + userName + " WITH PASSWORD = '" + newPassword + "'");
        logger.info("Password changed for user:" + userName);
    }




    public static class TableData implements Serializable {
        private int id;
        private String name;
        private int age;
        private String address;

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

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof TableData) {
                TableData tableData = (TableData)o;
            }
            return   ((this.getId() == tableData.getId())
                    && (this.getName().equalsIgnoreCase(tableData.getName()))
                    && (this.getAge() == tableData.getAge())
                    && (this.getAddress().equalsIgnoreCase(tableData.getAddress())
            )) ? true: false;
        }

        @Override
        public String toString() {
            return "TableData{" +
                    ", id=" + id +
                    ", name='" + name + '\'' +
                    ", age=" + age +
                    ", address='" + address + '\'' +
                    '}';
        }

        public static class Builder {

            TableData tableData = new TableData();

            public Builder setId(int id) {
                tableData.setId(id);
                return this;
            }

            public Builder setName(String name) {
                tableData.setName(name);
                return this;
            }

            public Builder setAge(int age) {
                tableData.setAge(age);
                return this;
            }

            public Builder setAddress(String address) {
                tableData.setAddress(address);
                return this;
            }

            public TableData build() {
                return tableData;
            }
        }

        public static Builder builder() {
            return new Builder();
        }

    }



    static class TableDataComparator implements Comparator<TableData> {
        @Override
        public int compare(TableData o1, TableData o2) {
            // TODO
            return 0;
        }
    }


    public String toString() {
        String url = null;
        try {
            url = jdbcTemplate.getDataSource().getConnection().getMetaData().getURL();
        } catch (SQLException e) {
            logger.info(Utils.getStackTrace(e));
        }
        return url;
    }

    public TableData provisionData(String database, String table) throws Exception {
        InputStream fis = this.getClass().getClassLoader().getResourceAsStream("TableData.txt");
        //BufferedReader br = Files.newBufferedReader(Paths.get("TableData.txt"));
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        TableData tableData = new TableData();

        for (String line = br.readLine(); line != null; line = br.readLine()) {
            String[] str = line.split(",");
            tableData.setId(Integer.parseInt(str[0]));
            tableData.setName(str[1]);
            tableData.setAge(Integer.parseInt(str[2]));
            tableData.setAddress(str[3]);

            StringBuffer sb = new StringBuffer("");
            sb.append(String.format("INSERT INTO %s.dbo.%s (", database, table));
            sb.append("id, name, age, address) VALUES (");
            sb.append("?, ?, ?, ?) ");
            jdbcTemplate.update(sb.toString(),
                    new Object[] {
                            tableData.getId(),
                            tableData.getName(),
                            tableData.getAge(),
                            tableData.getAddress()});
            logger.info("Insert data into " + tableName + " inserted at " + getCurrentTime());
        }
        br.close();
        return tableData;
    }

    public TableData generateSimpleData() throws Exception {
        TableData tableData = TableData.builder().setId(10).setName("Tommy").setAge(28).setAddress("google").build();
        return tableData;
    }

    public TableData getTableData() {
        return tableData;
    }

    public TableData setInitalTableData() {
        SQLServerClient.tableData = new TableData();
        //tableData.setTableName(tableName);
        tableData.setId(1);
        tableData.setName("Jeffrey Test");
        tableData.setAge(25);
        tableData.setAddress("VMware Inc");
        return tableData;
    }

}
