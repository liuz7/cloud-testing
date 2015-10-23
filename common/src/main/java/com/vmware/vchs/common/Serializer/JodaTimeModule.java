/*
 * *****************************************************
 *  Copyright VMware, Inc. 2014.   All Rights Reserved.
 *  ******************************************************
 */

package com.vmware.vchs.common.Serializer;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.time.DateTime;

public class JodaTimeModule extends SimpleModule {
    public JodaTimeModule() {
        super();
        addSerializer(DateTime.class, new ISO8601TimeSerializer());
        addDeserializer(DateTime.class, new ISO8601TimeDeserializer());
    }
}
