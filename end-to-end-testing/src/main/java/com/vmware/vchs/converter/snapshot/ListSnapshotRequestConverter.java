package com.vmware.vchs.converter.snapshot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.vchs.model.portal.snapshot.ListSnapshotRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by georgeliu on 14/12/17.
 */
public class ListSnapshotRequestConverter extends AbstractHttpMessageConverter<ListSnapshotRequest> {

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(ListSnapshotRequestConverter.class);

    public ListSnapshotRequestConverter() {
        super(new MediaType("application", "json"));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.getCanonicalName().equalsIgnoreCase(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
    }

    @Override
    protected ListSnapshotRequest readInternal(Class<? extends ListSnapshotRequest> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        ListSnapshotRequest createSnapshotRequest = this.objectMapper.readValue(inputMessage.getBody(), clazz);
        return checkNotNull(createSnapshotRequest);
    }

    @Override
    protected void writeInternal(ListSnapshotRequest createSnapshotRequest, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        checkNotNull(createSnapshotRequest);
        this.objectMapper.writeValue(outputMessage.getBody(), createSnapshotRequest);
        logger.debug(this.objectMapper.writeValueAsString(createSnapshotRequest));
    }
}
