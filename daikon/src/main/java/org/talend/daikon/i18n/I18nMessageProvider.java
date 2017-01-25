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
package org.talend.daikon.i18n;

/**
 * Provides I18n service according to the local provider service
 */
public abstract class I18nMessageProvider {

    abstract protected LocaleProvider getLocaleProvider();

    /**
     * Return a I18nMessages with a resource bundle found at the path related to the classloader
     * 
     * @param classLoader, use to create the underlying resource bundle.
     * @param baseName, used to create the underlying resource bundle, see
     * {@link ResourceBundle#getBundle(String, java.util.Locale, ClassLoader, java.util.ResourceBundle.Control))}
     * @return a DynamicLocalFormatedI18n instance to handle i18n.
     */
    public I18nMessages getI18nMessages(ClassLoader classLoader, String baseName) {
        return new ClassLoaderBasedI18nMessages(getLocaleProvider(), classLoader, baseName);
    }

    /**
     * Return a I18nMessages with a resource bundle named messages.properties found at the package level of the clazz.
     * if not key found in that file, it looks for messages.properties in the super class packages.
     * 
     * @param clazz, use to create find messages.properties according to it package name.
     * @return a DynamicLocalFormatedI18n instance to handle i18n.
     */
    public I18nMessages getI18nMessages(Class<?> clazz) {
        return new ClassBasedI18nMessages(getLocaleProvider(), clazz);
    }

}
