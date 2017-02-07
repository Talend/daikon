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
package org.talend.daikon.properties.presentation;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.i18n.GlobalI18N;
import org.talend.daikon.i18n.I18nMessages;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.PropertiesDynamicMethodHelper;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.strings.ToStringIndent;
import org.talend.daikon.strings.ToStringIndentUtil;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * Represents a collection of {@link Property} objects that are grouped into a form for display. This form
 * can be manifested for example as a tab in a view, a dialog, or a page in a wizard. It can also be a portion of any of those
 * things, as forms can contain other forms.
 */
public class Form extends SimpleNamedThing implements ToStringIndent {

    /**
     * Prefix used in the i18n properties file for translatable strings for form attributes
     */
    public static final String I18N_FORM_PREFIX = "form."; //$NON-NLS-1$

    /**
     * Standard form name for the main form associated with a component.
     *
     * This has no significance in the Framework, it's just a usage convention.
     */
    public static final String MAIN = "Main"; //$NON-NLS-1$

    /**
     * Standard form name for the citizen-user form associated with a component. This would typically
     * have less content than the MAIN form, and is suitable for the less technical user.
     *
     * This has no significance in the Framework, it's just a usage convention.
     */
    public static final String CITIZEN_USER = "CitizenUser"; //$NON-NLS-1$

    /**
     * Standard form name for advanced properties associated with a component.
     *
     * This has no significance in the Framework, it's just a usage convention.
     */
    public static final String ADVANCED = "Advanced"; //$NON-NLS-1$

    /**
     * Standard form name for a form that references something (like a schema or other component), to be included in
     * some other form.
     *
     * This has no significance in the Framework, it's just a usage convention.
     */
    public static final String REFERENCE = "Reference";

    /**
     * Suffix used in the i18n properties file for translatable strings for subtitle value
     */
    public static final String I18N_SUBTITLE_NAME_SUFFIX = ".subtitle"; //$NON-NLS-1$

    /**
     * Subtitle of the form.
     */
    protected String subtitle;

    @JsonBackReference
    protected Properties properties;

    protected LinkedHashMap<String, Widget> widgetMap;

    transient private int lastRow;

    transient private int lastColumn;

    private boolean cancelable;

    // Stores the value temporarily in case the are canceled
    private Map<String, Object> originalValues;

    private boolean callBeforeFormPresent;

    private boolean callAfterFormBack;

    private boolean callAfterFormNext;

    private boolean callAfterFormFinish;

    private boolean allowBack;

    private boolean allowForward;

    private boolean allowFinish;

    /**
     * Indicate that some {@link Widget} objects for this form have changed and the UI should be re-rendered to reflect
     * the changed widget.
     */
    protected boolean refreshUI;

    private Form() {// used for deserialization
    }

    /**
     * Create a form that will get title from i18n properties file.
     *
     * @param props the related {@link Properties}, never null
     * @param name name of the form, not for display
     */
    public Form(Properties props, String name) {
        super(name, null);
        widgetMap = new LinkedHashMap<>();
        props.addForm(this);
        properties = props;
        PropertiesDynamicMethodHelper.setFormLayoutMethods(props, name, this);
    }

    /**
     * Create a form that will get title from i18n properties file.
     *
     * @param props the related {@link Properties}, never null
     * @param name name of the form, not for display
     */
    public static Form create(Properties props, String name) {
        return new Form(props, name);
    }

    @Override
    protected I18nMessages createI18nMessageFormater() {
        return GlobalI18N.getI18nMessageProvider().getI18nMessages(properties.getClass());
    }

    public Form setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Get the internationalized version of the displayName, with {@link Form#I18N_FORM_PREFIX} +
     * {@link Form#getName()} + {@link NamedThing#I18N_DISPLAY_NAME_SUFFIX} to find the value from the i18n.
     */
    @Override
    public String getDisplayName() {
        return displayName != null ? displayName : getI18nMessage(I18N_FORM_PREFIX + name + NamedThing.I18N_DISPLAY_NAME_SUFFIX);
    }

    /**
     * Generally not used, as the value is calculated from the i18n properties.
     * 
     * @see NamedThing#getTitle()
     */
    public Form setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * If no title was specified (using {@link #setTitle(String)}) then the value is calculated using the i18n key:
     * {@link Form#I18N_FORM_PREFIX} + {@link Form#getName()} + {@link NamedThing#I18N_TITLE_NAME_SUFFIX}.
     */
    @Override
    public String getTitle() {
        return title != null ? title : getI18nMessage(I18N_FORM_PREFIX + name + NamedThing.I18N_TITLE_NAME_SUFFIX);
    }

    /**
     * Generally not used, as the value is calculated from the i18n properties.
     * 
     * @see #getSubtitle()
     */
    public Form setSubtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    /**
     * If no title was specified (using {@link #setTitle(String)} then the value is calculated using the i18n key:
     * {@link Form#I18N_FORM_PREFIX} + {@link Form#getName()} + {@link #I18N_SUBTITLE_NAME_SUFFIX}.
     */
    public String getSubtitle() {
        return subtitle != null ? subtitle : getI18nMessage(I18N_FORM_PREFIX + name + I18N_SUBTITLE_NAME_SUFFIX);
    }

    /**
     * Return all of the {@link Widget}s contained by this form.
     */
    public Collection<Widget> getWidgets() {
        return widgetMap.values();
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Used to add a {@link Property}, {@link Properties}, or {@link Form} as a {@link Widget} in this form in the next
     * row and first column.
     */
    public Form addRow(NamedThing child) {
        addRow(Widget.widget(child));
        return this;
    }

    /**
     * Used to add a {@link Property}, {@link Properties}, or {@link Form} as a {@link Widget} in this form as the next
     * column of the current row.
     */
    public Form addColumn(NamedThing child) {
        addColumn(Widget.widget(child));
        return this;
    }

    /**
     * Add the widget in the next row and first column.
     */
    public Form addRow(Widget widget) {
        lastColumn = 1;
        String widgetName = getWidgetContentName(widget);
        widgetMap.put(widgetName, widget.setRow(++lastRow).setOrder(lastColumn));
        PropertiesDynamicMethodHelper.setWidgetLayoutMethods(properties, widgetName, widget);
        return this;
    }

    /**
     * Add the widget in the next column of current row.
     */
    public Form addColumn(Widget widget) {
        String widgetName = getWidgetContentName(widget);
        widgetMap.put(widgetName, widget.setRow(lastRow).setOrder(++lastColumn));
        PropertiesDynamicMethodHelper.setWidgetLayoutMethods(properties, widgetName, widget);
        return this;
    }

    /**
     * Change the visibility status of all of the {@code Form}'s widgets.
     */
    public void setHidden(boolean hidden) {
        for (Widget w : getWidgets()) {
            w.setHidden(hidden);
        }
    }

    /**
     * Change the visibility status of all of the {@code Form}'s widgets.
     */
    public void setVisible(boolean visible) {
        for (Widget w : getWidgets()) {
            w.setVisible(visible);
        }
    }

    /**
     * Return widget name from its contents
     */
    private String getWidgetContentName(Widget widget) {
        NamedThing child = widget.getContent();
        String widgetName = child.getName();
        /*
         * We don't use the form name since that's not going to necessarily be unique within the form's list of
         * properties. The Properties object associated with the form will have a unique name within the enclosing
         * Properties (and therefore this Form).
         */
        if (child instanceof Form) {
            widgetName = ((Form) child).getProperties().getName();
        }
        if (widgetName == null) {
            throw new NullPointerException();
        }
        return widgetName;
    }

    /**
     * Return the {@link Widget} with the specified name.
     */
    public Widget getWidget(String child) {
        return widgetMap.get(child);
    }

    /**
     * Return the {@link Widget} of a NamedThing.
     */
    public Widget getWidget(NamedThing child) {
        return widgetMap.get(child.getName());
    }

    /**
     * Uses the class name to get a {@link Widget}.
     *
     * @param childClass the Class of the desired {@link Properties} to get.
     * @return the {@code Widget} belonging to those properties.
     */
    public Widget getWidget(Class<?> childClass) {
        for (Widget w : widgetMap.values()) {
            NamedThing p = w.getContent();
            // See comment above in fill()
            if (p instanceof Form) {
                p = ((Form) p).getProperties();
            }
            if (p.getClass() == childClass) {
                return w;
            }
        }
        return null;
    }

    /**
     * Returns a {@link Form} that is contained in this form with the specified name.
     */
    public Form getChildForm(String child) {
        Widget w = getWidget(child);
        return (Form) w.getContent();
    }

    /**
     * Sets the value of the given property to the specified value.
     *
     * If the form is cancelable (see
     * {@link org.talend.daikon.properties.service.PropertiesService#makeFormCancelable(Properties, String)}) the values
     * can be reset to the original values when
     * {@link org.talend.daikon.properties.service.PropertiesService#cancelFormValues(Properties, String)} is called.
     * <p/>
     */
    // FIXME - TDKN-67 remove the cancelable through the service
    public void setValue(String property, Object value) {
        if (property.contains(".")) {
            throw new IllegalArgumentException(
                    "Cannot setValue on a qualified property: '" + property + "', use the Form associated with the property.");
        }
        NamedThing se = getProperties().getProperty(property);
        if (!(se instanceof Property)) {
            throw new IllegalArgumentException("Attempted to set value on " + se + " which is not a Property");
        }
        Property p = (Property) se;
        if (cancelable) {
            if (originalValues == null) {
                throw new IllegalStateException("Cannot setValue on " + property + " after cancelValues() has been called");
            }
            if (!originalValues.containsKey(property)) {
                originalValues.put(property, p.getValue());
            }
        }
        p.setValue(value);
    }

    /**
     * For internal use only.
     */
    // FIXME - TDKN-67 remove the cancelable through the service
    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        originalValues = null;
        if (cancelable) {
            originalValues = new HashMap<>();
        }
        for (Widget w : widgetMap.values()) {
            NamedThing p = w.getContent();
            if (p instanceof Form) {
                ((Form) p).setCancelable(cancelable);
            }
        }
    }

    /**
     * For internal use only.
     */
    // FIXME - TDKN-67 remove the cancelable through the service
    public void cancelValues() {
        if (originalValues == null) {
            return;
        }
        for (String key : originalValues.keySet()) {
            ((Property) getProperties().getProperty(key)).setValue(originalValues.get(key));
        }
        originalValues = null;
        for (Widget w : widgetMap.values()) {
            NamedThing p = w.getContent();
            if (p instanceof Form) {
                ((Form) p).cancelValues();
            }
        }
    }

    public boolean isRefreshUI() {
        return refreshUI;
    }

    public void setRefreshUI(boolean refreshUI) {
        this.refreshUI = refreshUI;
    }

    // FIXME - consider removing the word "Form" from these
    public boolean isCallBeforeFormPresent() {
        return callBeforeFormPresent;
    }

    public void setCallBeforeFormPresent(boolean callBeforeFormPresent) {
        this.callBeforeFormPresent = callBeforeFormPresent;
    }

    public boolean isCallAfterFormBack() {
        return callAfterFormBack;
    }

    public void setCallAfterFormBack(boolean callAfterFormBack) {
        this.callAfterFormBack = callAfterFormBack;
    }

    public boolean isCallAfterFormNext() {
        return callAfterFormNext;
    }

    public void setCallAfterFormNext(boolean callAfterFormNext) {
        this.callAfterFormNext = callAfterFormNext;
    }

    public boolean isCallAfterFormFinish() {
        return callAfterFormFinish;
    }

    public void setCallAfterFormFinish(boolean callAfterFormFinish) {
        this.callAfterFormFinish = callAfterFormFinish;
    }

    public boolean isAllowBack() {
        return allowBack;
    }

    public void setAllowBack(boolean allowBack) {
        this.allowBack = allowBack;
    }

    public boolean isAllowForward() {
        return allowForward;
    }

    public void setAllowForward(boolean allowForward) {
        this.allowForward = allowForward;
    }

    public boolean isAllowFinish() {
        return allowFinish;
    }

    public void setAllowFinish(boolean allowFinish) {
        this.allowFinish = allowFinish;
    }

    @Override
    public String toString() {
        return toStringIndent(0);
    }

    @Override
    public String toStringIndent(int indent) {
        StringBuilder sb = new StringBuilder();
        String is = ToStringIndentUtil.indentString(indent);
        sb.append(is + "Form: " + getName());
        if (isRefreshUI()) {
            sb.append(" REFRESH_UI");
        }
        if (isCallBeforeFormPresent()) {
            sb.append(" BEFORE_FORM_PRESENT");
        }
        if (isCallAfterFormBack()) {
            sb.append(" AFTER_FORM_BACK");
        }
        if (isCallAfterFormNext()) {
            sb.append(" AFTER_FORM_NEXT");
        }
        if (isCallAfterFormFinish()) {
            sb.append(" AFTER_FORM_FINISH");
        }
        if (isAllowBack()) {
            sb.append(" ALLOW_BACK");
        }
        if (isAllowForward()) {
            sb.append(" ALLOW_FORWARD");
        }
        if (isAllowFinish()) {
            sb.append(" ALLOW_FINISH");
        }
        for (Widget w : getWidgets()) {
            sb.append("\n" + w.toStringIndent(indent + 4));
        }
        return sb.toString();
    }

}
