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
import org.talend.daikon.properties.runtime.RuntimeContext;

/**
 * Implementation of the {@link PropertiesService}.
 */
public class PropertiesServiceImpl implements PropertiesService<Properties> {

    private Repository<Properties> repository;

    @Override
    // FIXME TDKN-67 - remove this
    public Properties makeFormCancelable(Properties properties, String formName) {
        Form form = properties.getForm(formName);
        if (form == null) {
            throw new IllegalArgumentException("Form: " + formName + " not found");
        }
        form.setCancelable(true);
        return properties;
    }

    @Override
    // FIXME TDKN-67 - remove this
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
    public Properties validateProperty(String propName, Properties properties, RuntimeContext context) throws Throwable {
        PropertiesDynamicMethodHelper.validateProperty(properties, propName, context);
        return properties;
    }

    @Override
    public Properties beforePropertyActivate(String propName, Properties properties) throws Throwable {
        PropertiesDynamicMethodHelper.beforePropertyActivate(properties, propName);
        return properties;
    }

    @Override
    public Properties beforePropertyActivate(String propName, Properties properties, RuntimeContext context) throws Throwable {
        PropertiesDynamicMethodHelper.beforePropertyActivate(properties, propName, context);
        return properties;
    }

    @Override
    public Properties beforePropertyPresent(String propName, Properties properties) throws Throwable {
        PropertiesDynamicMethodHelper.beforePropertyPresent(properties, propName);
        return properties;
    }

    @Override
    public Properties beforePropertyPresent(String propName, Properties properties, RuntimeContext context) throws Throwable {
        PropertiesDynamicMethodHelper.beforePropertyPresent(properties, propName, context);
        return properties;
    }

    @Override
    public Properties afterProperty(String propName, Properties properties) throws Throwable {
        PropertiesDynamicMethodHelper.afterProperty(properties, propName);
        return properties;
    }

    @Override
    public Properties afterProperty(String propName, Properties properties, RuntimeContext context) throws Throwable {
        PropertiesDynamicMethodHelper.afterProperty(properties, propName, context);
        return properties;
    }

    @Override
    public Properties beforeFormPresent(String formName, Properties properties) throws Throwable {
        PropertiesDynamicMethodHelper.beforeFormPresent(properties, formName);
        return properties;
    }

    @Override
    public Properties beforeFormPresent(String formName, Properties properties, RuntimeContext context) throws Throwable {
        PropertiesDynamicMethodHelper.beforeFormPresent(properties, formName, context);
        return properties;
    }

    @Override
    public Properties afterFormNext(String formName, Properties properties) throws Throwable {
        PropertiesDynamicMethodHelper.afterFormNext(properties, formName);
        return properties;
    }

    @Override
    public Properties afterFormNext(String formName, Properties properties, RuntimeContext context) throws Throwable {
        PropertiesDynamicMethodHelper.afterFormNext(properties, formName, context);
        return properties;
    }

    @Override
    public Properties afterFormBack(String formName, Properties properties) throws Throwable {
        PropertiesDynamicMethodHelper.afterFormBack(properties, formName);
        return properties;
    }

    @Override
    public Properties afterFormBack(String formName, Properties properties, RuntimeContext context) throws Throwable {
        PropertiesDynamicMethodHelper.afterFormBack(properties, formName, context);
        return properties;
    }

    @Override
    public Properties afterFormFinish(String formName, Properties properties) throws Throwable {
        PropertiesDynamicMethodHelper.afterFormFinish(properties, formName, repository);
        return properties;
    }

    @Override
    public Properties afterFormFinish(String formName, Properties properties, RuntimeContext context) throws Throwable {
        PropertiesDynamicMethodHelper.afterFormFinish(properties, formName, repository, context);
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
