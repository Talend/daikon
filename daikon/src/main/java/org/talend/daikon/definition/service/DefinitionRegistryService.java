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

/**
 * The service should handle {@link Definition} and garanties that onyl one instance is registered with on unique name (
 * {@link Definition.getName()}
 */
public interface DefinitionRegistryService {

    /**
     * Get the map of all {@link Definition} that implement a specific interface using thier name as the map key.
     *
     * @param An interface or subclass of {@link Definition}.
     * @return All {@link Definition} that were registered in the framework and that implement that interface or an empty map.
     */
    <T extends Definition> Map<String, T> getDefinitionsMapByType(Class<T> cls);

    /**
     * Return the image related to the given definition
     * 
     * @param definitionName, name of the defintion to get the image for
     * @return the image stream or null if none was provided or an error occurred
     * @exception ComponentException thrown if the definitionName is not registered in the service
     */
    InputStream getImage(String definitionName);

}
