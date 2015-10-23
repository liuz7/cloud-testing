package com.vmware.vchs.converter.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.vmware.vchs.model.portal.common.ListResponse;
import com.vmware.vchs.model.portal.instance.ListInstanceItem;
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
 * Created by georgeliu on 14/10/31.
 */
public class ListResponseConverter extends AbstractHttpMessageConverter<ListResponse> {

    private ObjectMapper objectMapper = new ObjectMapper();

    public ListResponseConverter() {
        super(new MediaType("application", "json"));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.getCanonicalName().equalsIgnoreCase(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
    }

    @Override
    protected ListResponse readInternal(Class<? extends ListResponse> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        ListResponse listResponse = new ListResponse();
        String body = CharStreams.toString(new InputStreamReader(inputMessage.getBody(), Charsets.UTF_8));
        JsonNode rootNode = checkNotNull(this.objectMapper.readTree(body), "The list response should not be null");
        ArrayNode instances = checkNotNull((ArrayNode) rootNode.findValue("data"), "The list data should not be null");
        ListInstanceItem[] instancesArray = this.objectMapper.readValue(instances.toString(), ListInstanceItem[].class);
        listResponse.setData(Arrays.asList(instancesArray));
        listResponse.setTotal(checkNotNull(rootNode.findValue("total")).asInt());
        listResponse.setPage(checkNotNull(rootNode.findValue("page")).asInt());
        listResponse.setPageSize(checkNotNull(rootNode.findValue("pageSize")).asInt());
        logger.debug(this.objectMapper.writeValueAsString(listResponse));
        return checkNotNull(listResponse);
    }

    @Override
    protected void writeInternal(ListResponse listResponse, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        checkNotNull(listResponse);
        this.objectMapper.writeValue(outputMessage.getBody(), listResponse);
    }
}
