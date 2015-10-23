/*
 * *****************************************************
 *  Copyright VMware, Inc. 2014.   All Rights Reserved.
 *  ******************************************************
 */

package com.vmware.vchs.model.portal.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

public class ISO8601TimeDeserializer extends StdScalarDeserializer<DateTime> {
    public ISO8601TimeDeserializer() {
        super(DateTime.class);
    }

    @Override
    public DateTime deserialize(JsonParser jp,
                                DeserializationContext ctxt) throws IOException {

        JsonToken currentToken = jp.getCurrentToken();
        if (currentToken == JsonToken.VALUE_STRING) {
            String dateTimeAsString = jp.getText().trim();
            return fromISOString(dateTimeAsString);
        }

        throw ctxt.mappingException(handledType());
    }

    public static DateTime fromISOString(String time) {
        if (time == null || time.isEmpty()) {
            return null;
        }
        if(time.matches(".*\\.\\d+Z$")){
            return ISODateTimeFormat.dateTime().withZoneUTC().parseDateTime(time);
        }
        return ISODateTimeFormat.dateTimeNoMillis().withZoneUTC().parseDateTime(time);
    }
}
