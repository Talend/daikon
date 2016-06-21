package org.talend.daikon.avro;

/**
 * Constants to be used to augment the Avro schema.
 */
public class SchemaConstants {

    /**
     * If Schema.Type can't represent this java type, then use JAVA_CLASS_FLAG as the property key and the real class
     * name as the value
     */
    public static final String JAVA_CLASS_FLAG = "java-class";

    /**
     * If a schema is used as an input specification, and the record includes this property (with ANY value), then the
     * actual schema return should be expanded to include all possible fields that the input component can find.
     */
    public static final String INCLUDE_ALL_FIELDS = "include-all-fields"; //$NON-NLS-1$

    /**
     * When a component provides a Schema to be used, it can normally still be modified at design-time.
     * 
     * If a record Schema has this property, no fields can be added, reordered or removed or modified at design-time.
     * 
     * If a field has this property, it can't be modified at design-time.
     * 
     * A record Schema can be locked, but individual fields unlocked by setting this value to false.
     */
    public final static String TALEND_IS_LOCKED = "talend.isLocked"; //$NON-NLS-1$

    /**
     * It denotes a field, which is generated and filled by component itself.
     * Such field has no meaning for user, but is required by component for some purpose.
     * One of usages is to mark additional field passed to reject flow.
     */
    public final static String TALEND_FIELD_GENERATED = "talend.field.generated"; //$NON-NLS-1$

    public final static String TALEND_COLUMN_DB_COLUMN_NAME = "talend.field.dbColumnName"; //$NON-NLS-1$

    public final static String TALEND_COLUMN_DB_TYPE = "talend.field.dbType"; //$NON-NLS-1$

    public final static String TALEND_COLUMN_PATTERN = "talend.field.pattern"; //$NON-NLS-1$

    /**
     * String representation of an int.
     */
    public final static String TALEND_COLUMN_DB_LENGTH = "talend.field.length"; //$NON-NLS-1$

    /**
     * String representation of an int.
     */
    public final static String TALEND_COLUMN_PRECISION = "talend.field.precision"; //$NON-NLS-1$

    public final static String TALEND_COLUMN_SCALE = "talend.field.scale"; //$NON-NLS-1$

    public final static String TALEND_COLUMN_DEFAULT = "talend.field.default"; //$NON-NLS-1$

    /**
     * Use this property to indicate the column is a key, normally when the output component has ability to create
     * table, should need this property
     */
    public final static String TALEND_COLUMN_IS_KEY = "talend.field.isKey"; //$NON-NLS-1$
}
