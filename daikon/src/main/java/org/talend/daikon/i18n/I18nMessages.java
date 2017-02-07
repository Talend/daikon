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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A base class for tools that provide message translation services.
 */
public abstract class I18nMessages {

    private transient MessageFormat formatter = new MessageFormat(""); //$NON-NLS-1$

    protected transient LocaleProvider localeProvider;

    public I18nMessages(LocaleProvider localeProvider) {
        this.localeProvider = localeProvider;
    }

    abstract public String getMessage(String key, Object... arguments);

    protected Locale getLocale() {
        if (localeProvider != null) {
            Locale locale = localeProvider.getLocale();
            return locale != null ? locale : Locale.getDefault();
        } else {
            return Locale.getDefault();
        }
    }

    /**
     * Get the message bundle and get the value according to the key and then format it all of this using the dynamic
     * Locale.
     * 
     * @param key the identifying key for the message
     * @param classLoader, the classloader used to look for the basename resource.
     * @param baseName, used to create the underlying resource bundle, see {@link ResourceBundle#getBundle(String)}
     * @param arguments, used for the formatting the return message using the {@link java.text.MessageFormat}
     * @return the formatted message, never null
     * @exception MissingResourceException if not message was found with the key.
     */
    protected String getFormattedMessage(String key, ClassLoader classLoader, String baseName, Object... arguments)
            throws MissingResourceException {
        Locale locale = getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale, classLoader);
        String bundleValue = bundle.getString(key);
        // format it now
        formatter.setLocale(locale);
        formatter.applyPattern(bundleValue);
        return formatter.format(arguments);
    }

}