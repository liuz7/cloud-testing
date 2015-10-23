package com.vmware.vchs.model.portal.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

/**
 * Created by georgeliu on 15/1/14.
 */
public class ISO8601TimeSerializer extends StdScalarSerializer<DateTime> {
    public ISO8601TimeSerializer() {
        super(DateTime.class);
    }

    @Override
    public void serialize(DateTime value,
                          JsonGenerator jgen,
                          SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeString(toISOString(value));
    }

    public static String toISOString(DateTime t) {
        if (t == null) {
            return "";
        }
        return ISODateTimeFormat.dateTimeNoMillis().withZoneUTC().print(t);
    }
}