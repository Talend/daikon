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
import java.util.Map;

import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.PropertiesVisitor;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.strings.ToStringIndent;
import org.talend.daikon.strings.ToStringIndentUtil;

/**
 * The {@code Widget} class defines the presentation characteristics of the property within its {@link Form}.
 *
 * A {@code Widget} may also refer to a {@link Form}.
 */
public class Widget implements ToStringIndent {

    /*
     * Widget types
     */

    /**
     * No special widget is requested, the default for the property's type is to be used.
     */
    public static final String DEFAULT_WIDGET_TYPE = "widget.type.default";

    /**
     * Presentation of a schema editor.
     */
    public static final String SCHEMA_EDITOR_WIDGET_TYPE = "widget.type.schema.editor";

    /**
     * Presentation of a reference to a schema on one line. This shows the name of the schema and provides a button to
     * open the schema editor/viewer in a dialog.
     */
    public static final String SCHEMA_REFERENCE_WIDGET_TYPE = "widget.type.schema.reference";

    /**
     * Provides a means of selecting a name or name/description from a set of names, possibly arranged in a hierarchy.
     * This is to be used for a large number of names, as this has search capability.
     *
     * The NAME_SELECTION_AREA will operate on a property whose occur max times is -1, and whose possible values can be
     * picked in {@code List<NamedThing>} and that will set the values as another {@code List<NamedThing>}. It will show
     * everything on the list and then once complete will set the values of the list only to those that are selected.
     */
    public static final String NAME_SELECTION_AREA_WIDGET_TYPE = "widget.type.name.selection.area";

    /**
     * A reference to a named selection. This just shows the selected name and a button to get a dialog that has the
     * {@link #NAME_SELECTION_AREA_WIDGET_TYPE}.
     */
    public static final String NAME_SELECTION_REFERENCE_WIDGET_TYPE = "widget.type.name.selection.widget";

    /**
     * A reference to a component. This could be a reference to this component, another single component in the
     * enclosing scope's type, or a specified component instance. This is rendered as a single line with the type of
     * reference in a combo box. This is only used with the components system in conjunction with the
     * {@code ComponentReferenceProperties}.
     */
    public static final String COMPONENT_REFERENCE_WIDGET_TYPE = "widget.type.component.reference";

    /**
     * A button
     */
    public static final String BUTTON_WIDGET_TYPE = "widget.type.button";

    /**
     * A table, the widget content shall be a {@link Properties} that will provide a MAIN form (see {@link Form#MAIN}).
     * The main form shall contain a list of widget that will represent each table column and which content should be a
     * Property. Each Property is going to be used as the column definition, the {@link Property#getDisplayName()} shall
     * be used as the Column header. Each Property (=column) has a value of type List<T> in which the first element is
     * the first row element for this column and the second in the list is the second row value for this column.
     *
     */
    public static final String TABLE_WIDGET_TYPE = "widget.type.table";

    /*
     * a Text editable widget that hides the text to the user, mainly used for passwords.
     */
    public static final String HIDDEN_TEXT_WIDGET_TYPE = "widget.type.hidden.text";

    /**
     * A file widget with button select file from local system. The widget content shall be a {@link Property}<
     * {@link String}>
     */
    public static final String FILE_WIDGET_TYPE = "widget.type.file";

    /**
     * A directory widget with button select directory from local system. The widget content shall be a {@link Property}
     * < {@link String}>
     */
    public static final String DIRECTORY_WIDGET_TYPE = "widget.type.directory";

    /**
     * Tell the client that the property possible values ({@link Property#getPossibleValues()} must be used as unique
     * choice for the value of the property.
     **/
    public static final String ENUMERATION_WIDGET_TYPE = "widget.type.enumeration";

    public static final String TEXT_AREA_WIDGET_TYPE = "widget.type.textArea";

    /**
     * now it works for dataprep only
     */
    public static final String RADIO_WIDGET_TYPE = "widget.type.radio";

    /**
     * now it works for dataprep only
     */
    public static final String SELECT_WIDGET_TYPE = "widget.type.select";

    /**
     * This widget is designed to select a dataprep preparation with data Dataprep Picker.
     */
    public static final String SELECT_WIZARD_WIDGET_TYPE = "widget.type.select.wizard";

    /**
     * This widget will allow the UI to access an external link.
     */
    public static final String EXTERNAL_LINK_WIDGET_TYPE = "widget.type.external.link";

    /**
     * now it works for dataprep only
     */
    public static final String DATALIST_WIDGET_TYPE = "widget.type.datalist";

    /**
     * A table widget which use json as input. Below is a sample:
     * 
     * <pre>
     * {
     *  "columnNames": [
     *      "Id",
     *      "Name",
     *      "Age"
     *  ],
     *  "data": [
     *      [
     *          "1",
     *          "Tom",
     *          "22"
     *      ],
     *      [
     *          "2",
     *          "Mike",
     *          "33"
     *      ],
     *      [
     *          "3",
     *          "Lucy",
     *          "18"
     *      ]
     *  ]
     * }
     * </pre>
     * 
     * Notes: The "columnNames" is a column names array. If there is not this element table will use "Column 1, Column
     * 2, ..." as column names by default. The "data" is a data array. Every subset is a row of table.
     */
    public static final String JSON_TABLE_WIDGET_TYPE = "widget.type.jsonTable";

    /**
     * This widget represents a multiple checkbox which allow to select or deselect each value among a list of values.
     *
     * It is backed by a Property<List<String>> for the java model where the possible values are the one displayed in
     * the widget for selection.
     */
    public static final String MULTIPLE_VALUE_SELECTOR_WIDGET_TYPE = "widget.type.listview";

    /**
     * This widget represents a nested properties widget - a list of additional properties which can be added or
     * removed.
     * 
     * We can set the ui:options type of nested properties by setting a configuration value with the following key:
     * {@link Widget#NESTED_PROPERTIES_TYPE_OPTION}
     *
     * Using the following method: {@link Widget#setConfigurationValue(String, Object)}
     *
     * Default value is "default"
     *
     * Example:
     * setConfigurationValue(NESTED_PROPERTIES_TYPE_OPTION, "filter")
     * 
     */
    public static final String NESTED_PROPERTIES = "widget.type.nestedProperties";

    public static final String NESTED_PROPERTIES_TYPE_OPTION = "type";

    /**
     * This widget represents a nested properties widget - a list of additional properties which can be added or
     * removed - that cannot be collapsed.
     *
     * We can set the ui:options type of nested properties by setting a configuration value with the following key:
     * {@link Widget#NESTED_PROPERTIES_TYPE_OPTION}
     *
     * Using the following method: {@link Widget#setConfigurationValue(String, Object)}
     *
     * Default value is "default"
     *
     * Example:
     * setConfigurationValue(NESTED_PROPERTIES_TYPE_OPTION, "filter")
     */
    public static final String UNCOLLAPSIBLE_NESTED_PROPERTIES = "widget.type.uncollapsibleNestedProperties";

    /**
     * This widget represents a nested properties widget - a list of additional properties which can be added or
     * removed - that will be displayed on the UI side by side.
     *
     * We can set the ui:options type of nested properties by setting a configuration value with the following key:
     * {@link Widget#NESTED_PROPERTIES_TYPE_OPTION}
     *
     * Using the following method: {@link Widget#setConfigurationValue(String, Object)}
     *
     * Default value is "default"
     *
     * Example:
     * setConfigurationValue(NESTED_PROPERTIES_TYPE_OPTION, "filter")
     */
    public static final String COLUMNS_PROPERTIES = "widget.type.columns";

    /**
     * This widget is designed to simplify and speed up input of source code.
     *
     * We can set the language that will be used for this code widget by setting a configuration value with the following key:
     * {@link Widget#CODE_SYNTAX_WIDGET_CONF}
     *
     * Using the following method: {@link Widget#setConfigurationValue(String, Object)}
     *
     * Python is the default language if no configuration value for code syntax is specified.
     *
     * Example:
     * setConfigurationValue(CODE_SYNTAX_WIDGET_CONF, "java")
     */
    public static final String CODE_WIDGET_TYPE = "widget.type.code";

    public static final String CODE_SYNTAX_WIDGET_CONF = "language";

    /*
     * Widget configurations
     */

    /**
     * Tell the client whether the widget is readonly or not.
     */
    public static final String READ_ONLY_WIDGET_CONF = "widget.conf.readonly";

    /**
     * Tell the client whether the widget is autofocus or not.
     */
    public static final String AUTO_FOCUS_WIDGET_CONF = "widget.conf.autofocus";

    /**
     * Tell the client whether hide the toolbar or not. For example can set it as "true" to hide the toolbar of a table
     * widget.
     */
    public static final String HIDE_TOOLBAR_WIDGET_CONF = "widget.conf.hide.toolbar";

    /**
     * The placeholder is a value to show to the user if they haven't yet specified a value.
     */
    public static final String PLACEHOLDER_WIDGET_CONF = "widget.conf.placeholder";

    /**
     * The row in the form where this property is to be presented. Starting with 1.
     */
    private int row;

    /**
     * The order in the row where this property is to be presented. Starting with 1.
     */
    private int order;

    private boolean hidden;

    /**
     * The type of widget to be used to express this property. This is used only if there is a choice given the type of
     * property.
     */
    private String widgetType = DEFAULT_WIDGET_TYPE;

    /**
     * Is the validation associated with this expected to be long running (so that the UI should give a wait indication.
     * This is for things like doing a connection or loading data from a database.
     */
    private boolean longRunning;

    /**
     * This property is to be deemphasized in the UI. For example, it can be right-justified (in a LtoR UI) to keep the
     * description out of the column of the descriptions of the other properties that might be in a column.
     */
    private boolean deemphasize;

    //
    // Internal properties set by the component framework
    //

    private boolean callBeforeActivate;

    private boolean callBeforePresent;

    private boolean callValidate;

    private boolean callAfter;

    private NamedThing content;

    private Map<String, Object> configurationValues = new HashMap<>();

    public Widget(NamedThing content) {
        this.content = content;
    }

    public static Widget widget(NamedThing content) {
        return new Widget(content);
    }

    public NamedThing getContent() {
        return content;
    }

    public int getRow() {
        return this.row;
    }

    public Widget setRow(int row) {
        this.row = row;
        return this;
    }

    public int getOrder() {
        return this.order;
    }

    public Widget setOrder(int order) {
        this.order = order;
        return this;
    }

    /**
     * Set or reset this as hidden and mark the underlying {@link Property} or {@link Form} as hidden or not.
     */
    public Widget setHidden(boolean hidden) {
        this.hidden = hidden;
        if (content == null) {
            return this;
        }

        if (content instanceof Form) {
            // Recurse to change visibility to nested Forms
            ((Form) content).setHidden(hidden);
        } else {
            setHiddenProperty(content, hidden);
        }
        return this;
    }

    private void setHiddenProperty(Object content, boolean hidden) {
        if (content instanceof Property) {
            // Persist this with the underlying property
            Property prop = (Property) content;
            if (hidden) {
                prop.addFlag(Property.Flags.HIDDEN);
            } else {
                prop.removeFlag(Property.Flags.HIDDEN);
            }
        } else if (content instanceof Properties) {
            ((Properties) content).accept(new PropertiesVisitor() {

                @Override
                public void visit(Properties properties, Properties parent) {
                    for (NamedThing namedThing : properties.getProperties()) {
                        if (namedThing instanceof Property || namedThing instanceof Properties) {
                            setHiddenProperty(namedThing, hidden);
                        }
                    }
                }
            }, null);
        }
    }

    /**
     * Set or reset this as hidden and mark the underlying {@link Property} or {@link Form} as hidden or not.
     */
    public Widget setHidden() {
        return setHidden(true);
    }

    /**
     * Set or reset this as visible and mark the underlying {@link Property} or {@link Form} as visible or not.
     */
    public Widget setVisible(boolean visible) {
        return setHidden(!visible);
    }

    /**
     * Set or reset this as visible and mark the underlying {@link Property} or {@link Form} as visible or not.
     */
    public Widget setVisible() {
        return setVisible(true);
    }

    public boolean isHidden() {
        return hidden;
    }

    /**
     * Set or reset this as hidden and mark the underlying {@link Property} or {@link Form} as hidden or not.
     */
    public Widget setHidden(Property<Boolean> hidden) {
        return setHidden(hidden.getValue());
    }

    /**
     * return if the current Widget is visible or not.
     */
    public boolean isVisible() {
        return !hidden;
    }

    /**
     * Set or reset this as visible and mark the underlying {@link Property} or {@link Form} as visible or not.
     */
    public Widget setVisible(Property<Boolean> visible) {
        return setHidden(!visible.getValue());
    }

    public String getWidgetType() {
        return widgetType;
    }

    public Widget setWidgetType(String widgetType) {
        this.widgetType = widgetType;
        return this;
    }

    public boolean isLongRunning() {
        return longRunning;
    }

    public Widget setLongRunning(boolean longRunning) {
        this.longRunning = longRunning;
        return this;
    }

    public boolean isDeemphasize() {
        return deemphasize;
    }

    public Widget setDeemphasize(boolean deemphasize) {
        this.deemphasize = deemphasize;
        return this;
    }

    /**
     * For internal use only.
     */
    public boolean isCallBeforeActivate() {
        return callBeforeActivate;
    }

    /**
     * For internal use only, this is automatically set by the framework.
     */
    public void setCallBefore(boolean callBefore) {
        if (widgetType.equals(SCHEMA_REFERENCE_WIDGET_TYPE) || widgetType.equals(NAME_SELECTION_REFERENCE_WIDGET_TYPE)) {
            this.callBeforeActivate = callBefore;
            this.callBeforePresent = !callBefore;
        } else {
            this.callBeforePresent = callBefore;
            this.callBeforeActivate = !callBefore;
        }
    }

    /**
     * For internal use only.
     */
    public boolean isCallBeforePresent() {
        return callBeforePresent;
    }

    /**
     * For internal use only.
     */
    public boolean isCallValidate() {
        return callValidate;
    }

    /**
     * For internal use only, this is automatically set by the framework.
     */
    public void setCallValidate(boolean callValidate) {
        this.callValidate = callValidate;
    }

    /**
     * For internal use only.
     */
    public boolean isCallAfter() {
        return callAfter;
    }

    /**
     * For internal use only, this is automatically set by the framework.
     */
    public void setCallAfter(boolean callAfter) {
        this.callAfter = callAfter;
    }

    public boolean isReadonly() {
        return Boolean.valueOf(String.valueOf(getConfigurationValue(READ_ONLY_WIDGET_CONF)));
    }

    public Widget setReadonly(boolean readonly) {
        setConfigurationValue(READ_ONLY_WIDGET_CONF, readonly);
        return this;
    }

    public boolean isAutoFocus() {
        return Boolean.valueOf(String.valueOf(getConfigurationValue(AUTO_FOCUS_WIDGET_CONF)));
    }

    public Widget setAutoFocus(boolean autoFocus) {
        setConfigurationValue(AUTO_FOCUS_WIDGET_CONF, autoFocus);
        return this;
    }

    /**
     * Return the previously stored value using {@link Widget#setConfigurationValue(String, Object)} with the given key.
     *
     * @param key, identify the value to be fetched
     * @return the object stored along with the key or null if none found.
     */
    public Object getConfigurationValue(String key) {
        return configurationValues.get(key);
    }

    /**
     * This stores a value with the given key in a map. It is only settable in {@link Properties#setupLayout()} because
     * it may not be serialized.
     *
     * @param key, key to store the object with
     * @param value, any object.
     * @return this widget.
     */
    public Widget setConfigurationValue(String key, Object value) {
        configurationValues.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return toStringIndent(0);
    }

    @Override
    public String toStringIndent(int indent) {
        StringBuilder sb = new StringBuilder();
        String is = ToStringIndentUtil.indentString(indent);
        sb.append(is + "Widget: " + getWidgetType() + " " + getRow() + "/" + getOrder() + " ");
        NamedThing n = getContent();
        if (n instanceof Form) {
            sb.append("Form: ");
        }
        sb.append(n.getName());
        if (n instanceof Form) {
            sb.append(" (props: " + ((Form) n).getProperties().getName() + ")");
        }
        if (n instanceof Property) {
            Collection values = ((Property) n).getPossibleValues();
            if (values != null) {
                sb.append(" Values: " + values);
            }
        }
        if (isCallBeforeActivate()) {
            sb.append(" CALL_BEFORE_ACTIVATE");
        }
        if (isCallBeforePresent()) {
            sb.append(" CALL_BEFORE_PRESENT");
        }
        if (isCallAfter()) {
            sb.append(" CALL_AFTER");
        }
        if (isCallValidate()) {
            sb.append(" CALL_VALIDATE");
        }
        return sb.toString();
    }

}
