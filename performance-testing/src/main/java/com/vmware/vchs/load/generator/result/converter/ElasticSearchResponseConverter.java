package com.vmware.vchs.load.generator.result.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.vchs.load.generator.result.model.ElasticSearchResponse;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.Charset;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by georgeliu on 14/10/31.
 */
public class ElasticSearchResponseConverter extends AbstractHttpMessageConverter<ElasticSearchResponse> {

    private ObjectMapper objectMapper = new ObjectMapper();

    public ElasticSearchResponseConverter() {
        super(new MediaType("application", "json"));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.getCanonicalName().equalsIgnoreCase(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
    }

    @Override
    protected ElasticSearchResponse readInternal(Class<? extends ElasticSearchResponse> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        ElasticSearchResponse getInstanceResponse = this.objectMapper.readValue(inputMessage.getBody(), clazz);
        logger.debug(this.objectMapper.writeValueAsString(getInstanceResponse));
        return checkNotNull(getInstanceResponse);
    }

    @Override
    protected void writeInternal(ElasticSearchResponse getInstanceResponse, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        checkNotNull(getInstanceResponse);
        this.objectMapper.writeValue(outputMessage.getBody(), getInstanceResponse);
    }
}
