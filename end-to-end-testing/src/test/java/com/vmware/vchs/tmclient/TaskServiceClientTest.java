package com.vmware.vchs.tmclient;/*
 * ******************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ******************************************************
 */

import com.vmware.vchs.tmclient.processor.TaskAckMessageProcessor;
import com.vmware.vchs.tmclient.constant.ResourceAction;

public class TaskServiceClientTest {

    public static void main(final String[] args) {
        TaskManagerExecution execution = new TaskManagerExecution();
        execution.run(null, "111", "kjkj123", ResourceAction.getversion, 10, "liuda-mssqlnode-0", new NotificationHandler(new TaskAckMessageProcessor()));
    }

}
