package com.vmware.vchs.model.constant;

public class ModelType {
    private ModelType() {}

    public static final String CREATE_BACKUP_REQUEST   = "CreateBackupRequest";
    public static final String DELETE_BACKUP_REQUEST   = "DeleteBackupRequest";
    public static final String LIST_BACKUPS_REQUEST    = "ListBackupsRequest";
    public static final String LIST_BACKUPS_RESPONSE   = "ListBackupsResponse";

    public static final String CREATE_INSTANCE_REQUEST = "CreateInstanceRequest";
    public static final String GET_INSTANCE_RESPONSE   = "GetInstanceResponse";
    public static final String LIST_INSTANCES_RESPONSE = "ListInstancesResponse";
    public static final String UPDATE_INSTANCE_REQUEST = "UpdateInstanceRequest";

    public static final String CREATE_SNAPSHOT_REQUEST = "CreateSnapshotRequest";
    public static final String GET_SNAPSHOT_RESPONSE   = "auto";
    public static final String LIST_SNAPSHOTS_RESPONSE = "ListSnapshotsResponse";

    public static final String BACKUP_RESOURCE         = "BackupResource";
    public static final String INSTANCE_RESOURCE       = "InstanceResource";
    public static final String SNAPSHOT_RESOURCE       = "SnapshotResource";

    public static final String NODE_CREATE_INSTANCE_REQUEST  = "node.CreateInstanceRequest";
    public static final String NODE_UPDATE_INSTANCE_REQUEST  = "node.UpdateInstanceRequest";
    public static final String NODE_CREATE_SNAPSHOT_REQUEST  = "node.CreateSnapshotRequest";

    public static final String NODE_RESOURCE_STATUS          = "node.ResourceStatus";
    public static final String NODE_SNAPSHOT                 = "node.Snapshot";
}
