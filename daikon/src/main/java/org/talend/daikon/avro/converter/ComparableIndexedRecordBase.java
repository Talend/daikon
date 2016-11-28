// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;

/**
 * A base for {@link IndexedRecord} implementations that respect the Java {@link Object} contracts for {@link #equals},
 * {@link #hashCode} and {@link #compareTo}.
 */
public abstract class ComparableIndexedRecordBase implements IndexedRecord, Comparable<IndexedRecord> {

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof IndexedRecord))
            return false;
        IndexedRecord that = (IndexedRecord) o;
        if (!this.getSchema().equals(that.getSchema()))
            return false;
        // TODO(rskraba): there should be an better&faster compare for Avro!
        return GenericData.get().compare(this, that, getSchema()) == 0;
    }

    @Override
    public int hashCode() {
        return GenericData.get().hashCode(this, getSchema());
    }

    @Override
    public int compareTo(IndexedRecord that) {
        return GenericData.get().compare(this, that, getSchema());
    }

    @Override
    public String toString() {
        return GenericData.get().toString(this);
    }
}