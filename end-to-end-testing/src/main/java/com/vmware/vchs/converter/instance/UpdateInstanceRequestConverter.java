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
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Sets;
import com.vmware.vchs.common.utils.ObjectUtils;
import com.vmware.vchs.model.portal.instance.UpdateInstanceRequest;
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
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The Instance Request Converter for Instance Request Json-Object converter.
 */
public class UpdateInstanceRequestConverter extends AbstractHttpMessageConverter<UpdateInstanceRequest> {

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(UpdateInstanceRequestConverter.class);


    public UpdateInstanceRequestConverter() {
        super(new MediaType("application", "json"));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.getCanonicalName().equalsIgnoreCase(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
    }

    @Override
    protected UpdateInstanceRequest readInternal(Class<? extends UpdateInstanceRequest> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        UpdateInstanceRequest updateInstanceRequest = this.objectMapper.readValue(inputMessage.getBody(), clazz);
        return checkNotNull(updateInstanceRequest);
    }

    @Override
    protected void writeInternal(UpdateInstanceRequest updateInstanceRequest, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        checkNotNull(updateInstanceRequest);
        Set<String> propertiesToBeUpdated = Sets.newHashSet();
        for (String property : ObjectUtils.getProperty(UpdateInstanceRequest.class)) {
            String value = ObjectUtils.getProperty(updateInstanceRequest, property);
            if (value != null && !value.equalsIgnoreCase("0")) {
                propertiesToBeUpdated.add(property);
            }
        }
        SimpleBeanPropertyFilter theFilter = SimpleBeanPropertyFilter.filterOutAllExcept(propertiesToBeUpdated);
        FilterProvider filters = new SimpleFilterProvider().addFilter("updateFilter", theFilter);
        this.objectMapper.writer(filters).writeValue(outputMessage.getBody(), updateInstanceRequest);
        logger.debug(this.objectMapper.writer(filters).writeValueAsString(updateInstanceRequest));
    }
}
