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

import static org.talend.daikon.properties.property.Property.Flags.DESIGN_TIME_ONLY;
import static org.talend.daikon.properties.property.PropertyFactory.newEnum;
import static org.talend.daikon.properties.property.PropertyFactory.newProperty;

import java.util.EnumSet;

import org.talend.daikon.properties.property.Property;

/**
 * A reference to another properties. This could be in one of the following states:
 * <li>Use this properties (no reference)</li>
 * <li>Reference a single instance of a given properties type in the enclosing scope, e.g. Job</li>
 * <li>Reference to a particular instance of a properties. In this case, the {@link #properties} will be populated by
 * the {@link org.talend.daikon.properties.presentation.Widget}.</li>
 *
 * IMPORTANT - when using {@code ComponentReferenceProperties} the property name in the enclosingProperties must be
 * {@code referencedComponent}.
 *
 * The {@link org.talend.daikon.properties.presentation.WidgetType#COMPONENT_REFERENCE} uses this class as its
 * properties and the Widget will populate these values.
 */
public interface ReferenceProperties extends Properties {

    public enum ReferenceType {
        THIS_COMPONENT,
        COMPONENT_TYPE,
        COMPONENT_INSTANCE
    }

    //
    // Properties
    //
    public Property<ReferenceType> referenceType = newEnum("referenceType", ReferenceType.class);

    public Property<String> componentType = newProperty("componentType").setFlags(EnumSet.of(DESIGN_TIME_ONLY)); //$NON-NLS-1$

}
