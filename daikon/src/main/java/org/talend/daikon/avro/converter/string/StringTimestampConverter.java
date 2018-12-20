// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.avro.converter.string;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.avro.Schema;
import org.talend.daikon.avro.AvroUtils;

/**
 * Converts String datum to avro logical type timestamp and vice versa
 */
public class StringTimestampConverter extends StringConverter<Long> {

    private static final Schema TIMESTAMP_SCHEMA = AvroUtils._logicalTimestamp();

    public static final String DEFAULT_PATTERN = "dd-MM-yyyy hh:mm:ss:SSS";

    private final DateFormat dateFormat;

    /**
     * Constructor sets default datePattern to be used during conversion from
     * string to timestamp
     */
    public StringTimestampConverter() {
        this(DEFAULT_PATTERN);
    }

    /**
     * Constructor sets <code>datePattern</code> to be used during conversion
     * from string to timestamp
     * 
     * @param datePattern
     * date pattern, e.g. "dd-MM-yyyy hh:mm:ss:SSS"
     */
    public StringTimestampConverter(String datePattern) {
        if (datePattern == null) {
            throw new IllegalArgumentException("Date pattern is missed");
        }
        dateFormat = new SimpleDateFormat(datePattern);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * Returns schema of timestamp avro logical type
     * 
     * @return schema of timestamp avro logical type
     */
    @Override
    public Schema getSchema() {
        return TIMESTAMP_SCHEMA;
    }

    @Override
    public String convertToDatum(Long value) {
        Date timestamp = new Date(value);
        return dateFormat.format(timestamp);
    }

    @Override
    public Long convertToAvro(String value) {
        try {
            Date timestamp = dateFormat.parse(value);
            return timestamp.getTime();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Input string doesn't match specified date pattern", e);
        }
    }

}
