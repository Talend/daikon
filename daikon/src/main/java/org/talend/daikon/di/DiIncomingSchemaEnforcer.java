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
package org.talend.daikon.di;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.di.Transformer.BigDecimalTransformer;
import org.talend.daikon.di.Transformer.BooleanTransformer;
import org.talend.daikon.di.Transformer.BytesTransformer;
import org.talend.daikon.di.Transformer.DoubleTransformer;
import org.talend.daikon.di.Transformer.EmptyTransformer;
import org.talend.daikon.di.Transformer.FloatTransformer;
import org.talend.daikon.di.Transformer.IntTransformer;
import org.talend.daikon.di.Transformer.LongTransformer;
import org.talend.daikon.di.Transformer.DateTimeTransformer;
import org.talend.daikon.di.Transformer.StringTransformer;

/**
 * <b>You should almost certainly not be using this class.</b>
 * 
 * This class acts as a wrapper around arbitrary values to coerce the Talend 6 Studio types in a generated POJO to a
 * {@link IndexedRecord} object that can be processed in the next component..
 * <p>
 * A wrapper like this should be attached before an output component, for example, to ensure that its incoming data with
 * the constraints imposed by the Studio meet the contract of the component framework, for example:
 * <ul>
 * <li>Coercing the types of the Talend POJO objects to expected Avro schema types.</li>
 * <li>Unwrapping data in a routines.system.Dynamic column into flat fields.</li>
 * </ul>
 * <p>
 * One instance of this object can be created per incoming schema and reused.
 */
public class DiIncomingSchemaEnforcer implements DiSchemaConstants {

    /**
     * The design-time schema from the Studio that determines how incoming java column data will be interpreted.
     */
    private final Schema incomingDesignSchema;

    /**
     * The position of the dynamic column in the incoming schema. This is -1 if there is no dynamic column. There can be
     * a maximum of one dynamic column in the schema.
     */
    private final int incomingDynamicColumn;

    /**
     * The {@link Schema} of the actual runtime data that will be provided by this object. This will only be null if
     * dynamic columns exist, but they have not been finished initializing.
     */
    private Schema incomingRuntimeSchema;

    /** The fields constructed from dynamic columns. This will only be non-null during construction. */
    private List<Schema.Field> fieldsFromDynamicColumns = null;

    /** The values wrapped by this object. */
    private GenericData.Record wrapped = null;

    /**
     * Access the indexed fields by their name. We should prefer accessing them by index for performance, but this
     * complicates the logic of dynamic columns quite a bit.
     */
    private final Map<String, Integer> columnToFieldIndex = new HashMap<>();

    private Transformer[] transformers;

    // TODO(rskraba): fix with a general type conversion strategy.
    private final Map<String, SimpleDateFormat> dateFormatCache = new HashMap<>();

    public DiIncomingSchemaEnforcer(Schema incoming) {
        this.incomingDesignSchema = incoming;
        this.incomingRuntimeSchema = incoming;

        // Find the dynamic column, if any.
        incomingDynamicColumn = AvroUtils.isIncludeAllFields(incoming)
                ? Integer.valueOf(incoming.getProp(DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION)) : -1;
        if (incomingDynamicColumn != -1) {
            incomingRuntimeSchema = null;
            fieldsFromDynamicColumns = new ArrayList<>();
        }

        // Add all of the runtime columns except any dynamic column to the index map.
        for (Schema.Field f : incoming.getFields()) {
            if (f.pos() != incomingDynamicColumn) {
                columnToFieldIndex.put(f.name(), f.pos());
            }
        }
    }

    /**
     * Take all of the parameters from the dynamic metadata and adapt it to a field for the runtime Schema.
     */
    public void initDynamicColumn(String name, String dbName, String type, String dbType, int dbTypeId, int length, int precision,
            String format, String description, boolean isKey, boolean isNullable, String refFieldName, String refModuleName) {
        if (!needsInitDynamicColumns())
            return;

        // Add each column to the field index and the incoming runtime schema.
        // TODO(rskraba): validate all types coming from the studio and add annotations.
        Schema fieldSchema = null;
        if ("id_String".equals(type)) {
            fieldSchema = Schema.create(Schema.Type.STRING);
        } else if ("id_Boolean".equals(type)) {
            fieldSchema = Schema.create(Schema.Type.BOOLEAN);
        } else if ("id_Integer".equals(type)) {
            fieldSchema = Schema.create(Schema.Type.INT);
        } else if ("id_Long".equals(type)) {
            fieldSchema = Schema.create(Schema.Type.LONG);
        } else if ("id_Double".equals(type)) {
            fieldSchema = Schema.create(Schema.Type.DOUBLE);
        } else if ("id_Float".equals(type)) {
            fieldSchema = Schema.create(Schema.Type.FLOAT);
        } else if ("id_Byte".equals(type)) {
            fieldSchema = AvroUtils._byte();
        } else if ("id_Short".equals(type)) {
            fieldSchema = AvroUtils._short();
        } else if ("id_Character".equals(type)) {
            fieldSchema = AvroUtils._character();
        } else if ("id_BigDecimal".equals(type)) {
            fieldSchema = AvroUtils._decimal();
        } else if ("id_Date".equals(type)) {
            fieldSchema = AvroUtils._date();
        } else {
            throw new UnsupportedOperationException("Unrecognized type " + type);
        }

        if (isNullable) {
            fieldSchema = SchemaBuilder.nullable().type(fieldSchema);
        }
        Schema.Field field = new Schema.Field(name, fieldSchema, description, (Object) null);
        // Set pattern for date type
        if ("id_Date".equals(type) && format != null) {
            field.addProp(SchemaConstants.TALEND_COLUMN_PATTERN, format);
        }
        fieldsFromDynamicColumns.add(field);
    }

    /**
     * Called when dynamic columns have finished being initialized. After this call, the {@link #getDesignSchema()} can
     * be used to get the runtime schema.
     */
    public void initDynamicColumnsFinished() {
        if (!needsInitDynamicColumns())
            return;

        // Copy all of the fields that were initialized from dynamic columns into the runtime Schema.
        boolean dynamicFieldsAdded = false;
        List<Schema.Field> fields = new ArrayList<Schema.Field>();
        for (Schema.Field designField : incomingDesignSchema.getFields()) {
            // Replace the dynamic column by all of its contents.
            if (designField.pos() == incomingDynamicColumn) {
                fields.addAll(fieldsFromDynamicColumns);
                dynamicFieldsAdded = true;
            }
            // Make a complete copy of the field (it can't be reused).
            Schema.Field designFieldCopy = new Schema.Field(designField.name(), designField.schema(), designField.doc(),
                    designField.defaultVal());
            for (Map.Entry<String, Object> e : designField.getObjectProps().entrySet()) {
                designFieldCopy.addProp(e.getKey(), e.getValue());
            }
            fields.add(designFieldCopy);
        }
        if (!dynamicFieldsAdded) {
            fields.addAll(fieldsFromDynamicColumns);
        }

        incomingRuntimeSchema = Schema.createRecord(incomingDesignSchema.getName(), incomingDesignSchema.getDoc(),
                incomingDesignSchema.getNamespace(), incomingDesignSchema.isError());
        incomingRuntimeSchema.setFields(fields);

        // Map all of the fields from the runtime Schema to their index.
        for (Schema.Field f : incomingRuntimeSchema.getFields()) {
            columnToFieldIndex.put(f.name(), f.pos());
        }

        // And indicate that initialization is finished.
        fieldsFromDynamicColumns = null;
    }

    /**
     * @return true only if there is a dynamic column and they haven't been finished initializing yet. When this returns
     * true, the enforcer can't be used yet and {@link #getDesignSchema()} is guaranteed to return null.
     */
    public boolean needsInitDynamicColumns() {
        return fieldsFromDynamicColumns != null;
    }

    public Schema getRuntimeSchema() {
        return incomingRuntimeSchema;
    }

    public Schema getDesignSchema() {
        return incomingDesignSchema;
    }

    public void put(String name, Object v) {
        put(columnToFieldIndex.get(name), v);
    }

    public void put(int i, Object v) {
        if (wrapped == null)
            wrapped = new GenericData.Record(getRuntimeSchema());

        if (v == null) {
            wrapped.put(i, null);
            return;
        }

        if (transformers == null) {
            List<Field> fields = getRuntimeSchema().getFields();
            transformers = new Transformer[fields.size()];
            for (int j = 0; j < fields.size(); j++) {
                transformers[j] = createTransformer(fields.get(j));
            }
        }
        wrapped.put(i, transformers[i].transform(v));
    }

    private Transformer createTransformer(Schema.Field f) {
        // TODO(rskraba): check type validation for correctness with studio objects.
        Schema fieldSchema = AvroUtils.unwrapIfNullable(f.schema());

        Transformer transformer = new EmptyTransformer();
        boolean isLogicalDate = false;
        LogicalType logicalType = fieldSchema.getLogicalType();
        if (logicalType != null) {
            if (logicalType == LogicalTypes.date() || logicalType == LogicalTypes.timestampMillis()) {
                isLogicalDate = true;
            }
        }

        // TODO(rskraba): This is pretty rough -- fix with a general type conversion strategy.
        String talendType = f.getProp(DiSchemaConstants.TALEND6_COLUMN_TALEND_TYPE);
        String javaClass = fieldSchema.getProp(SchemaConstants.JAVA_CLASS_FLAG);
        if (isLogicalDate || "id_Date".equals(talendType) || "java.util.Date".equals(javaClass)) {
            transformer = new DateTimeTransformer(f.getProp(DiSchemaConstants.TALEND6_COLUMN_PATTERN), dateFormatCache);
        }

        if ("id_BigDecimal".equals(talendType) || "java.math.BigDecimal".equals(javaClass)) {
            transformer = new BigDecimalTransformer();
        }

        if (transformer instanceof EmptyTransformer) {
            switch (fieldSchema.getType()) {
            case BOOLEAN:
                transformer = new BooleanTransformer();
                break;
            case FIXED:
            case BYTES:
                transformer = new BytesTransformer();
                break;
            case DOUBLE:
                transformer = new DoubleTransformer();
                break;
            case FLOAT:
                transformer = new FloatTransformer();
                break;
            case INT:
                transformer = new IntTransformer();
                break;
            case LONG:
                transformer = new LongTransformer();
                break;
            case STRING:
                transformer = new StringTransformer();
                break;
            case ARRAY:
            case ENUM:
            case MAP:
            case NULL:
            case RECORD:
            case UNION:
            default:
                break;
            }
        }

        return transformer;
    }

    /**
     * @return An IndexedRecord created from the values stored in this enforcer and clears out any existing values.
     */
    public IndexedRecord createIndexedRecord() {
        // Send the data to a new instance of IndexedRecord and clear out the existing values.
        IndexedRecord copy = wrapped;
        wrapped = null;
        return copy;
    }
}