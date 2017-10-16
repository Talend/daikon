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

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.avro.visitor.path.TraversalPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Main entry point of the Avro visitable API.
 *
 * To visit an IndexedRecord and its fields, implement the {@link RecordVisitor} interface,
 * instantiate an new VisitableRecord and execute its {@link #accept(RecordVisitor)} method
 */
public class VisitableRecord extends AbstractVisitableStructure<IndexedRecord> {

    /**
     * Create a new visitable record from an existing Avro {@link IndexedRecord}
     * 
     * @param record the record to visit
     */
    public VisitableRecord(IndexedRecord record) {
        this(record, TraversalPath.create(record.getSchema()));
    }

    VisitableRecord(IndexedRecord value, TraversalPath path) {
        super(value, path);
    }

    @Override
    public void accept(RecordVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * @return an iterator over this record's field
     */
    public Iterator<VisitableStructure> getFields() {
        final Schema schema = this.getValue().getSchema();
        final List<VisitableStructure> fields = new ArrayList<>(schema.getFields().size());
        for (Schema.Field field : schema.getFields()) {
            final TraversalPath fieldPath = this.getPath().append(field.name(), field.pos(), field.schema());
            final VisitableStructure node = VisitableStructureFactory.createVisitableField(field, getValue(), fieldPath);
            fields.add(node);
        }
        return Collections.unmodifiableList(fields).iterator();
    }
}
