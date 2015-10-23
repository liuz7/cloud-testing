package com.vmware.vchs.tmclient.processor;

import com.vmware.vchs.test.client.model.Request;
import com.vmware.vchs.common.utils.JsonUtils;
import com.vmware.vchs.test.client.model.HeaderConstants;
import com.vmware.vchs.test.client.model.TaskType;
import com.vmware.vchs.microservices.model.MicroServiceRequest;
import com.vmware.vchs.taskservice.common.message.NotifyMessage;
import com.vmware.vchs.taskservice.common.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TaskAckMessageProcessor extends AbstractMessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(TaskAckMessageProcessor.class);

    public TaskAckMessageProcessor() {
        super();
    }

    @Override
    public Request handleRequest(Request request) {
        Map<String, String> headers = new HashMap<>();
        headers.putAll(request.getHeaders());
        NotifyMessage notifyMessage = null;
        MicroServiceRequest nodeRequest = null;
        try {
            notifyMessage = objectMapper.readValue((byte[]) request.getBody(), NotifyMessage.class);
            byte[] context = Base64.decode((String) notifyMessage.getTask().getContext());
            nodeRequest = JsonUtils.parseJsonObject(context, MicroServiceRequest.class);

            logger.info("Complete event for task: {}, task-id: {}, status: {}", notifyMessage.getTask().getName(),
                    notifyMessage.getTaskId(),
                    notifyMessage.getTask().getStatus());
        } catch (Exception e) {
            logger.error("Failed when unMarshall TM complete message", e);
            return null;
        }

        nodeRequest.withPayload(notifyMessage.getTask().getOutput());
        headers.put(HeaderConstants.TASK_TYPE, TaskType.event.name());
        headers.put(HeaderConstants.TASK_STATUS, notifyMessage.getTask().getStatus().name());
        return new Request(headers, nodeRequest);
    }

}
