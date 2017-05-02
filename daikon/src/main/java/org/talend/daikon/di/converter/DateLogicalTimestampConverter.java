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
package org.talend.daikon.di.converter;

import java.util.Date;

import org.apache.avro.Schema;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.converter.AvroConverter;

/**
 * Converts DI Date to Avro logical Timestamps millis and vice versa
 */
public class DateLogicalTimestampConverter implements AvroConverter<Date, Long> {

    private static final Schema LOGICAL_TIMESTAMP_SCHEMA = AvroUtils._logicalTimestamp();

    /**
     * Returns schema of Avro logical timestamp type
     */
    @Override
    public Schema getSchema() {
        return LOGICAL_TIMESTAMP_SCHEMA;
    }

    /**
     * Returns {@link Date} class, which is type of DI data
     * 
     * @return {@link Date} class
     */
    @Override
    public Class<Date> getDatumClass() {
        return Date.class;
    }

    /**
     * Converts long, which is a number of milliseconds from unix epoch, to timestamp with millisecond precision
     */
    @Override
    public Date convertToDatum(Long value) {
        return new Date(value);
    }

    /**
     * Converts timestamp ({@link Date}) with millisecond precision to long value, which is a number of milliseconds from unix
     * epoch
     */
    @Override
    public Long convertToAvro(Date value) {
        return value.getTime();
    }

}
