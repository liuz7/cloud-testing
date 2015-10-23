/*
 *
 *  * ******************************************************
 *  * Copyright VMware, Inc. 2014.   All Rights Reserved.
 *  * ******************************************************
 *
 */

package com.vmware.vchs.converter.instance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.vchs.model.portal.instance.CreateInstanceRequest;
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
 * The Instance Request Converter for Instance Request Json-Object converter.
 */
public class CreateInstanceRequestConverter extends AbstractHttpMessageConverter<CreateInstanceRequest> {

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(CreateInstanceRequestConverter.class);


    public CreateInstanceRequestConverter() {
        super(new MediaType("application", "json"));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.getCanonicalName().equalsIgnoreCase(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
    }

    @Override
    protected CreateInstanceRequest readInternal(Class<? extends CreateInstanceRequest> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        CreateInstanceRequest createInstanceRequest = this.objectMapper.readValue(inputMessage.getBody(), clazz);
        return checkNotNull(createInstanceRequest);
    }

    @Override
    protected void writeInternal(CreateInstanceRequest createInstanceRequest, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        checkNotNull(createInstanceRequest);
        this.objectMapper.writeValue(outputMessage.getBody(), createInstanceRequest);
        logger.debug(this.objectMapper.writeValueAsString(createInstanceRequest));
    }
}
