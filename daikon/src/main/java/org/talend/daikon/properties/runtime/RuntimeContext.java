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
package org.talend.daikon.properties.runtime;

import org.talend.daikon.properties.Properties;

/**
 * Represents Runtime context, in which {@link Properties} are used.
 * This interface can be used to pass some information from Runtime to {@link Properties}'s trigger methods:
 * {@code before<PropertyName>}, {@code after<PropertyName>} etc
 */
public interface RuntimeContext {

    /**
     * Return a data value for given key or null if there is no such data
     * 
     * @param key data value key
     * @return data value for given key or null
     */
    Object getData(String key);

}
