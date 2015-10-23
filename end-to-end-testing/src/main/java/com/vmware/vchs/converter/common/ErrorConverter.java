package com.vmware.vchs.converter.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.vchs.model.portal.common.Error;
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
 * Created by georgeliu on 14/10/31.
 */
public class ErrorConverter extends AbstractHttpMessageConverter<com.vmware.vchs.model.portal.common.Error> {

    private ObjectMapper objectMapper = new ObjectMapper();

    public ErrorConverter() {
        super(new MediaType("application", "json"));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.getCanonicalName().equalsIgnoreCase(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
    }

    @Override
    protected Error readInternal(Class<? extends Error> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        Error error = this.objectMapper.readValue(inputMessage.getBody(), clazz);
        logger.debug(this.objectMapper.writeValueAsString(error));
        return checkNotNull(error);
    }

    @Override
    protected void writeInternal(Error error, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        checkNotNull(error);
        this.objectMapper.writeValue(outputMessage.getBody(), error);
    }
}
