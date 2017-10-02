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
 * XPATH implementation of {@link TraversalPathPrinter}
 */
public class XpathPrinter implements TraversalPathPrinter {

    private static final char SEPARATOR = '/';

    private static final char OPEN_INDEX = '[';

    private static final char CLOSE_INDEX = ']';

    private final StringBuffer buffer = new StringBuffer();

    @Override
    public void root() {
        // nothing to do
    }

    @Override
    public void append(String name, int position) {
        buffer.append(SEPARATOR);
        buffer.append(name);
    }

    @Override
    public void arrayIndex(int index) {
        buffer.append(OPEN_INDEX);
        buffer.append(index);
        buffer.append(CLOSE_INDEX);
    }

    @Override
    public void mapEntry(String key) {
        buffer.append(SEPARATOR);
        buffer.append(key);
    }

    public String toString() {
        String result = buffer.toString();
        return result.length() == 0 ? String.valueOf(SEPARATOR) : result;
    }
}
