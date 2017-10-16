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
 * Wrapper implementation for null
 */
public class VisitableNull extends AbstractVisitableStructure<Void> {

    VisitableNull(TraversalPath path) {
        super(null, path);
    }

    @Override
    public void accept(RecordVisitor visitor) {
        visitor.visit(this);
    }
}
