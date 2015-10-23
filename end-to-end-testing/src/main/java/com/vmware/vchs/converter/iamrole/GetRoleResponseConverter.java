package com.vmware.vchs.converter.iamrole;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.vmware.vchs.model.portal.iamRole.GetRoleResponse;
import com.vmware.vchs.model.portal.iamRole.RoleItemResponse;
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
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by fanz on 5/25/15.
 */

public class GetRoleResponseConverter extends AbstractHttpMessageConverter<GetRoleResponse> {

    private ObjectMapper objectMapper = new ObjectMapper();


    public GetRoleResponseConverter() {
        super(new MediaType("application", "json"));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return clazz.getCanonicalName().equalsIgnoreCase(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
    }

    @Override
    protected GetRoleResponse readInternal(Class<? extends GetRoleResponse> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        GetRoleResponse getRoleResponse = new GetRoleResponse();
        String body = CharStreams.toString(new InputStreamReader(inputMessage.getBody(), Charsets.UTF_8));
        JsonNode rootNode = checkNotNull(this.objectMapper.readTree(body), "The list response should not be null");
        RoleItemResponse[] roleItemResponseArray = this.objectMapper.readValue(rootNode.toString(), RoleItemResponse[].class);
        List<RoleItemResponse> roleItemResponses = Arrays.asList(roleItemResponseArray);
        getRoleResponse.setRoleItemResponses(roleItemResponses);
        logger.debug(this.objectMapper.writeValueAsString(roleItemResponses));
        return getRoleResponse;
    }

    @Override
    protected void writeInternal(GetRoleResponse roleItemResponses, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        this.objectMapper.writeValue(outputMessage.getBody(), roleItemResponses);
    }


}
