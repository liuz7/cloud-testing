package com.vmware.vchs.datapath;

import com.vmware.vchs.launcher.TestHelper;
import com.vmware.vchs.test.config.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Created by sjun on 8/12/15.
 */
public class DBWriter implements Runnable {
    protected static final Configuration configuration = TestHelper.getConfiguration();

    private MssqlConnection connection;
    private String database;
    private String table;
    private Thread thread;

    private int period;
    private int rowSize;
    public DBWriter(MssqlConnection connection, String database, String table){
        this.connection = connection;
        this.database = database;
        this.table = table;
        this.thread = null;
        this.period = configuration.getDataPath().getDbWriter().getPeriod();
        this.rowSize = configuration.getDataPath().getDbWriter().getRowSize() * 1024;

        init();
    }

    protected void init() {
        connection.createDatabase(database);
        DataPathTestModel.createTable(connection, database, table);
    }

    public String getTable() {
        return table;
    }

    public String getDatabase() {
        return database;
    }

    public synchronized void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }
    }

    public synchronized void stop() {
        if(thread != null) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
            thread = null;
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                insertOneRow();
                TimeUnit.SECONDS.sleep(period);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void insertOneRow() {
        String text = DataGenerator.getData(rowSize);
        DataPathTestModel model = new DataPathTestModel(text);
        model.save(connection, database, table);
    }

    public void insertTestData(int size) {
        String text = DataGenerator.getData(size);
        DataPathTestModel model = new DataPathTestModel(text);
        model.save(connection, database, table);
    }
}
