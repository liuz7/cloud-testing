package com.vmware.vchs.converter.networks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.vchs.model.portal.instance.DataPath;
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
 * Created by georgeliu on 15/5/28.
 */
public class UpdateInstanceConnectionResponseConverter extends AbstractHttpMessageConverter<DataPath> {

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(UpdateInstanceConnectionRequestConverter.class);


    public UpdateInstanceConnectionResponseConverter() {
        super(new MediaType("application", "json"));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.getCanonicalName().equalsIgnoreCase(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
    }

    @Override
    protected DataPath readInternal(Class<? extends DataPath> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        DataPath dataPath = this.objectMapper.readValue(inputMessage.getBody(), clazz);
        logger.debug(this.objectMapper.writeValueAsString(dataPath));
        return checkNotNull(dataPath);
    }

    @Override
    protected void writeInternal(DataPath dataPath, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        checkNotNull(dataPath);
        this.objectMapper.writeValue(outputMessage.getBody(), dataPath);
        logger.debug(this.objectMapper.writeValueAsString(dataPath));
    }
}
