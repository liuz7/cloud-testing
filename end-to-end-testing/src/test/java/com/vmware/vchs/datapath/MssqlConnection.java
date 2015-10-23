package com.vmware.vchs.datapath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.util.List;

/**
 * Created by sjun on 8/12/15.
 */
public class MssqlConnection {
    protected static final Logger logger = LoggerFactory.getLogger(MssqlConnection.class);

    private JdbcTemplate template;
    private String host;
    private String port;
    private String username;
    private String password;
    private SimpleDriverDataSource dataSource;
    public MssqlConnection(String host, String port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        logger.info("MssqlConnection {}:{}@{}:{}",
                username, password,
                host, port);

        String connectionString = "jdbc:jtds:sqlserver://" + host + ":" + port;
        dataSource = new SimpleDriverDataSource(new net.sourceforge.jtds.jdbc.Driver(),
                connectionString, username, password);
        this.template = new JdbcTemplate(dataSource);
    }

    public void close(){
        // do nothing here, as we may change datasource implement, which need to close explicitly
    }

    public JdbcTemplate getTemplate() {
        return template;
    }

    public void createDatabase(String database) throws DataAccessException {
        String query = "IF NOT EXISTS ( SELECT [name] FROM sys.databases WHERE [name] = '" + database + "' ) CREATE DATABASE " + database;
        template.execute(query);
        logger.info("Create Database " + database + ".");
    }

    public void dropDatabase(String database) throws DataAccessException {
        template.execute("DROP DATABASE " + database);
        logger.info("Drop Database " + database + ".");
    }

    public void dropTable(String database, String table) throws DataAccessException {
        if (isTableExists(database, table)) {
            template.execute(String.format("DROP TABLE %s.dbo.%s;", database, table));
            logger.info("Drop Table " + database + "." + table + ".");
        }
    }

    public void useDatabase(String database) throws DataAccessException {
        template.execute("USE " + database);
        logger.info("Use Database " + database + ".");
    }

    public boolean isConnected() {
        try {
            template.queryForObject("select 1", Integer.class);
            return true;
        } catch (DataAccessException e) {
        }
        return false;
    }

    public boolean isTableExists(String database, String tableName) throws DataAccessException {
        String query = String.format("SELECT count(*) FROM %s.information_schema.tables where table_name='%s';",
                database, tableName);
        int count = template.queryForObject(query, Integer.class);
        return count > 0;
    }

    public boolean isDatabaseExists(String database) throws DataAccessException {
        String query = String.format("SELECT count(*) FROM sys.databases where name='%s';",
                database);
        int count = template.queryForObject(query, Integer.class);
        return count > 0;
    }

    public String getCurrentTime() throws DataAccessException {
        return template.queryForObject("SELECT GETUTCDATE()", String.class);
    }

    @Override
    public String toString() {
        return String.format("MssqlConnection %s:%s@%s:%s",
                username, password,
                host, port);
    }






    public List<Integer> getLastRows(String database, String table, int count, String time) {
        return getTemplate().queryForList(
                String.format("select top %d id from %s.dbo.%s where timestamp<'%s' order by timestamp desc;",
                        count, database, table, time), Integer.class);
    }

//    public void verifyDataLists(String snapshotTime,Map<String, List<Integer>> data) {
//        for(Map.Entry<String, List<Integer>> entry : data.entrySet()) {
//            String[] parts = entry.getKey().split(":");
//            assertThat(parts.length).isEqualTo(2);
//            String database = parts[0];
//            String table = parts[1];
//            logger.info("verify {}.dbo.{}", database, table);
//            assertThat(isDatabaseExists(database)).isTrue();
//            assertThat(isTableExists(database, table)).isTrue();
//
//            // verify there are no more items in snapshot
//            int diff = getTemplate().queryForObject(String.format("select count(*) from %s.dbo.%s where timestamp>'%s'", database, table, snapshotTime), Integer.class);
//            logger.info("Diff " + diff);
//            //assertThat(diff).isLessThanOrEqualTo(5);
//
//            // verify there are no items missed in snapshot
//            List<Integer> ids = getLastRows(database, table, 10, snapshotTime);
//            logger.info("Expected: {}", entry.getValue().toString());
//            logger.info("Restored: {}", ids.toString());
//
//            assertThat(entry.getValue().size()).isEqualTo(ids.size());
//            if (! entry.getValue().isEmpty()) {
//                assertThat(Collections.max(entry.getValue())).isEqualTo(Collections.max(ids));
//                assertThat(Collections.min(entry.getValue())).isEqualTo(Collections.min(ids));
//            }
//        }
//    }
//
//
//    public void loadDataBySize(String database, String table, int totalSizeInKB) throws Exception {
//        if (totalSizeInKB <= 0) {
//            return;
//        }
//        if (totalSizeInKB <= 10240) {
//            directLoadDataBySize(database, table, totalSizeInKB);
//            return;
//        }
//
//        String tmpTable = "temp_data";
//        createDatabase(database);
//        DataPathTestModel.createTable(this, database, tmpTable);
//        DataPathTestModel.createTable(this, database, table);
//
//        logger.info("Write 10M data in tmp table.");
//        String text = DataGenerator.getData(1024*1024*10);
//        DataPathTestModel model = new DataPathTestModel(text);
//        model.save(this, database, tmpTable);
//        logger.info("Write completed in tmp table.");
//        String sql = String.format("INSERT INTO %s.dbo.%s (data) SELECT data FROM %s.dbo.%s",
//                database, table, database, tmpTable);
//        int count = (totalSizeInKB-1)/10240 + 1;
//        logger.info("Start insert data.");
//        for (int i=0;i<count;i++) {
//           getTemplate().execute(sql);
//            if(i % 10 == 9) {
//                logger.info("Inserted 100M to table.");
//            }
//        }
//        logger.info("Completed insert data.");
//    }
//
//    private void directLoadDataBySize(String database, String table, int size) throws Exception {
//        logger.info("Start write {} kb to {}.dbo.{}", size, database, table);
//        createDatabase(database);
//        DataPathTestModel.createTable(this, database, table);
//        String text = DataGenerator.getData(size * 1024);
//        DataPathTestModel model = new DataPathTestModel(text);
//        model.save(this, database, table);
//        logger.info("Write completed.");
//    }
}
