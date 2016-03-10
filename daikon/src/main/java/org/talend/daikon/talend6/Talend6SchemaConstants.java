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
package org.talend.daikon.talend6;

import org.apache.avro.Schema;
import org.talend.daikon.avro.SchemaConstants;

/**
 * Constants that can be used as keys in an Avro {@link Schema} properties in order to remain feature-equivalent to the
 * existing Talend 6 IMetadataTable.
 * 
 * Values that are null are always omitted from the properties. The type of the value is always String.
 */
public interface Talend6SchemaConstants {

    /*
     * Can apply to table and columns. --------------------------------------
     */

    public final static String TALEND6_ID = "talend6.column.id"; //$NON-NLS-1$

    public final static String TALEND6_COMMENT = "talend6.table.comment"; //$NON-NLS-1$

    public final static String TALEND6_LABEL = "talend6.table.label"; //$NON-NLS-1$

    /** Property is present if readonly, otherwise not present. */
    public final static String TALEND6_IS_READ_ONLY = "talend6.table.readOnly"; //$NON-NLS-1$

    /** The key will have this as a prefix, pointing to the value. */
    public final static String TALEND6_ADDITIONAL_PROPERTIES = "talend6.prop."; //$NON-NLS-1$

    /*
     * Table custom properties. ---------------------------------------------
     */

    public final static String TALEND6_TABLE_NAME = "talend6.table.name"; //$NON-NLS-1$

    public final static String TALEND6_TABLE_TYPE = "talend6.table.type"; //$NON-NLS-1$

    /** Value is the label of the dynamic column. */
    public final static String TALEND6_TABLE_DYNAMIC_COLUMN = "talend6.table.dynamicColumn"; //$NON-NLS-1$

    /*
     * Column custom properties. --------------------------------------------
     */

    /** Property is present if key, otherwise not present. */
    public final static String TALEND6_COLUMN_IS_KEY = "talend6.column.isKey"; //$NON-NLS-1$

    public final static String TALEND6_COLUMN_SOURCE_TYPE = SchemaConstants.TALEND_COLUMN_DB_TYPE;

    public final static String TALEND6_COLUMN_TALEND_TYPE = "talend6.column.talendType"; //$NON-NLS-1$

    public final static String TALEND6_COLUMN_PATTERN = SchemaConstants.TALEND_COLUMN_PATTERN;

    /** String representation of an int. */
    public final static String TALEND6_COLUMN_LENGTH = SchemaConstants.TALEND_COLUMN_DB_LENGTH;

    /** String representation of an int. */
    public final static String TALEND6_COLUMN_ORIGINAL_LENGTH = "talend6.column.originalLength"; //$NON-NLS-1$

    /** Property is present if nullable, otherwise not present. */
    public final static String TALEND6_COLUMN_IS_NULLABLE = "talend6.column.isNullable"; //$NON-NLS-1$

    /** String representation of an int. */
    public final static String TALEND6_COLUMN_PRECISION = SchemaConstants.TALEND_COLUMN_PRECISION;

    public final static String TALEND6_COLUMN_DEFAULT = SchemaConstants.TALEND_COLUMN_DEFAULT;

    public final static String TALEND6_COLUMN_ORIGINAL_DB_COLUMN_NAME = SchemaConstants.TALEND_COLUMN_DB_COLUMN_NAME;

    public final static String TALEND6_COLUMN_RELATED_ENTITY = "talend6.column.relatedEntity"; //$NON-NLS-1$

    public final static String TALEND6_COLUMN_RELATIONSHIP_TYPE = "talend6.column.relationshipType"; //$NON-NLS-1$

}
