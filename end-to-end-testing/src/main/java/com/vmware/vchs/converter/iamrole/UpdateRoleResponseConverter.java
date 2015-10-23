package com.vmware.vchs.converter.iamrole;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.vmware.vchs.model.portal.iamRole.UpdateRoleResponse;
import com.vmware.vchs.model.portal.iamRole.UpdateRoleResponseItem;
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
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by fanz on 5/6/15.
 */
public class UpdateRoleResponseConverter extends AbstractHttpMessageConverter<UpdateRoleResponse>  {

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(UpdateRoleResponseConverter.class);

    public UpdateRoleResponseConverter() {
        super(new MediaType("application", "json"));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }


    @Override
    protected boolean supports (Class<?> clazz) {
        return clazz.getCanonicalName().equalsIgnoreCase(((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
    }


    //@Override
    protected UpdateRoleResponse readInternal(Class<? extends UpdateRoleResponse> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException{
        UpdateRoleResponse updateRoleResponse = new UpdateRoleResponse();
        String body = CharStreams.toString(new InputStreamReader(inputMessage.getBody(), Charsets.UTF_8));
        JsonNode rootNode = checkNotNull(this.objectMapper.readTree(body), "The list response should not be null");
        UpdateRoleResponseItem[] updateRoleResponseItemArray = this.objectMapper.readValue(rootNode.toString(), UpdateRoleResponseItem[].class);
        List<UpdateRoleResponseItem> updateRoleResponseItems = Arrays.asList(updateRoleResponseItemArray);
        updateRoleResponse.setUpdateRoleResponseItemList(updateRoleResponseItems);
        logger.debug(this.objectMapper.writeValueAsString(updateRoleResponseItems));
        return updateRoleResponse;
    }


    //@Override
    protected void writeInternal(UpdateRoleResponse updateRoleResponse, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        this.objectMapper.writeValue(outputMessage.getBody(), updateRoleResponse);
    }


}
