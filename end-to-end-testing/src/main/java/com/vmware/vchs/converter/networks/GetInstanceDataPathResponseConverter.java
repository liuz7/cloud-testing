package com.vmware.vchs.converter.networks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.vchs.model.portal.instance.Connections;
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
 * Created by georgeliu on 15/4/1.
 */
public class GetInstanceDataPathResponseConverter extends AbstractHttpMessageConverter<Connections> {

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(GetInstanceDataPathResponseConverter.class);

    public GetInstanceDataPathResponseConverter() {
        super(new MediaType("application", "json"));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.getCanonicalName().equalsIgnoreCase(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
    }

    @Override
    protected Connections readInternal(Class<? extends Connections> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        Connections connections = this.objectMapper.readValue(inputMessage.getBody(), clazz);
        logger.debug(this.objectMapper.writeValueAsString(connections));
        return checkNotNull(connections);
    }

    @Override
    protected void writeInternal(Connections connections, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        checkNotNull(connections);
        this.objectMapper.writeValue(outputMessage.getBody(), connections);
    }


}
