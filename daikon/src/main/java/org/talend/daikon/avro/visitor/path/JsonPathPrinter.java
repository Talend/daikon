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

/**
 * JSONPath implementation of {@link TraversalPathPrinter}.
 *
 * Supports both "dot-based" and "brackets-based" notation.
 */
public class JsonPathPrinter implements TraversalPathPrinter {

    private final StringBuffer buffer = new StringBuffer();

    private final JsonPathStyle style;

    public JsonPathPrinter() {
        this(JsonPathStyle.DOT);
    }

    public JsonPathPrinter(JsonPathStyle style) {
        this.style = style;
    }

    @Override
    public void root() {
        buffer.append('$');
    }

    @Override
    public void append(String name, int position) {
        this.style.notation.append(this.buffer, name);
    }

    @Override
    public void arrayIndex(int index) {
        this.buffer.append('[');
        this.buffer.append(index);
        this.buffer.append(']');
    }

    @Override
    public void mapEntry(String key) {
        this.style.notation.append(this.buffer, key);
    }

    @Override
    public String toString() {
        return this.buffer.toString();
    }

    public enum JsonPathStyle {

        DOT(new DotNotation()),
        BRACKETS(new BracketNotation());

        public final JsonPathNotation notation;

        JsonPathStyle(JsonPathNotation notation) {
            this.notation = notation;
        }

    }

    private interface JsonPathNotation {

        void append(StringBuffer buffer, String name);

    }

    private static final class DotNotation implements JsonPathNotation {

        @Override
        public void append(StringBuffer buffer, String name) {
            buffer.append('.');
            buffer.append(name);
        }
    }

    private static final class BracketNotation implements JsonPathNotation {

        @Override
        public void append(StringBuffer buffer, String name) {
            buffer.append("[\'");
            buffer.append(name);
            buffer.append("\']");
        }
    }

}
