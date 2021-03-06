/*
 * ******************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ******************************************************
 */

package com.vmware.vchs.gateway.model.converter;

import org.joda.time.DateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Date;

/**
 * Converte Date type to joda DateTime
 */
@Converter
public class DateToDateTimeConverter implements AttributeConverter<DateTime, Date> {
    @Override
    public Date convertToDatabaseColumn(DateTime attribute) {
        return attribute.toDate();
    }

    @Override
    public DateTime convertToEntityAttribute(Date dbData) {
        return new DateTime(dbData);
    }
}