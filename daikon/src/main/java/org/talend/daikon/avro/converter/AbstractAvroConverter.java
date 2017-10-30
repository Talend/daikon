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
package org.talend.daikon.avro.converter;

import org.apache.avro.Schema;

/**
 * Abstract implementation of {@link AvroConverter}, which implements common methods
 */
public abstract class AbstractAvroConverter<DatumT, AvroT> implements AvroConverter<DatumT, AvroT> {

    /**
     * Class of DI data
     */
    private final Class<DatumT> clazz;

    /**
     * Schema of Avro data
     */
    private final Schema schema;

    /**
     * Sets Avro {@link Schema} and DI {@link Class} of data
     * 
     * @param clazz type of DI data
     * @param schema schema of a Avro data
     */
    public AbstractAvroConverter(Class<DatumT> clazz, Schema schema) {
        this.clazz = clazz;
        this.schema = schema;
    }

    /**
     * Returns {@link Class} of DI data
     */
    @Override
    public Class<DatumT> getDatumClass() {
        return this.clazz;
    }

    /**
     * Returns {@link Schema} of Avro data
     */
    @Override
    public Schema getSchema() {
        return this.schema;
    }
}
