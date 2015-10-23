package com.vmware.vchs.nats;



import com.vmware.vchs.common.utils.JsonUtils;
import com.vmware.vchs.microservices.client.MicroServiceClient;
import com.vmware.vchs.microservices.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.vmware.vchs.aas.transport.*;

import java.util.concurrent.TimeUnit;

public class NatsClient implements MicroServiceClient {
    private TransportConnection mConnection;
    private static Logger logger = LoggerFactory.getLogger(NatsClient.class);
    InvocationContext iContext;

    /*
     * work around for lacking of this information from response
     */
    static public String resourceAction;
    static public String resourceType;

    public NatsClient(final TransportConnection connection) {
        mConnection = connection;
        iContext = mConnection.createRequestContext();
    }


    static class GatewayMessageHandler implements MessageHandler {
        private MicroServiceResponseHandler mResponseHandler;

        public GatewayMessageHandler(final MicroServiceResponseHandler responseHandler) {
            mResponseHandler = responseHandler;
        }

        @Override
        public void onMessage(byte[] bytes, MessageHeaders messageHeaders) {
            try {
                MicroServiceResponse response = JsonUtils.parseJsonObject(bytes, MicroServiceResponse.class);
                byte[] payloadBytes = JsonUtils.toByteArray(response.getPayload());

                if (messageHeaders.getProperty("microservice.errorcode") != null) {
                    handleError(payloadBytes);
                }
            } catch (Exception e) {
                logger.error("Got exception during <onMessage>", e);
            }
        }

        private void handleError(byte[] bytes) {
            try {
                MicroServiceError error =
                        JsonUtils.parseJsonObject(bytes, MicroServiceError.class);

                String info = "\ncode:" + error.getErrorcode()
                        + "\nstatus:" + error.getStatus()
                        + "\nmessage:" + error.getMessage()
                        + "\n";

                mResponseHandler.onError(new Throwable(info));
            } catch (Exception e) {
                logger.error("failed to deSerialize error response!", e);
            }
        }

        @Override
        public void onError(final String messageId, final TransportException ex) {
        }
    }

    @Override
    public void asyncInvoke(final String destination,
                            final long timeout,
                            final MicroServiceRequest request,
                            final MicroServiceResponseHandler responseHandler) {

        MessageHeaders headers = fillHeaders(request);
        resourceAction = headers.getProperty(MicroServiceRequest.OPERATIONNAME);
        resourceType = headers.getProperty(MicroServiceRequest.RESOURCETYPE);

        try {
            mConnection.request(new MessageEndpoint(destination, DestinationType.QUEUE),
                    (byte[]) request.getPayload(), headers, iContext, timeout,
                    TimeUnit.SECONDS, new GatewayMessageHandler(responseHandler));
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    static private MessageHeaders fillHeaders(MicroServiceRequest request) {
        final MessageHeaders properties = new MessageHeaders();

        properties.setProperty(MicroServiceRequest.REQUESTID, request.getRequestID());
        properties.setProperty(MicroServiceRequest.SERVICEID, request.getServiceID());
        if(request.getResourceID() != null)
            properties.setProperty(MicroServiceRequest.RESOURCEID, request.getResourceID());
        properties.setProperty(MicroServiceRequest.VERSION, "1.0");
        properties.setProperty(MicroServiceRequest.RESOURCETYPE, request.getResourceType());
        properties.setProperty(MicroServiceRequest.OPERATIONNAME, request.getOperationName());
        properties.setProperty(MicroServiceRequest.SECURITY_TOKEN, "sec-tkn");

        return properties;
    }

}
