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
package org.talend.daikon.avro.visitor.path;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;

/**
 * Defines a traversal path in a hierarchical record.
 *
 * Each element in the path is an instance of {@link TraversalPathElement} (or one of its sub-classes).
 *
 * This API is immutable.
 *
 * To build a new path, use this class's methods as fluent API:
 *
 * <pre>
 *     {@code
 *     Schema schema = ...;
 *     TraversalPath path = TraversalPath.create(schema)
 *         .append("field1")
 *         .append("array")
 *         .appendArrayIndex(5);
 *     path.toString(); // return /field1/array[5] in XPATH notation
 *     }
 * </pre>
 *
 * When building the path above, each call to the {@link #append(String)} method will
 * assume it complies with the schema provided with initiating the path.
 *
 * It is also possible to build a path that do not comply with its root's schema
 * by using {@link #append(TraversalPathElement)} or {@link #append(String, int, Schema)} methods
 *
 */
public class TraversalPath implements Iterable<TraversalPath.TraversalPathElement>, Serializable {

    private static final long serialVersionUID = 1L;

    private final LinkedList<TraversalPathElement> elements;

    private final String path;

    private TraversalPath(LinkedList<TraversalPathElement> elements) {
        this.elements = elements;
        XpathPrinter printer = new XpathPrinter();
        this.path = this.toString(printer);
    }

    /**
     * Creates a new root path.
     *
     * @param schema the schema of the root element (probably a record schema)
     * @return the newly created path referring to root.
     */
    public static TraversalPath create(Schema schema) {
        final RootPathElement root = new RootPathElement(schema);
        return new TraversalPath(new LinkedList<TraversalPathElement>(Collections.singletonList(root)));
    }

    /**
     * creates a new path by appending a new element given its name.
     * Assumes this path has a record schema with a field named "name" in it.
     *
     * @param name the name of the field
     * @return the newly created path
     * @throws AvroRuntimeException if the current element is not a record
     * @throws NullPointerException if the current element's schema has no field named name
     */
    public TraversalPath append(String name) {
        final Schema schema = this.last().getSchema();
        final Schema.Field field = schema.getField(name);
        return this.append(name, field.pos(), field.schema());
    }

    /**
     * creates a new path by appending a new element given its position in the schema.
     * Assumes this path has a record schema and a field at the provided position.
     *
     * @param position the position of the field
     * @return the newly created path
     * @throws AvroRuntimeException if the current element is not a record
     * @throws ArrayIndexOutOfBoundsException if the position is not valid
     */
    public TraversalPath append(int position) {
        final Schema schema = this.last().getSchema();
        final Schema.Field field = schema.getFields().get(position);
        return this.append(field.name(), position, field.schema());
    }

    /**
     * creates a new path by appending a new element to this one.
     *
     * Note that there is not schema conformity check when using this method.
     *
     * @param element the new path element to append
     * @return the newly created path
     */
    public TraversalPath append(TraversalPathElement element) {
        LinkedList<TraversalPathElement> newList = new LinkedList<>(this.elements);
        newList.add(element);
        return new TraversalPath(newList);
    }

    /**
     * creates a new path by appending a new element to this one.
     *
     * Note that there is not schema conformity check when using this method.
     *
     * @param name name of the new element
     * @param position element's position within its parent hierarchy
     * @param schema element's schema
     * @return the newly created path
     */
    public TraversalPath append(String name, int position, Schema schema) {
        return this.append(new TraversalPathElement(name, position, schema));
    }

    /**
     * creates a new path by appending a new array element to this once - which is considered
     * as being the path to an array.
     *
     * Example in XPATH notation:
     * given /field1/array
     * when called with index=1
     * then returns /field1/array[1]
     *
     * @param index element's index within the array
     * @return the newly created path
     */
    public TraversalPath appendArrayIndex(int index) {
        TraversalPathElement array = this.last();
        return this.append(new ArrayItemPathElement(array, index));
    }

    /**
     * creates a new path by appending a new map entry element to this once - which is considered
     * as being the path to a map.
     *
     * @param key the entry key
     * @return the newly created path
     */
    public TraversalPath appendMapEntry(String key) {
        TraversalPathElement map = this.last();
        return this.append(new MapEntryPathElement(map, key));
    }

    /**
     * @return an {@link Iterator} over the elements of this path starting from root.
     */
    public Iterator<TraversalPathElement> iterator() {
        return this.elements.iterator();
    }

    /**
     * @return an {@link Iterator} over the elements of this path starting from the end.
     */
    public Iterator<TraversalPathElement> descendingIterator() {
        return this.elements.descendingIterator();
    }

    /**
     * @return the last (current) element of the path
     */
    public TraversalPathElement last() {
        return this.elements.getLast();
    }

    /**
     * @return the root element of the path.
     */
    public TraversalPathElement root() {
        return this.elements.getFirst();
    }

    @Override
    public String toString() {
        return this.path;
    }

    /**
     * Outputs the current path using the provider printer
     *
     * @param printer the printer to use
     * @return the printers result
     */
    public String toString(TraversalPathPrinter printer) {
        for (TraversalPathElement element : this.elements) {
            element.print(printer);
        }
        return printer.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TraversalPath that = (TraversalPath) o;

        return path != null ? path.equals(that.path) : that.path == null;
    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }

    /**
     * A single element a {@link TraversalPath}
     */
    public static class TraversalPathElement implements Serializable {

        private static final long serialVersionUID = 1L;

        private final String name;

        private final int position;

        private final Schema schema;

        /**
         * Creates a new element
         * 
         * @param name its name
         * @param position its position in its parent's schema
         * @param schema its schema
         */
        TraversalPathElement(String name, int position, Schema schema) {
            this.name = name;
            this.position = position;
            this.schema = schema;
        }

        /**
         * @return the name of the element
         */
        public String getName() {
            return name;
        }

        /**
         * @return the position of the element in it's parent's schema
         */
        public int getPosition() {
            return position;
        }

        /**
         * @return the schema of the element
         */
        public Schema getSchema() {
            return schema;
        }

        /**
         * Prints this element in a {@link TraversalPathPrinter}
         * 
         * @param printer the printer to use
         */
        public void print(TraversalPathPrinter printer) {
            printer.append(name, position);
        }
    }

    /**
     * The root element of a path. By convention, its name is ROOT, its position is 0
     */
    public static class RootPathElement extends TraversalPathElement {

        private static final long serialVersionUID = 1L;

        private static final String ROOT = "";

        /**
         * Creates a root element, given its schema
         * 
         * @param schema the schema of the root element
         */
        public RootPathElement(Schema schema) {
            super(ROOT, 0, schema);
        }

        public void print(TraversalPathPrinter printer) {
            printer.root();
        }
    }

    /**
     * Refers to an item of an array. This item is identified by its index in the array.
     */
    public static class ArrayItemPathElement extends TraversalPathElement {

        private static final long serialVersionUID = 1L;

        private final int index;

        /**
         * Creates a path item referencing an item of an array
         * 
         * @param arrayPath the path element for the array (the container)
         * @param index the index of this particular item
         */
        ArrayItemPathElement(TraversalPathElement arrayPath, int index) {
            super(arrayPath.name, arrayPath.position, arrayPath.schema.getElementType());
            this.index = index;
        }

        /**
         * @return the index of the element within the containing array
         */
        public int getIndex() {
            return this.index;
        }

        public void print(TraversalPathPrinter printer) {
            printer.arrayIndex(index);
        }
    }

    /**
     * Refers to an entry of a map. The element identified by its key in the map.
     *
     * As Avro, String keys are only supported.
     */
    public static class MapEntryPathElement extends TraversalPathElement {

        private static final long serialVersionUID = 1L;

        private final String key;

        /**
         * Creates a path element referencing an entry in a map
         * 
         * @param mapPath the path element for the map
         * @param key the key of this entry
         */
        MapEntryPathElement(TraversalPathElement mapPath, String key) {
            super(mapPath.name, mapPath.position, mapPath.schema.getValueType());
            this.key = key;
        }

        /**
         * @return the key for this entry
         */
        public String getKey() {
            return key;
        }

        public void print(TraversalPathPrinter printer) {
            printer.mapEntry(key);
        }
    }

}
