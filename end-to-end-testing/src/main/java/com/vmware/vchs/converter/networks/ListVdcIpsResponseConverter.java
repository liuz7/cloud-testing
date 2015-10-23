package com.vmware.vchs.converter.networks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.vmware.vchs.model.portal.networks.Data;
import com.vmware.vchs.model.portal.networks.ListVdcIpsResponse;
import com.vmware.vchs.model.portal.networks.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by georgeliu on 15/4/1.
 */
public class ListVdcIpsResponseConverter extends AbstractHttpMessageConverter<ListVdcIpsResponse> {

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(ListVdcIpsResponseConverter.class);

    public ListVdcIpsResponseConverter() {
        super(new MediaType("application", "json"));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.getCanonicalName().equalsIgnoreCase(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
    }

    @Override
    protected ListVdcIpsResponse readInternal(Class<? extends ListVdcIpsResponse> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        ListVdcIpsResponse listVdcIpsResponse = new ListVdcIpsResponse();
        Data data = new Data();
        String body = CharStreams.toString(new InputStreamReader(inputMessage.getBody(), Charsets.UTF_8));
        JsonNode rootNode = checkNotNull(this.objectMapper.readTree(body));
        ArrayNode regions = checkNotNull((ArrayNode) rootNode.findValue("data"));
        Region[] regionsArray = this.objectMapper.readValue(regions.toString(), Region[].class);
        data.setRegions(Arrays.asList(regionsArray));
        listVdcIpsResponse.setData(data);
        logger.debug(this.objectMapper.writeValueAsString(listVdcIpsResponse));
        return checkNotNull(listVdcIpsResponse);
    }

    @Override
    protected void writeInternal(ListVdcIpsResponse listVdcIpsResponse, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        checkNotNull(listVdcIpsResponse);
        this.objectMapper.writeValue(outputMessage.getBody(), listVdcIpsResponse);
    }

}
