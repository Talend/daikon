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

import static org.talend.daikon.properties.property.PropertyFactory.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.definition.Definition;
import org.talend.daikon.definition.service.DefinitionRegistryService;
import org.talend.daikon.properties.property.Property;

/**
 * A reference to another properties.
 * 
 * The {@link org.talend.daikon.properties.presentation.Widget#COMPONENT_REFERENCE_WIDGET_TYPE} uses this class as its
 * properties and the Widget will populate these values.
 */

public class ReferenceProperties<T extends Properties> extends PropertiesImpl {

    private static final long serialVersionUID = -5424441531219285811L;

    private static final Logger LOG = LoggerFactory.getLogger(ReferenceProperties.class);

    /**
     * name of the definition that may be used to create the reference type, which must create a Properties of type T.
     */
    public final Property<String> referenceDefinitionName = newProperty("referenceDefinitionName");

    /**
     * the reference instance
     */
    private T reference;

    public ReferenceProperties(String name, String referenceDefinitionName) {
        super(name);
        this.referenceDefinitionName.setValue(referenceDefinitionName);
    }

    public void setReference(Properties prop) {
        reference = (T) prop;
    }

    public T getReference() {
        return reference;
    }

    @Override
    protected boolean acceptUninitializedField(Field f) {
        if (super.acceptUninitializedField(f)) {
            return true;
        }
        // we accept that return field is not initialized after setupProperties.
        return "reference".equals(f.getName());
    }

    @Override
    protected void acceptForAllProperties(AnyPropertyVisitor visitor, Set<Properties> visited) {
        super.acceptForAllProperties(visitor, visited);

        if (reference != null) {
            acceptForProperty(visitor, visited, reference);
        }
    }

    /**
     * resolve the referenced properties between a group of properties.
     * 
     * @param properties list of all references to resolve
     * @param definitionRegistry used to find the definitions compatible with current properties
     */
    public static void resolveReferenceProperties(final Iterable<? extends Properties> properties,
            DefinitionRegistryService definitionRegistry) {
        // construct the definitionName and Properties map
        Map<String, Properties> def2PropsMap = new HashMap<>();

        for (Properties prop : properties) {
            // look for the definition associated with the properties
            Iterable<Definition> allDefs = definitionRegistry.getDefinitionForPropertiesType(prop.getClass());
            for (Definition def : allDefs) {
                def2PropsMap.put(def.getName(), prop);
            }
        }
        resolveReferenceProperties(def2PropsMap);
    }

    /**
     * resolve the referenced properties between a group of properties.
     * 
     * @param propertiesMap a map with the definitions name and Properties instance related to those definitions
     */
    public static void resolveReferenceProperties(final Map<String, Properties> propertiesMap) {
        for (Entry<String, Properties> entry : propertiesMap.entrySet()) {
            Properties theProperties = entry.getValue();
            theProperties.accept(new PropertiesVisitor() {

                @Override
                public void visit(Properties properties, Properties parent) {
                    if (properties instanceof ReferenceProperties<?>) {
                        ReferenceProperties<?> referenceProperties = (ReferenceProperties<?>) properties;
                        Properties theReference = propertiesMap.get(referenceProperties.referenceDefinitionName.getValue());
                        if (theReference != null) {
                            referenceProperties.setReference(theReference);
                        } else {// no reference of the required type has been provided so do no set anything but log it
                            LOG.debug("failed to find a reference object for ReferenceProperties[" + referenceProperties.getName()
                                    + "] with definition [" + referenceProperties.referenceDefinitionName.getValue()
                                    + "] and with parent type [" + (parent != null ? parent.getClass().getName() : "null") + "]");
                        }
                    }

                }
            }, null);
        }
    }

}
