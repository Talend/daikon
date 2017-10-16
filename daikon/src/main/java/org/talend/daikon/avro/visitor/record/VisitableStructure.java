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

import org.talend.daikon.avro.visitor.path.TraversalPath;

/**
 * Wrapper for an Avro record field - this is necessary to implement the visitor pattern
 * because Avro records are not visitable.
 *
 * Wrappers implementations are meant to be immutable
 *
 * @param <T> underlying value type
 */
public interface VisitableStructure<T> {

    /**
     * Visitable implementation
     * 
     * @param visitor the visitor
     */
    void accept(RecordVisitor visitor);

    /**
     * @return the actual value this field contains
     */
    T getValue();

    /**
     * @return the path of the field
     */
    TraversalPath getPath();

}
