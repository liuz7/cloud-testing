package com.vmware.vchs.base;

import com.vmware.vchs.datapath.DBWriter;
import com.vmware.vchs.datapath.DataGenerator;
import com.vmware.vchs.datapath.DataPathTestModel;
import com.vmware.vchs.launcher.TestHelper;
import com.vmware.vchs.model.portal.common.AsyncResponse;
import com.vmware.vchs.model.portal.instance.CreateInstanceRequest;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.model.portal.snapshot.GetSnapshotResponse;
import com.vmware.vchs.test.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by liuda on 9/3/15.
 */
public class DataPathInstance extends DbaasInstance {
    public Map<String, List<Integer>> data;
    protected List<DBWriter> dbWriterList = new ArrayList<>();
    protected static final Logger logger = LoggerFactory.getLogger(DataPathInstance.class);
    protected static final Configuration configuration = TestHelper.getConfiguration();
    public static final String preDbPrefix = "PREDB";
    public static final String preTablePrefix = "PRETable";
    protected static final int preBbCount = 2;
    protected static final int preTableCount = 2;

    protected static String username = "datatest";
    protected static String password = "datatest";

    public DataPathInstance(AsyncResponse instanceResponse, CreateInstanceRequest createInstanceRequest) throws Exception {
        super(instanceResponse, createInstanceRequest);
    }

    public DataPathInstance(ResponseEntity<AsyncResponse> instanceResponse, CreateInstanceRequest createInstanceRequest) throws Exception {
        super(instanceResponse, createInstanceRequest);
    }

    public DataPathInstance(GetInstanceResponse instanceResponse, String password, boolean prepardData) throws Exception {
        super(instanceResponse, password, prepardData);
    }


    public DataPathInstance(GetInstanceResponse instanceResponse, CreateInstanceRequest createInstanceRequest, String password, boolean prepardData) throws Exception {
        super(instanceResponse, createInstanceRequest, password, prepardData);
    }

    public DataPathInstance(GetInstanceResponse instanceResponse, CreateInstanceRequest createInstanceRequest) throws Exception {
        super(instanceResponse, createInstanceRequest);
    }

    public GetSnapshotResponse waitAutoSnapshot() throws Exception {
        GetSnapshotResponse snapshotResponse=super.waitAutoSnapshot();
        this.data=getData(snapshotResponse.getSnapshotTime());
        return snapshotResponse;
    }

    public GetSnapshotResponse createSnapshotWithRetry() throws Exception {
        GetSnapshotResponse snapshotResponse=super.createSnapshotWithRetry();
        this.data=getData(snapshotResponse.getSnapshotTime());
        return snapshotResponse;
    }

    public List<DBWriter> preparePreDb() throws Exception {
        List<DBWriter> dbWriters = new ArrayList<>();
        for (int dbIndex = 1; dbIndex <= preBbCount; dbIndex++) {
            String database = String.format("%s%d", preDbPrefix, dbIndex);
            for (int tableIndex = 1; tableIndex <= preTableCount; tableIndex++) {
                String table = String.format("%s%d", preTablePrefix, tableIndex);
                dbWriters.add(createDatabaseAndTable(database, table));
            }
        }
        loadDataBySize(preDbPrefix + 1, preTablePrefix + 1, configuration.getDataPath().getPreLoadSize());
        loadDataBySize(preDbPrefix + 2, preTablePrefix + 2, configuration.getDataPath().getPreLoadSize());
        return dbWriters;
    }

    public void deleteTable(String database, String table) {
        List<DBWriter> toBeRemoved = new ArrayList<>();
        for (DBWriter dbWriter : dbWriterList) {
            if (dbWriter.getDatabase().equals(database) && dbWriter.getTable().equals(table)) {
                dbWriter.stop();
                toBeRemoved.add(dbWriter);
            }
        }
        dbWriterList.removeAll(toBeRemoved);
        mssqlConnection.dropTable(database, table);
    }

    public void dropDatabase(String database) {
        List<DBWriter> toBeRemoved = new ArrayList<>();
        for (DBWriter dbWriter: dbWriterList) {
            if(dbWriter.getDatabase().equals(database)) {
                dbWriter.stop();
                toBeRemoved.add(dbWriter);
            }
        }
        dbWriterList.removeAll(toBeRemoved);
        mssqlConnection.dropDatabase(database);
    }

    public DBWriter createDatabaseAndTable(String database, String table) {
        DBWriter dbWriter = new DBWriter(mssqlConnection, database, table);
        dbWriterList.add(dbWriter);
        return dbWriter;
    }

    public void startWriters() {
        for (DBWriter dbWriter : dbWriterList) {
            dbWriter.start();
        }
    }

    public void stopWriters() {
        for (DBWriter dbWriter : dbWriterList) {
            dbWriter.stop();
        }
    }


    private Map<String, List<Integer>> getData(String startTime) {
        Map<String, List<Integer>> data = new HashMap<>();
        for (DBWriter dbWriter : dbWriterList) {
            String key = String.format("%s:%s", dbWriter.getDatabase(), dbWriter.getTable());
            data.put(key, mssqlConnection.getLastRows(dbWriter.getDatabase(), dbWriter.getTable(), 10, startTime));
        }
        return data;
    }


    public void verifyDataLists(GetSnapshotResponse snapshotResponse,Map<String, List<Integer>> data) {
        for (Map.Entry<String, List<Integer>> entry : data.entrySet()) {
            String[] parts = entry.getKey().split(":");
            assertThat(parts.length).isEqualTo(2);
            String database = parts[0];
            String table = parts[1];
            logger.info("verify {}.dbo.{}", database, table);
            assertThat(mssqlConnection.isDatabaseExists(database)).isTrue();
            assertThat(mssqlConnection.isTableExists(database, table)).isTrue();

            // verify there are no more items in snapshot
            String startTime = snapshotResponse.getSnapshotTime();
            int diff = mssqlConnection.getTemplate().queryForObject(String.format("select count(*) from %s.dbo.%s where timestamp>'%s'", database, table, startTime), Integer.class);
            logger.info("Diff " + diff);
            //assertThat(diff).isLessThanOrEqualTo(5);

            // verify there are no items missed in snapshot
            List<Integer> ids = mssqlConnection.getLastRows(database, table, 10, startTime);
            logger.info("Expected: {}", entry.getValue().toString());
            logger.info("Restored: {}", ids.toString());

            assertThat(entry.getValue().size()).isEqualTo(ids.size());
            if (!entry.getValue().isEmpty()) {
                assertThat(Collections.max(entry.getValue())).isEqualTo(Collections.max(ids));
                assertThat(Collections.min(entry.getValue())).isEqualTo(Collections.min(ids));
            }
        }
    }


    public void loadDataBySize(String database, String table, int totalSizeInKB) throws Exception {
        if (totalSizeInKB <= 0) {
            return;
        }
        if (totalSizeInKB <= 10240) {
            directLoadDataBySize(database, table, totalSizeInKB);
            return;
        }

        String tmpTable = "temp_data";
        mssqlConnection.createDatabase(database);
        DataPathTestModel.createTable(mssqlConnection, database, tmpTable);
        DataPathTestModel.createTable(mssqlConnection, database, table);

        logger.info("Write 10M data in tmp table.");
        String text = DataGenerator.getData(1024 * 1024 * 10);
        DataPathTestModel model = new DataPathTestModel(text);
        model.save(mssqlConnection, database, tmpTable);
        logger.info("Write completed in tmp table.");
        String sql = String.format("INSERT INTO %s.dbo.%s (data) SELECT data FROM %s.dbo.%s",
                database, table, database, tmpTable);
        int count = (totalSizeInKB - 1) / 10240 + 1;
        logger.info("Start insert data.");
        for (int i = 0; i < count; i++) {
            mssqlConnection.getTemplate().execute(sql);
            if (i % 10 == 9) {
                logger.info("Inserted 100M to table.");
            }
        }
        logger.info("Completed insert data.");
    }

    private void directLoadDataBySize(String database, String table, int size) throws Exception {
        logger.info("Start write {} kb to {}.dbo.{}", size, database, table);
        mssqlConnection.createDatabase(database);
        DataPathTestModel.createTable(mssqlConnection, database, table);
        String text = DataGenerator.getData(size * 1024);
        DataPathTestModel model = new DataPathTestModel(text);
        model.save(mssqlConnection, database, table);
        logger.info("Write completed.");
    }
}
