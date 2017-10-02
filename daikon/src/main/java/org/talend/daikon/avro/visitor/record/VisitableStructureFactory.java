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
package org.talend.daikon.avro.visitor.record;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.util.Utf8;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.visitor.path.TraversalPath;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * Internal utility class to create visitable structures.
 */
abstract class VisitableStructureFactory {

    private VisitableStructureFactory() {
        throw new UnsupportedOperationException("Cannot be instantiated");
    }

    static VisitableStructure createVisitableField(Schema.Field field, IndexedRecord indexedRecord, TraversalPath path) {
        final Schema schema = field.schema();
        final Object value = indexedRecord.get(field.pos());
        return createVisitableStructure(schema, value, path);
    }

    static VisitableStructure createVisitableStructure(Schema schema, Object value, TraversalPath path) {
        Schema unwrappedSchema = AvroUtils.unwrapIfNullable(schema);
        switch (unwrappedSchema.getType()) {
        case ARRAY:
            return new VisitableArray(ensureArray((List) value, unwrappedSchema), path);
        default:
            return createWrapperForType(unwrappedSchema.getType(), value, path);
        }
    }

    static private VisitableStructure createWrapperForType(Schema.Type type, Object value, TraversalPath path) {
        switch (type) {
        case INT:
            return new VisitableInt((Integer) value, path);
        case LONG:
            return new VisitableLong((Long) value, path);
        case RECORD:
            return new VisitableRecord((IndexedRecord) value, path);
        case MAP:
            return new VisitableMap((Map<Utf8, Object>) value, path);
        case STRING:
            return new VisitableString(value.toString(), path);
        case BOOLEAN:
            return new VisitableBoolean((Boolean) value, path);
        case FLOAT:
            return new VisitableFloat((Float) value, path);
        case DOUBLE:
            return new VisitableDouble((Double) value, path);
        case NULL:
            return new VisitableNull(path);
        case ENUM:
            return new VisitableString(value.toString(), path);
        case FIXED:
            return new VisitableFixed((GenericData.Fixed) value, path);
        case BYTES:
            return new VisitableBytes((ByteBuffer) value, path);
        // note: UNION is not supported yet
        default:
            throw new IllegalArgumentException("Unsupported Avro data type: " + type);
        }
    }

    /**
     * Useful because Avro does not ensure arrays are represented by an instance
     * of {@link GenericData.Array}.
     *
     * @param list a list
     * @param schema the schema of the array
     * @return an instance of {@link GenericData.Array} representing the provided list.
     */
    static private GenericData.Array ensureArray(List list, Schema schema) {
        if (list instanceof GenericData.Array) {
            return (GenericData.Array) list;
        }
        return new GenericData.Array(schema, list);
    }

}
