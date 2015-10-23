package com.vmware.vchs.converter.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.vchs.model.portal.services.Service;
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
 * Created by georgeliu on 15/4/23.
 */
public class ServicesConverter extends AbstractHttpMessageConverter<Service> {

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(ServicesConverter.class);

    public ServicesConverter() {
        super(new MediaType("application", "json"));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.getCanonicalName().equalsIgnoreCase(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
    }

    @Override
    protected Service readInternal(Class<? extends Service> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        Service service = this.objectMapper.readValue(inputMessage.getBody(), clazz);
        logger.debug(this.objectMapper.writeValueAsString(service));
        return checkNotNull(service);
    }

    @Override
    protected void writeInternal(Service service, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        checkNotNull(service);
        this.objectMapper.writeValue(outputMessage.getBody(), service);
    }

}
