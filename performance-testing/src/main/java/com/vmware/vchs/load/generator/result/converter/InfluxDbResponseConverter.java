package com.vmware.vchs.load.generator.result.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.vchs.load.generator.result.model.ElasticSearchResponse;
import com.vmware.vchs.load.generator.result.model.InfluxDbResponse;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import sun.misc.IOUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by georgeliu on 14/10/31.
 */
public class InfluxDbResponseConverter extends AbstractHttpMessageConverter<InfluxDbResponse> {

    private ObjectMapper objectMapper = new ObjectMapper();

    public InfluxDbResponseConverter() {
        super(new MediaType("application", "json"));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.getCanonicalName().equalsIgnoreCase(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
    }

    @Override
    protected InfluxDbResponse readInternal(Class<? extends InfluxDbResponse> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        InfluxDbResponse getInstanceResponse = this.objectMapper.readValue(inputMessage.getBody(), InfluxDbResponse.class);
        logger.debug(this.objectMapper.writeValueAsString(getInstanceResponse));
        return checkNotNull(getInstanceResponse);
    }


    @Override
    protected void writeInternal(InfluxDbResponse getInstanceResponse, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        checkNotNull(getInstanceResponse);
        this.objectMapper.writeValue(outputMessage.getBody(), getInstanceResponse);
    }
}
