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

/**
 * An Avro record visitor
 *
 * To visit an Avro generic record, simply proceed as follows:
 *
 * <pre>
 *     {@code
 *     IndexedRecord yourRecord = ...;
 *     RecordVisitor yourVisitor = ...;
 *     new VisitableRecord(yourRecord).accept(yourVisitor);
 *     }
 * </pre>
 *
 * Your visitor implementation will be called while traversing the record structure.
 *
 */
public interface RecordVisitor {

    /**
     * Visits a record.
     *
     * To continue the visit and iterate over the fields of this record, use
     * {@link VisitableRecord#getFields()}
     *
     * <pre>
     *     {@code
     *     void visit(VisitableRecord record) {
     *         Iterator<VisitableStructure> fields = record.getFields();
     *         while(fields.hasNext()) {
     *             fields.next().accept(this);
     *         }
     *     }
     *     }
     * </pre>
     *
     * @param record the record to visit
     */
    void visit(VisitableRecord record);

    /**
     * Visits an array
     *
     * To continue the visit and iterate over the items of this array, use
     * {@link VisitableArray#getItems(VisitableArray.ArrayItemsPathType)}.
     *
     * <pre>
     *     {@code
     *     void visit(VisitableArray array) {
     *        Iterator<VisitableStructure> items = array.getItems(VisitableArray.ArrayItemsPathType.INDEXED);
     *        while(items.hasNext()) {
     *           items.next().accept(this);
     *        }
     *     }
     *     }
     * </pre>
     *
     * @param array the array to visit
     */
    void visit(VisitableArray array);

    /**
     * Visits a map.
     *
     * To continue the visit, iterate over the entries of this map, use
     * {@link VisitableMap#getValues()}
     *
     * <pre>
     *     {@code
     *     void visit(VisitableMap map) {
     *        Iterator<VisitableStructure> entries = map.getEntries();
     *        while(entries.hasNext()) {
     *           entries.next().accept(this);
     *        }
     *     }
     *     }
     * </pre>
     *
     * @param map the map to visit
     */
    void visit(VisitableMap map);

    /**
     * visits an integer field
     * 
     * @param field the field to visit
     */
    void visit(VisitableInt field);

    /**
     * visits a long field
     * 
     * @param field the field to visit
     */
    void visit(VisitableLong field);

    /**
     * visits a string field
     * 
     * @param field the field to visit
     */
    void visit(VisitableString field);

    /**
     * visits a boolean field
     * 
     * @param field the field to visit
     */
    void visit(VisitableBoolean field);

    /**
     * visits a float field
     * 
     * @param field the field to visit
     */
    void visit(VisitableFloat field);

    /**
     * visits a double field
     * 
     * @param field the field to visit
     */
    void visit(VisitableDouble field);

    /**
     * visits a null field
     * 
     * @param field the field to visit
     */
    void visit(VisitableNull field);

    /**
     * visits a fixed field
     * 
     * @param field the field to visit
     */
    void visit(VisitableFixed field);

    /**
     * visits a bytes field
     * 
     * @param field the field to visit
     */
    void visit(VisitableBytes field);

}
