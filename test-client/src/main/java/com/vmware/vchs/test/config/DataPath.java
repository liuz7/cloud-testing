package com.vmware.vchs.test.config;

/**
 * Created by sjun on 8/13/15.
 */
public class DataPath {
    public static class DbWriter {
        private int period;
        private int rowSize;

        public int getRowSize() {
            return rowSize;
        }

        public void setRowSize(int rowSize) {
            this.rowSize = rowSize;
        }

        public int getPeriod() {
            return period;
        }

        public void setPeriod(int period) {
            this.period = period;
        }
    }

    private int waitTime;
    private DbWriter dbWriter;
    private int preLoadSize;

    public int getPreLoadSize() {
        return preLoadSize;
    }

    public void setPreLoadSize(int preLoadSize) {
        this.preLoadSize = preLoadSize;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public DbWriter getDbWriter() {
        return dbWriter;
    }

    public void setDbWriter(DbWriter dbWriter) {
        this.dbWriter = dbWriter;
    }
}