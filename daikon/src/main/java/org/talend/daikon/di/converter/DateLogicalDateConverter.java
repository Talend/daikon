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
 * Converts DI Date to Avro logical Date and vice versa
 */
public class DateLogicalDateConverter implements AvroConverter<Date, Integer> {

    private static final Schema LOGICAL_DATE_SCHEMA = AvroUtils._logicalDate();

    /**
     * The number of milliseconds in one day
     */
    private static final long ONE_DAY = 1000 * 60 * 60 * 24;

    /**
     * Returns schema of Avro logical date type
     */
    @Override
    public Schema getSchema() {
        return LOGICAL_DATE_SCHEMA;
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
     * Converts int, which is number days from unix epoch, to timestamp with millisecond precision
     */
    @Override
    public Date convertToDatum(Integer value) {
        return new Date(value * ONE_DAY);
    }

    /**
     * Converts timestamp ({@link Date}) with millisecond precision to int value, which is number of days from unix epoch
     */
    @Override
    public Integer convertToAvro(Date value) {
        return (int) (value.getTime() / ONE_DAY);
    }

}
