// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.properties;

import org.talend.daikon.properties.property.Property;

/**
 * Visit all members of a {@link Properties} object except {@link PresentationItem}
 */
public interface AnyPropertyVisitor {

    /**
     * Visit the Property.
     * 
     * @param property, the visited property
     * @param parent, the parent of the enclosing {@link Properties} object, or null if none.
     */
    void visit(Property property, Properties parent);

    /**
     * Visit the Properties
     * 
     * @param properties, the visited properties object
     * @param parent, the properties parent or null if none.
     */
    void visit(Properties properties, Properties parent);
}
