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
 * Abstract base implementation of {@link VisitableStructure}.
 *
 * This implementation is immutable, constructor's arguments are final members.
 *
 * @param <T> the inner type of value
 */
abstract class AbstractVisitableStructure<T> implements VisitableStructure<T> {

    private final T value;

    private final TraversalPath path;

    protected AbstractVisitableStructure(T value, TraversalPath path) {
        this.value = value;
        this.path = path;
    }

    @Override
    public final T getValue() {
        return value;
    }

    @Override
    public TraversalPath getPath() {
        return path;
    }
}
