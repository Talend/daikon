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
import org.talend.daikon.properties.PropertiesDynamicMethodHelper;
import org.talend.daikon.properties.presentation.Form;

/**
 * Main Component Service implementation that is not related to any framework (neither OSGI, nor Spring) it uses a
 * ComponentRegistry implementation that will be provided by framework specific Service classes
 */
public class PropertiesServiceImpl implements PropertiesService<Properties> {

    private Repository<Properties> repository;

    @Override
    public Properties makeFormCancelable(Properties properties, String formName) {
        Form form = properties.getForm(formName);
        if (form == null) {
            throw new IllegalArgumentException("Form: " + formName + " not found");
        }
        form.setCancelable(true);
        return properties;
    }

    @Override
    public Properties cancelFormValues(Properties properties, String formName) {
        Form form = properties.getForm(formName);
        if (form == null) {
            throw new IllegalArgumentException("Form: " + formName + " not found");
        }
        form.cancelValues();
        return properties;
    }

    @Override
    public Properties validateProperty(String propName, Properties properties) throws Throwable {
        PropertiesDynamicMethodHelper.validateProperty(properties, propName);
        return properties;
    }

    @Override
    public Properties beforePropertyActivate(String propName, Properties properties) throws Throwable {
        PropertiesDynamicMethodHelper.beforePropertyActivate(properties, propName);
        return properties;
    }

    @Override
    public Properties beforePropertyPresent(String propName, Properties properties) throws Throwable {
        PropertiesDynamicMethodHelper.beforePropertyPresent(properties, propName);
        return properties;
    }

    @Override
    public Properties afterProperty(String propName, Properties properties) throws Throwable {
        PropertiesDynamicMethodHelper.afterProperty(properties, propName);
        return properties;
    }

    @Override
    public Properties beforeFormPresent(String formName, Properties properties) throws Throwable {
        PropertiesDynamicMethodHelper.beforeFormPresent(properties, formName);
        return properties;
    }

    @Override
    public Properties afterFormNext(String formName, Properties properties) throws Throwable {
        PropertiesDynamicMethodHelper.afterFormNext(properties, formName);
        return properties;
    }

    @Override
    public Properties afterFormBack(String formName, Properties properties) throws Throwable {
        PropertiesDynamicMethodHelper.afterFormBack(properties, formName);
        return properties;
    }

    @Override
    public Properties afterFormFinish(String formName, Properties properties) throws Throwable {
        PropertiesDynamicMethodHelper.afterFormFinish(properties, formName, repository);
        return properties;
    }

    @Override
    public String storeProperties(Properties properties, String name, String repositoryLocation, String schemaPropertyName) {
        if (repository != null) {
            return repository.storeProperties(properties, name, repositoryLocation, schemaPropertyName);
        }
        return null;
    }

    @Override
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

}
