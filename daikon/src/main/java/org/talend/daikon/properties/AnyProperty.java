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

import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.property.Property;

import java.io.Serializable;

/**
 * A common interface for the members of a {@link Properties} object.
 *
 * This is implemented by {@link Property} and {@link Properties} and is used to allow the members of the {@code Properties}
 * object to be visited.
 */
public interface AnyProperty extends NamedThing, Serializable {

    /**
     * Be visited from it's parent.
     * 
     * @param visitor the visitor of the object
     * @param parent Properties that issued the visit or null if no parent is set.
     * 
     */
    void accept(AnyPropertyVisitor visitor, Properties parent);
}
