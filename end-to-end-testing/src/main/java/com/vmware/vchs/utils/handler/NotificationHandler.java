package com.vmware.vchs.utils.handler;

import com.vmware.vchs.aas.transport.MessageHandler;
import com.vmware.vchs.aas.transport.MessageHeaders;
import com.vmware.vchs.aas.transport.TransportException;
import com.vmware.vchs.common.utils.JsonInstanceSerializer;
import com.vmware.vchs.microservices.model.MicroServiceRequest;
import com.vmware.vchs.test.client.model.HeaderConstants;
import com.vmware.vchs.test.client.model.Request;
import com.vmware.vchs.tmclient.processor.MessageProcessor;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by liuda on 15/4/2.
 */
public class NotificationHandler<T> implements Future<T>, MessageHandler {

    private final MessageProcessor messageProcessor;
    private JsonInstanceSerializer jsonInstanceSerializer;

    private enum State {WAITING, DONE, CANCELLED}

    private volatile State state = State.WAITING;
    private final BlockingQueue<T> reply = new ArrayBlockingQueue<>(1);

    public NotificationHandler(MessageProcessor messageProcessor, Class<T> payloadClass) {
        this.messageProcessor = messageProcessor;
        this.jsonInstanceSerializer = new JsonInstanceSerializer(payloadClass);
    }

    @Override
    public void onMessage(byte[] bytes, MessageHeaders messageHeaders) {
        try {
            Map<String, String> headers = new HashMap<>();
            for (String key : messageHeaders.getAllKeys()) {
                headers.put(key, messageHeaders.getProperty(key));
            }
            Request request = messageProcessor.handleRequest(new Request(headers, bytes));
            if (request.getBody() instanceof MicroServiceRequest) {
                MicroServiceRequest convertedRequest = (MicroServiceRequest) request.getBody();
                if (convertedRequest.getPayload() instanceof HashMap) {
                    HashMap map = (HashMap) convertedRequest.getPayload();
                    if (map.containsKey("payload")) {
                        Object payload = map.get("payload");
                        T result = (T) this.jsonInstanceSerializer.deserialize((Map) payload);
                        reply.put(result);
                    } else {
                        String status = request.getHeaders().get(HeaderConstants.TASK_STATUS);
                        reply.put((T) status);
                    }
                }
            }
        } catch (Exception e) {
            try {
                String errorString = new String(bytes, "UTF-8");
                reply.put((T) errorString);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    @Override
    public void onError(final String messageId, final TransportException ex) {
        System.err.println("## notification error?");
    }

    @Override
    public boolean isDone() {
        return state == State.DONE;
    }

    @Override
    public boolean isCancelled() {
        return state == State.CANCELLED;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        try {
            state = State.CANCELLED;
            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return this.reply.take();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        final T replyOrNull = reply.poll(timeout, unit);
        if (replyOrNull == null) {
            throw new TimeoutException();
        }
        return replyOrNull;
    }
}
