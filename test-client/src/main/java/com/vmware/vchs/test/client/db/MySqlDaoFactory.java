/*
 * ******************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ******************************************************
 */
package com.vmware.vchs.test.client.db;


/**
 * @author liuda
 */
public interface MySqlDaoFactory {
//    BackupDao createBackupDao();
//
//    SnapshotDao createSnapshotDao();

    SysDao createSysDao();
}
