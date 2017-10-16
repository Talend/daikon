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

import org.apache.avro.generic.GenericData;
import org.talend.daikon.avro.visitor.path.TraversalPath;

/**
 * Wrapper implementation for fixed Avro type
 */
public class VisitableFixed extends AbstractVisitableStructure<GenericData.Fixed> {

    VisitableFixed(GenericData.Fixed value, TraversalPath path) {
        super(value, path);
    }

    @Override
    public void accept(RecordVisitor visitor) {
        visitor.visit(this);
    }
}
