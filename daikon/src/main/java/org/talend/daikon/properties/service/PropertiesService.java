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
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.runtime.RuntimeContext;

/**
 * The service that drives the user interface (UI) accessing {@link Properties} objects.
 *
 * This is a service so that the a single UI can be specified using {@link Properties} objects which can be presented in a local
 * desktop environment, a web environment (using JSON objects/REST calls), or by scripting.
 */
public interface PropertiesService<T extends Properties> extends Repository<T> {

    /**
     * Makes the specified {@link Form} object cancelable, which means that modifications to the values can be canceled.
     *
     * This is intended for local use only. When using this with the REST service, the values can simply be reset in the JSON
     * version of the {@link Form} object, so the cancel operation can be implemented entirely by the client.
     * 
     * @param properties the {@link Properties} object associated with the {@code Form}.
     * @param formName the name of the form
     * @return the {@link Properties} object specified as modified by this service.
     */
    // FIXME TDKN-67 - remove this
    T makeFormCancelable(T properties, String formName);

    /**
     * Cancels the changes to the values in the specified {@link Form} object after the it was made cancelable.
     *
     * This is intended for local use only. When using this with the REST service, the values can simply be reset in the JSON
     * version of the {@link Form} object, so the cancel operation can be implemented entirely by the client.
     * 
     * @param properties the {@link Properties} object associated with the {@code Form}.
     * @param formName the name of the form
     * @return the {@link Properties} object specified as modified by this service.
     */
    // FIXME TDKN-67 - remove this
    T cancelFormValues(T properties, String formName);

    /**
     * @see {@link Properties} for a description of the meaning of this method.
     * @return the {@link Properties} object specified as modified by this service.
     */
    T validateProperty(String propName, T properties) throws Throwable;

    /**
     * Calls {@code validate<PropertyName>} method of {@link Properties} instance with {@link RuntimeContext} parameter
     * 
     * @see {@link Properties} for a description of the meaning of this method.
     * @return the {@link Properties} object specified as modified by this service.
     */
    T validateProperty(String propName, T properties, RuntimeContext context) throws Throwable;

    /**
     * @see {@link Properties} for a description of the meaning of this method.
     * @return the {@link Properties} object specified as modified by this service.
     */
    T beforePropertyActivate(String propName, T properties) throws Throwable;

    /**
     * Calls {@code before<PropertyName>} method of {@link Properties} instance with {@link RuntimeContext} parameter
     * 
     * @see {@link Properties} for a description of the meaning of this method.
     * @return the {@link Properties} object specified as modified by this service.
     */
    T beforePropertyActivate(String propName, T properties, RuntimeContext context) throws Throwable;

    /**
     * @see {@link Properties} for a description of the meaning of this method.
     * @return the {@link Properties} object specified as modified by this service.
     */
    T beforePropertyPresent(String propName, T properties) throws Throwable;

    /**
     * Calls {@code before<PropertyName>} method of {@link Properties} instance with {@link RuntimeContext} parameter
     * 
     * @see {@link Properties} for a description of the meaning of this method.
     * @return the {@link Properties} object specified as modified by this service.
     */
    T beforePropertyPresent(String propName, T properties, RuntimeContext context) throws Throwable;

    /**
     * @see {@link Properties} for a description of the meaning of this method.
     * @return the {@link Properties} object specified as modified by this service.
     */
    T afterProperty(String propName, T properties) throws Throwable;

    /**
     * Calls {@code after<PropertyName>} method of {@link Properties} instance with {@link RuntimeContext} parameter
     * 
     * @see {@link Properties} for a description of the meaning of this method.
     * @return the {@link Properties} object specified as modified by this service.
     */
    T afterProperty(String propName, T properties, RuntimeContext context) throws Throwable;

    /**
     * @see {@link Properties} for a description of the meaning of this method.
     * @return the {@link Properties} object specified as modified by this service.
     */
    T beforeFormPresent(String formName, T properties) throws Throwable;

    /**
     * Calls {@code beforeFormPresent<FormName>} method of {@link Properties} instance with {@link RuntimeContext} parameter
     * 
     * @see {@link Properties} for a description of the meaning of this method.
     * @return the {@link Properties} object specified as modified by this service.
     */
    T beforeFormPresent(String formName, T properties, RuntimeContext context) throws Throwable;

    /**
     * @see {@link Properties} for a description of the meaning of this method.
     * @return the {@link Properties} object specified as modified by this service.
     */
    T afterFormNext(String formName, T properties) throws Throwable;

    /**
     * Calls {@code afterFormNext<FormName>} method of {@link Properties} instance with {@link RuntimeContext} parameter
     * 
     * @see {@link Properties} for a description of the meaning of this method.
     * @return the {@link Properties} object specified as modified by this service.
     */
    T afterFormNext(String formName, T properties, RuntimeContext context) throws Throwable;

    /**
     * @see {@link Properties} for a description of the meaning of this method.
     * @return the {@link Properties} object specified as modified by this service.
     */
    T afterFormBack(String formName, T properties) throws Throwable;

    /**
     * Calls {@code afterFormBack<FormName>} method of {@link Properties} instance with {@link RuntimeContext} parameter
     * 
     * @see {@link Properties} for a description of the meaning of this method.
     * @return the {@link Properties} object specified as modified by this service.
     */
    T afterFormBack(String formName, T properties, RuntimeContext context) throws Throwable;

    /**
     * @see {@link Properties} for a description of the meaning of this method.
     * @return the {@link Properties} object specified as modified by this service.
     */
    T afterFormFinish(String formName, T properties) throws Throwable;

    /**
     * Calls {@code afterFormFinish<FormName>} method of {@link Properties} instance with {@link RuntimeContext} parameter
     * 
     * @see {@link Properties} for a description of the meaning of this method.
     * @return the {@link Properties} object specified as modified by this service.
     */
    T afterFormFinish(String formName, T properties, RuntimeContext context) throws Throwable;

    /**
     * Allows for a local implementation to setup a repository store used to store {@link T}.
     */
    void setRepository(Repository repository);

}