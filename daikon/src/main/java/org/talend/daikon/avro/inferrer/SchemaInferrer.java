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
package org.talend.daikon.avro.inferrer;

import org.apache.avro.Schema;

/**
 * Infers (extracts) Avro {@link Schema} from specific data example (Datum)
 * It can be used to guess schema from the first data arrived to particular part of the system
 */
public interface SchemaInferrer<DatumT> {

    /**
     * Analyzes <code>datum<code> instance and creates Avro schema of this datum
     * 
     * @param datum specific data
     * @return Avro schema of datum
     */
    Schema inferSchema(DatumT datum);
}
