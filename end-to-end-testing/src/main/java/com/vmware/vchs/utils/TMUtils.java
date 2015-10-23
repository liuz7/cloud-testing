package com.vmware.vchs.utils;

import com.vmware.vchs.model.nats.Version;
import com.vmware.vchs.tmclient.TaskManagerExecution;
import com.vmware.vchs.tmclient.constant.ResourceAction;
import com.vmware.vchs.tmclient.processor.TaskAckMessageProcessor;
import com.vmware.vchs.utils.handler.NotificationHandler;

import java.util.concurrent.ExecutionException;

/**
 * Created by georgeliu on 15/4/9.
 */
public class TMUtils {

    public static Version getVersion(String nodeId) throws InterruptedException, ExecutionException {
        TaskManagerExecution execution = new TaskManagerExecution();
        NotificationHandler<Version> notificationHandler = new NotificationHandler(new TaskAckMessageProcessor(), Version.class);
        execution.run(null, "111", "kjkj123", ResourceAction.getversion, 30 * 60, nodeId, notificationHandler);
        Version version = notificationHandler.get();
        return version;
    }

    public static String startMaintenance(String nodeId) throws InterruptedException, ExecutionException {
        TaskManagerExecution execution = new TaskManagerExecution();
        NotificationHandler<String> notificationHandler = new NotificationHandler(new TaskAckMessageProcessor(), String.class);
        execution.run(null, "111", "kjkj123", ResourceAction.maintain, 30 * 60, nodeId, notificationHandler);
        String status = notificationHandler.get();
        return status;
    }
}
