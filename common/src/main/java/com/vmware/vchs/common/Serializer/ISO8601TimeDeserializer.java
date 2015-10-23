/*
 * *****************************************************
 *  Copyright VMware, Inc. 2014.   All Rights Reserved.
 *  ******************************************************
 */

package com.vmware.vchs.common.Serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import org.joda.time.DateTime;

import java.io.IOException;

import static com.vmware.vchs.common.Serializer.TimeUtils.fromISOString;

public class ISO8601TimeDeserializer extends StdScalarDeserializer<DateTime> {
    public ISO8601TimeDeserializer() {
        super(DateTime.class);
    }

    @Override
    public DateTime deserialize(JsonParser jp,
                                DeserializationContext ctxt) throws IOException, JsonProcessingException {

        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            String dateTimeAsString = jp.getText().trim();
            return fromISOString(dateTimeAsString);
        }

        throw ctxt.mappingException(handledType());
    }
}
