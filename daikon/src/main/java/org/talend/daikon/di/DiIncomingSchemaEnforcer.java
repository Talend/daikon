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

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.avro.AvroUtils;
import org.talend.daikon.avro.SchemaConstants;

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
public class DiIncomingSchemaEnforcer {

    /**
     * Dynamic column position possible value, which means schema doesn't have dynamic column
     */
    private static final int NO_DYNAMIC_COLUMN = -1;

    /**
     * The design-time schema from the Studio that determines how incoming java column data will be interpreted.
     * This schema is retrieved from downstream component's properties
     */
    private final Schema designSchema;

    /**
     * The position of the dynamic column in the incoming schema. This is -1 if there is no dynamic column. There can be
     * a maximum of one dynamic column in the schema.
     */
    private final int dynamicColumnPosition;

    /**
     * The {@link Schema} of the actual runtime data that will be provided by this object. This will only be null if
     * dynamic columns exist, but they have not been finished initializing.
     */
    private Schema runtimeSchema;

    /**
     * Collection of fields constructed from dynamic columns. This will only be non-null during construction.
     */
    private List<Schema.Field> dynamicFields = null;

    /**
     * The values wrapped by this object - current {@link IndexedRecord}
     */
    private GenericData.Record currentRecord = null;

    /**
     * Access the indexed fields by their name. We should prefer accessing them by index for performance, but this
     * complicates the logic of dynamic columns quite a bit.
     */
    private final Map<String, Integer> columnToFieldIndex = new HashMap<>();

    // TODO(rskraba): fix with a general type conversion strategy.
    private final Map<String, SimpleDateFormat> dateFormatCache = new HashMap<>();

    /**
     * Constructor
     * 
     * @param incoming design schema retrieved from downstream component properties
     */
    public DiIncomingSchemaEnforcer(Schema incoming) {
        designSchema = incoming;

        // Find the dynamic column, if any.
        dynamicColumnPosition = AvroUtils.isIncludeAllFields(designSchema)
                ? Integer.valueOf(designSchema.getProp(DiSchemaConstants.TALEND6_DYNAMIC_COLUMN_POSITION)) : NO_DYNAMIC_COLUMN;
        if (dynamicColumnPosition != NO_DYNAMIC_COLUMN) {
            runtimeSchema = null;
            dynamicFields = new ArrayList<>();
        } else {
            runtimeSchema = designSchema;
        }

        // Add all of the runtime columns except any dynamic column to the index map.
        for (Schema.Field f : designSchema.getFields()) {
            if (f.pos() != dynamicColumnPosition) {
                columnToFieldIndex.put(f.name(), f.pos());
            }
        }
    }

    /**
     * Take all of the parameters from the dynamic metadata and adapt it to a field for the runtime Schema.
     * 
     * @deprecated because it was renamed. Use {@link this#addDynamicField(String, String, String, String, boolean)} instead
     */
    @Deprecated
    public void initDynamicColumn(String name, String dbName, String type, String dbType, int dbTypeId, int length, int precision,
            String format, String description, boolean isKey, boolean isNullable, String refFieldName, String refModuleName) {
        addDynamicField(name, type, format, description, isNullable);
    }

    /**
     * Recreates dynamic field from parameters retrieved from DI dynamic metadata
     * 
     * @param name dynamic field name
     * @param type dynamic field type
     * @param format dynamic field date format
     * @param description dynamic field description
     * @param isNullable defines whether dynamic field may contain <code>null</code> value
     */
    public void addDynamicField(String name, String type, String format, String description, boolean isNullable) {
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
        dynamicFields.add(field);
    }

    /**
     * Called when dynamic columns have finished being initialized. After this call, the {@link #getDesignSchema()} can be
     * used to get the runtime schema.
     * 
     * @deprecated because it was renamed. Use {@link this#recreateRuntimeSchema()} instead
     */
    @Deprecated
    public void initDynamicColumnsFinished() {
        recreateRuntimeSchema();
    }

    /**
     * Recreates runtime schema from design schema and dynamic fields.
     * Design schema is set in Constructor during enforcer initialization.
     * Dynamic fields are recreated by calling {@link this#addDynamicField()} methods.
     * This method should be called only after all dynamic fields are recreated.
     * Also should be called before calling {@link this#put()} and {@link this#createIndexedRecord()} methods
     */
    public void recreateRuntimeSchema() {
        if (!needsInitDynamicColumns())
            return;

        // Copy all of the fields that were initialized from dynamic columns into the runtime Schema.
        boolean dynamicFieldsAdded = false;
        List<Schema.Field> fields = new ArrayList<Schema.Field>();
        for (Schema.Field designField : designSchema.getFields()) {
            // Replace the dynamic column by all of its contents.
            if (designField.pos() == dynamicColumnPosition) {
                fields.addAll(dynamicFields);
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
            fields.addAll(dynamicFields);
        }

        runtimeSchema = Schema.createRecord(designSchema.getName(), designSchema.getDoc(), designSchema.getNamespace(),
                designSchema.isError());
        runtimeSchema.setFields(fields);

        // Map all of the fields from the runtime Schema to their index.
        for (Schema.Field f : runtimeSchema.getFields()) {
            columnToFieldIndex.put(f.name(), f.pos());
        }

        // And indicate that initialization is finished.
        dynamicFields = null;
    }

    /**
     * @deprecated because it was renamed. Use {@link this#areDynamicFieldsInitialized()} instead
     * @return true only if there is a dynamic column and they haven't been finished initializing yet. When this returns
     * true, the enforcer can't be used yet and {@link #getDesignSchema()} is guaranteed to return null.
     */
    @Deprecated
    public boolean needsInitDynamicColumns() {
        return areDynamicFieldsInitialized();
    }

    /**
     * Checks whether dynamic fields were already initialized.
     * Dynamic fields are initialized using parameters from the first incoming data object.
     * Thus, this method returns <code>false</code>, if dynamic fields were not initialized yet (before first data object).
     * It returns <code>true</code>, if dynamic fields were initialized (after first data object)
     * 
     * @return true, if dynamic fields were initialized; false - otherwise
     */
    public boolean areDynamicFieldsInitialized() {
        return dynamicFields != null;
    }

    /**
     * Return runtime schema
     * 
     * @return runtime schema
     */
    public Schema getRuntimeSchema() {
        return runtimeSchema;
    }

    /**
     * Returns design schema
     * 
     * @return design schema
     */
    public Schema getDesignSchema() {
        return designSchema;
    }

    /**
     * Put record value by field name
     * 
     * @param name field name
     * @param value data value
     */
    public void put(String name, Object value) {
        put(columnToFieldIndex.get(name), value);
    }

    /**
     * Put record value by field index
     * 
     * @param index field index to put in
     * @param value data value
     */
    public void put(int index, Object value) {
        if (currentRecord == null) {
            createNewRecord();
        }

        if (value == null) {
            currentRecord.put(index, null);
            return;
        }

        // TODO(rskraba): check type validation for correctness with studio objects.
        Schema.Field f = runtimeSchema.getFields().get(index);
        Schema fieldSchema = AvroUtils.unwrapIfNullable(f.schema());

        Object datum = null;

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
            if (value instanceof Date) {
                datum = value;
            } else if (value instanceof Long) {
                datum = new Date((long) value);
            } else if (value instanceof String) {
                String pattern = f.getProp(DiSchemaConstants.TALEND6_COLUMN_PATTERN);
                String vs = (String) value;

                if (pattern == null || pattern.equals("yyyy-MM-dd'T'HH:mm:ss'000Z'")) {
                    if (!vs.endsWith("000Z")) {
                        throw new RuntimeException("Unparseable date: \"" + vs + "\""); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    pattern = "yyyy-MM-dd'T'HH:mm:ss";
                }

                SimpleDateFormat df = dateFormatCache.get(pattern);
                if (df == null) {
                    df = new SimpleDateFormat(pattern);
                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
                    dateFormatCache.put(pattern, df);
                }

                try {
                    datum = df.parse((String) value);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if ("id_BigDecimal".equals(talendType) || "java.math.BigDecimal".equals(javaClass)) {
            if (value instanceof BigDecimal) {
                datum = value;
            } else if (value instanceof String) {
                datum = new BigDecimal((String) value);
            }
        }

        if (datum == null) {
            switch (fieldSchema.getType()) {
            case ARRAY:
                break;
            case BOOLEAN:
                if (value instanceof Boolean)
                    datum = value;
                else
                    datum = Boolean.valueOf(String.valueOf(value));
                break;
            case FIXED:
            case BYTES:
                if (value instanceof byte[])
                    datum = value;
                else
                    datum = String.valueOf(value).getBytes();
                break;
            case DOUBLE:
                if (value instanceof Number)
                    datum = ((Number) value).doubleValue();
                else
                    datum = Double.valueOf(String.valueOf(value));
                break;
            case ENUM:
                break;
            case FLOAT:
                if (value instanceof Number)
                    datum = ((Number) value).floatValue();
                else
                    datum = Float.valueOf(String.valueOf(value));
                break;
            case INT:
                if (value instanceof Number)
                    datum = ((Number) value).intValue();
                else
                    datum = Integer.valueOf(String.valueOf(value));
                break;
            case LONG:
                if (value instanceof Number)
                    datum = ((Number) value).longValue();
                else
                    datum = Long.valueOf(String.valueOf(value));
                break;
            case MAP:
                break;
            case NULL:
                datum = null;
                break;
            case RECORD:
                break;
            case STRING:
                datum = String.valueOf(value);
                break;
            case UNION:
                break;
            default:
                break;
            }
        }

        currentRecord.put(index, datum);
    }

    /**
     * @return An IndexedRecord created from the values stored in this enforcer and clears out any existing values.
     */
    public IndexedRecord createIndexedRecord() {
        // Send the data to a new instance of IndexedRecord and clear out the existing values.
        IndexedRecord copy = currentRecord;
        currentRecord = null;
        return copy;
    }

    /**
     * Returns current {@link IndexedRecord}
     * 
     * @return current {@link IndexedRecord}
     */
    public IndexedRecord getCurrentRecord() {
        return currentRecord;
    }

    /**
     * Creates new instance for {@link IndexedRecord}
     * This should be called before series of {@link this#put()} calls, which copies values from next DI data object into this
     * enforcer
     */
    public void createNewRecord() {
        currentRecord = new GenericData.Record(getRuntimeSchema());
    }

}