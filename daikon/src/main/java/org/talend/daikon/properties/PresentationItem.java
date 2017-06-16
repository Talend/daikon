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

import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

/**
 * Contains a {@link Widget} that appears in the UI that is not backed by a {@link Property}.
 *
 * This is used for things like buttons (that require actions) or text items that provide description or instruction.
 */
public class PresentationItem extends SimpleNamedThing implements AnyProperty {

    private static final long serialVersionUID = 1L;

    public static final String I18N_PRESENTATION_ITEM_PREFIX = "presItem.";

    /**
     * The {@link Form} to show when this {@code PresentationItem} is activated (the button is pressed).
     */
    private Form formtoShow;

    /**
     * creates a presentation item with a name and a displayName
     * 
     * @param name
     * @param displayName @deprecated, please use the {@link PresentationItem#PresentationItem(String)} constructor
     * instead and provide a .properties file with the i18n value just like any Property but with the prefix
     * {@link PresentationItem#I18N_PRESENTATION_ITEM_PREFIX}
     */
    @Deprecated
    public PresentationItem(String name, String displayName) {
        super(name, displayName);
    }

    public PresentationItem(String name) {
        super(name);
    }

    public Form getFormtoShow() {
        return formtoShow;
    }

    public void setFormtoShow(Form formtoShow) {
        this.formtoShow = formtoShow;
    }

    /**
     * If no displayName was specified then the i18n key, then {@link # I18N_PRESENTATION_ITEM_PREFIX} +
     * {@code name_of_this_item} + {@link NamedThing#I18N_DISPLAY_NAME_SUFFIX} to find the value from the i18n.
     */
    @Override
    public String getDisplayName() {
        return displayName != null ? displayName
                : getI18nMessage(I18N_PRESENTATION_ITEM_PREFIX + name + NamedThing.I18N_DISPLAY_NAME_SUFFIX);
    }

    @Override
    public String toString() {
        return "Presentation Item: " + getName() + " - " + getDisplayName();
    }

    @Override
    public void accept(AnyPropertyVisitor visitor, Properties parent) {
        // do nothing
    }

}
