package com.vmware.vchs.converter.iamrole;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vmware.vchs.model.portal.iamRole.UpdateRoleRequest;
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
 * Created by fanz on 5/5/15.
 * <p>
 * This UpdateRoleRequestConverter is for Updating Reuqest JSON-Object converter
 */
public class UpdateRoleRequestConverter extends AbstractHttpMessageConverter<UpdateRoleRequest> {

    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(UpdateRoleRequestConverter.class);


    public UpdateRoleRequestConverter() {
        super(new MediaType("application", "json"));
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }


    protected boolean supports(Class<?> clazz) {
        return clazz.getCanonicalName().equalsIgnoreCase(((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
    }


    protected UpdateRoleRequest readInternal(Class<? extends UpdateRoleRequest> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        UpdateRoleRequest updateRoleRequest = this.objectMapper.readValue(inputMessage.getBody(), clazz);
        return checkNotNull(updateRoleRequest);
    }


    protected void writeInternal(UpdateRoleRequest updateRoleRequest, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        this.objectMapper.writeValue(outputMessage.getBody(), updateRoleRequest.getUpdateRoleRequestItemList());
        logger.debug(this.objectMapper.writeValueAsString(updateRoleRequest.getUpdateRoleRequestItemList()));
    }


}
