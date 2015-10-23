package com.vmware.vchs.tmclient;

import com.vmware.vchs.aas.transport.MessageHandler;
import com.vmware.vchs.aas.transport.MessageHeaders;
import com.vmware.vchs.aas.transport.TransportException;
import com.vmware.vchs.microservices.model.MicroServiceError;
import com.vmware.vchs.microservices.model.MicroServiceRequest;
import com.vmware.vchs.test.client.model.Request;
import com.vmware.vchs.tmclient.processor.MessageProcessor;
import com.vmware.vchs.common.utils.JsonUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuda on 15/4/2.
 */
public class NotificationHandler implements MessageHandler {
    private final MessageProcessor messageProcessor;

    public NotificationHandler(MessageProcessor messageProcessor){
        this.messageProcessor = messageProcessor;
    }

    @Override
    public void onMessage(byte[] bytes, MessageHeaders messageHeaders) {
        try {
            Map<String, String> headers = new HashMap<>();
            for (String key : messageHeaders.getAllKeys()) {
                headers.put(key, messageHeaders.getProperty(key));
            }
            Request request = messageProcessor.handleRequest(new Request(headers, bytes));
            if( request.getBody() instanceof MicroServiceRequest) {
                MicroServiceRequest convertedRequest = (MicroServiceRequest)request.getBody();
                if(convertedRequest.getPayload() instanceof HashMap) {
                    HashMap map =(HashMap)convertedRequest.getPayload();
                    if (map.containsKey("payload")) {
                        System.out.println(map.get("payload"));
                    }
                }
            }
        } catch (Exception e) {
            try {
                MicroServiceError error =
                        JsonUtils.parseJsonObject(bytes, MicroServiceError.class);
                System.out.println("error! ");
                System.out.println("  status               : " + error.getStatus());
                System.out.println("  code                 : " + error.getErrorcode());
                System.out.println("  message              : " + error.getMessage());
                System.out.println();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } finally {
        }
    }

    @Override
    public void onError(final String messageId, final TransportException ex) {
        System.err.println("## notification error?");
    }
}
