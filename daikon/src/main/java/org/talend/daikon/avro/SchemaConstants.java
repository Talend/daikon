package org.talend.daikon.avro;

/**
 * Constants to be used to augment the Avro schema
 */
public class SchemaConstants {

    /**
     * A dynamic schema field. The Avro type for this is Type.BYTES
     */
    public static final String LOGICAL_DYNAMIC = "dynamic";

    public final static String TALEND_COLUMN_DB_TYPE = "talend.column.sourceType"; //$NON-NLS-1$

    public final static String TALEND_COLUMN_PATTERN = "talend.column.pattern"; //$NON-NLS-1$

    public final static String TALEND_COLUMN_DB_COLUMN_NAME = "talend.column.dbColumnName"; //$NON-NLS-1$

    /** String representation of an int. */
    public final static String TALEND_COLUMN_DB_LENGTH = "talend.column.length"; //$NON-NLS-1$

    /** String representation of an int. */
    public final static String TALEND_COLUMN_PRECISION = "talend.column.precision"; //$NON-NLS-1$

    public final static String TALEND_COLUMN_SCALE = "talend.column.scale"; //$NON-NLS-1$

    public final static String TALEND_COLUMN_DEFAULT = "talend.column.default"; //$NON-NLS-1$
}
