package com.vmware.vchs.converter.instance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
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
public class GetInstanceResponseConverter extends AbstractHttpMessageConverter<GetInstanceResponse> {

    private ObjectMapper objectMapper = new ObjectMapper();

    public GetInstanceResponseConverter() {
        super(new MediaType("application", "json"));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.getCanonicalName().equalsIgnoreCase(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
    }

    @Override
    protected GetInstanceResponse readInternal(Class<? extends GetInstanceResponse> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        GetInstanceResponse getInstanceResponse = this.objectMapper.readValue(inputMessage.getBody(), clazz);
        logger.debug(this.objectMapper.writeValueAsString(getInstanceResponse));
        return checkNotNull(getInstanceResponse);
    }

    @Override
    protected void writeInternal(GetInstanceResponse getInstanceResponse, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        checkNotNull(getInstanceResponse);
        this.objectMapper.writeValue(outputMessage.getBody(), getInstanceResponse);
    }
}
