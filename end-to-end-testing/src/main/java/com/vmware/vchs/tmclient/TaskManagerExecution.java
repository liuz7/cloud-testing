/*
 * *****************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * *****************************************************
 */

package com.vmware.vchs.tmclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vmware.vchs.aas.transport.*;
import com.vmware.vchs.common.utils.JsonUtils;
import com.vmware.vchs.microservices.model.MicroServiceRequest;
import com.vmware.vchs.test.client.model.etcd.MSSqlNode;
import com.vmware.vchs.taskservice.client.JobBuilder;
import com.vmware.vchs.taskservice.client.TaskServiceFactory;
import com.vmware.vchs.test.client.etcd.BasicEtcdClient;
import com.vmware.vchs.tmclient.constant.ResourceAction;
import com.vmware.vchs.tmclient.constant.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TaskManagerExecution {

    public TaskManagerExecution() {
        TransportConnectionFactory.init();
        config = new ServiceConfig();
        Map<String, Object> properties = new HashMap<>();
        properties.put(TransportConstants.USERNAME, config.getMessageBusUsername());
        properties.put(TransportConstants.PASSWORD, config.getMessageBusPassword());
        connection = TransportConnectionFactory
                .createConnection(TransportType.NATS, config.getMessageBusServer(), properties);

    }

    private static Logger logger = LoggerFactory.getLogger(TaskManagerExecution.class);
    private TransportConnection connection;
    private MessageEndpoint endpoint;
    private ServiceConfig config;

//  this is broken, and not used in project
//  private String getNodeIdFromEtcd(int index) {
//        List<MSSqlNode> etcdNodes = new BasicEtcdClient().getMSSqlNodes();
//        return etcdNodes.get(index).getId();
//    }


    private static MicroServiceRequest serviceRequestBuilder(Object payload, String resourceId, String requestId, ResourceAction action) {
        return new MicroServiceRequest()
                .withRequestID(requestId)
                .withServiceID(ServiceType.mssql.name())
                .withApiVersion("1.0")
                .withOperationName(action.toString())
                .withResourceType("instances")
                .withResourceID(resourceId)
                .withPayload(payload);
    }

    public void run(Object payload, String resourceId, String requestId, ResourceAction action, int expireInSeconds, String nodeId, MessageHandler handler) {
        try {
            String taskId = UUID.randomUUID().toString();
            nodeId = "dbaas.node." + nodeId;

            final com.vmware.vchs.taskservice.common.model.Task t = new JobBuilder().task()
                    .withResourceId(UUID.randomUUID().toString())
                    .withTaskId(taskId)
                    .withName(action.toString())
                    .assignTo(config.getNodePubURI(nodeId))
                    .notifyTo(config.getNatsURI(nodeId))
                    .expireIn(expireInSeconds)
                    .withContext(JsonUtils.toByteArray(serviceRequestBuilder(payload, resourceId, requestId, action)))
                    .withAssigneeAcceptType("application/json")
                    .withNotifyStrategy(JobBuilder.TaskBuilder.backoff(10, 1000))
                    .build();

            Map<String, String> params = new HashMap<>();
            endpoint = new MessageEndpoint(nodeId, DestinationType.TOPIC);
            connection.subscribe(endpoint, handler);
            params.putIfAbsent("TASK_PUBLISH_URI", config.getTMPubURI());
            params.putIfAbsent("QUERY_PUBLISH_URI", config.getTMQueryURI());
            TaskServiceFactory.newInstance(params).submit(t);

        } catch (UnsupportedOperationException e) {
            logger.error("Circuit open for taskManager, fast fail!");
        } catch (JsonProcessingException e) {
            logger.error("Got json exception when submitted task to task manager: {}", e);
        } catch (Exception e) {
            logger.error("Got TaskRemoteException when submitted task to task manager: {}", e);
        }
    }
}



