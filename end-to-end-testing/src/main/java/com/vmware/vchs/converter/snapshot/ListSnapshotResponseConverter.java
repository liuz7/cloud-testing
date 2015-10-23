package com.vmware.vchs.converter.snapshot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.vchs.model.portal.snapshot.ListSnapshotResponse;
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
public class ListSnapshotResponseConverter extends AbstractHttpMessageConverter<ListSnapshotResponse> {

    private ObjectMapper objectMapper = new ObjectMapper();

    public ListSnapshotResponseConverter() {
        super(new MediaType("application", "json"));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.getCanonicalName().equalsIgnoreCase(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
    }

    @Override
    protected ListSnapshotResponse readInternal(Class<? extends ListSnapshotResponse> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        ListSnapshotResponse listSnapshotResponse = this.objectMapper.readValue(inputMessage.getBody(), clazz);
        logger.debug(this.objectMapper.writeValueAsString(listSnapshotResponse));
        return checkNotNull(listSnapshotResponse);
    }

    @Override
    protected void writeInternal(ListSnapshotResponse listSnapshotResponse, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        checkNotNull(listSnapshotResponse);
        this.objectMapper.writeValue(outputMessage.getBody(), listSnapshotResponse);
    }

}
