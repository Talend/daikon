// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.definition.service;

import java.io.InputStream;
import java.util.Map;

import org.talend.daikon.definition.Definition;
import org.talend.daikon.properties.Properties;

/**
 * The service should handle {@link Definition} and guarantees that only one instance is registered with a unique name (
 * {@link Definition#getName()}
 */
public interface DefinitionRegistryService {

    /**
     * Get the map of all {@link Definition} that implement a specific interface using the name as the map key.
     *
     * @param cls An interface or subclass of {@link Definition}.
     * @return All {@link Definition} that were registered in the framework and that implement that interface or an empty map.
     */
    <T extends Definition> Map<String, T> getDefinitionsMapByType(Class<T> cls);

    /**
     * Return the image related to the given definition.
     *
     * @param definitionName, name of the definition to get the image for
     * @return the image stream or null if none was provided or an error occurred
     * @exception org.talend.daikon.exception.TalendRuntimeException thrown if the definitionName is not registered in
     * the service
     */
    InputStream getImage(String definitionName);

    /**
     * @return the list of definitions that can create an instance of properties. All the definition returned shall be able to
     * return an exact instance of the class or one of it's derived class. An empty iterable is returned if none found.
     */
    Iterable<Definition> getDefinitionForPropertiesType(Class<? extends Properties> propertiesClass);

    /**
     * creates the Properties instance related to the Definition, the returned properties has also been initialised (
     * {@link Properties#init()} called).
     */
    <P extends Properties> P createProperties(Definition<P> definition, String name);

}
