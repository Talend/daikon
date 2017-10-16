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
import org.apache.avro.generic.GenericData;
import org.talend.daikon.avro.visitor.path.TraversalPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Wrapper for arrays.
 *
 * Calling the {@link #accept(RecordVisitor)} will only call {@link RecordVisitor#visit(VisitableArray)}.
 *
 * To visit all items of the array, call the {@link #getItems(ArrayItemsPathType)} method and visit each
 * item.
 *
 * When calling this method, it is possible to choose how item's path are built, it is either:
 * - index (/array[0], /array[1] ...)
 * - not indexed /array path will be returned for each item of the array
 */
public class VisitableArray extends AbstractVisitableStructure<GenericData.Array> {

    public VisitableArray(GenericData.Array value, TraversalPath path) {
        super(value, path);
    }

    @Override
    public void accept(RecordVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * @return an iterator over all items contained in the array
     * @param type how items path is built
     *
     */
    public Iterator<VisitableStructure> getItems(ArrayItemsPathType type) {
        final GenericData.Array array = this.getValue();
        final Schema elementSchema = array.getSchema().getElementType();
        final List<VisitableStructure> items = new ArrayList<>(array.size());
        for (int i = 0; i < array.size(); i++) {
            final Object value = array.get(i);
            final TraversalPath path = type.buildTraversalPath(this.getPath(), i);
            final VisitableStructure element = VisitableStructureFactory.createVisitableStructure(elementSchema, value, path);
            items.add(element);
        }
        return Collections.unmodifiableList(items).iterator();
    }

    /**
     * Defines how arrays elements' path are built.
     */
    public enum ArrayItemsPathType {

        INDEXED(new IndexedArrayItemPathBuilder()),
        NOT_INDEXED(new NotIndexedArrayItemPathBuilder());

        private final ArrayItemPathBuilder pathBuilder;

        ArrayItemsPathType(ArrayItemPathBuilder pathBuilder) {
            this.pathBuilder = pathBuilder;
        }

        private TraversalPath buildTraversalPath(TraversalPath path, int index) {
            return this.pathBuilder.buildTraversalPath(path, index);
        }
    }

    private interface ArrayItemPathBuilder {

        TraversalPath buildTraversalPath(TraversalPath path, int index);

    }

    private static class IndexedArrayItemPathBuilder implements ArrayItemPathBuilder {

        @Override
        public TraversalPath buildTraversalPath(TraversalPath path, int index) {
            return path.appendArrayIndex(index);
        }
    }

    private static class NotIndexedArrayItemPathBuilder implements ArrayItemPathBuilder {

        @Override
        public TraversalPath buildTraversalPath(TraversalPath path, int index) {
            Schema elementsSchema = path.last().getSchema().getElementType();
            Iterator<TraversalPath.TraversalPathElement> iterator = path.iterator();
            TraversalPath.TraversalPathElement current = iterator.next();
            TraversalPath result = TraversalPath.create(current.getSchema());
            while (current != path.last()) {
                result = result.append(current);
                current = iterator.next();
            }
            result = result.append(path.last().getName(), path.last().getPosition(), elementsSchema);
            return result;
        }
    }

}
