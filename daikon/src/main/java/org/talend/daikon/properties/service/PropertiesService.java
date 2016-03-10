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
package org.talend.daikon.properties.service;

import org.talend.daikon.properties.Properties;

/**
 * The Main service provided by this project to get access to all registered components and their properties.
 */
public interface PropertiesService<T extends Properties> extends Repository<T> {

    T makeFormCancelable(T properties, String formName);

    T cancelFormValues(T properties, String formName);

    T validateProperty(String propName, T properties) throws Throwable;

    T beforePropertyActivate(String propName, T properties) throws Throwable;

    T beforePropertyPresent(String propName, T properties) throws Throwable;

    T afterProperty(String propName, T properties) throws Throwable;

    T beforeFormPresent(String formName, T properties) throws Throwable;

    T afterFormNext(String formName, T properties) throws Throwable;

    T afterFormBack(String formName, T properties) throws Throwable;

    T afterFormFinish(String formName, T properties) throws Throwable;

    /**
     * Allows for a local implementation to setup a repository store used to store {@link T}.
     * 
     * @param repository
     */
    void setRepository(Repository repository);

}