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

import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.Property;
import org.talend.daikon.strings.ToStringIndent;
import org.talend.daikon.strings.ToStringIndentUtil;

/**
 * The {@code Widget} class defines the presentation characteristics of the property within its {@link Form}.
 */
public class Widget implements ToStringIndent {

    /**
     * No special widget is requested, the default for the property's type is to be used.
     */
    public static final String DEFAULT_WIDGET_TYPE = "widget.type.default";

    /**
     * Presentation of a schema editor.
     */
    public static final String SCHEMA_EDITOR_WIDGET_TYPE = "widget.type.schema.editor";

    /**
     * Presentation of a reference to a schema on one line. This shows the name of the schema and provides a button
     * to open the schema editor/viewer in a dialog.
     */
    public static final String SCHEMA_REFERENCE_WIDGET_TYPE = "widget.type.schema.reference";

    /**
     * Provides a means of selecting a name or name/description from a set of names, possibly arranged in a
     * hierarchy. This is to be used for a large number of names, as this has search capability.
     *
     * The NAME_SELECTION_AREA will operate on a property whose occur max times is -1, and whose possible values can be picked
     * in {@code List<NamedThing>} and that will set the values as another {@code List<NamedThing>}. It will show everything
     * on the list and then once complete will set the values of the list only to those that are selected.
     */
    public static final String NAME_SELECTION_AREA_WIDGET_TYPE = "widget.type.name.selection.area";

    /**
     * A reference to a named selection. This just shows the selected name and a button to get a dialog that has the
     * {@link #NAME_SELECTION_AREA}.
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
     * A table, the widget content shall be a {@link Properties} that will provide a MAIN form (see
     * {@link Form#MAIN}). The main form shall contain a list of widget that will represent each table column and
     * which content should be a Property. Each Property is going to be used as the column definition, the
     * {@link Property#getDisplayName()} shall be used as the Column header. Each Property (=column) has a value of
     * type List<T> in which the first element is the first row element for this column and the second in the list
     * is the second row value for this column.
     * 
     */
    public static final String TABLE_WIDGET_TYPE = "widget.type.table";

    /*
     * a Text editable widget that hides the text to the user, mainly used for passwords.
     */
    public static final String HIDDEN_TEXT_WIDGET_TYPE = "widget.type.hidden.text";

    /*
     * a File widget.
     */
    public static final String FILE_WIDGET_TYPE = "widget.type.file";

    /**
     * Tell the client that the property possible values ({@link Property#getPossibleValues()} must be used as
     * unique choice for the value of the property.
     **/
    public static final String ENUMERATION_WIDGET_TYPE = "widget.type.enumeration";

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

    public static Widget widget(NamedThing content) {
        return new Widget(content);
    }

    public Widget(NamedThing content) {
        this.content = content;
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

    public Widget setHidden(boolean hidden) {
        this.hidden = hidden;
        if (content != null && content instanceof Form) {
            // Recurse to change visibility to nested Forms
            ((Form) content).setHidden(hidden);
        } else if (content != null && content instanceof Property) {
            // Persist this with the underlying property
            Property prop = (Property) content;
            if (hidden) {
                prop.addFlag(Property.Flags.HIDDEN);
            } else {
                prop.removeFlag(Property.Flags.HIDDEN);
            }
        }
        return this;
    }

    public boolean isHidden() {
        return hidden;
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

    //
    // These are automatically set by this framework; they are not to be specified by the user.
    //

    public boolean isCallBeforeActivate() {
        return callBeforeActivate;
    }

    public void setCallBefore(boolean callBefore) {
        if (widgetType == SCHEMA_REFERENCE_WIDGET_TYPE || widgetType == NAME_SELECTION_REFERENCE_WIDGET_TYPE) {
            this.callBeforeActivate = callBefore;
            this.callBeforePresent = !callBefore;
        } else {
            this.callBeforePresent = callBefore;
            this.callBeforeActivate = !callBefore;
        }
    }

    public boolean isCallBeforePresent() {
        return callBeforePresent;
    }

    public boolean isCallValidate() {
        return callValidate;
    }

    public void setCallValidate(boolean callValidate) {
        this.callValidate = callValidate;
    }

    public boolean isCallAfter() {
        return callAfter;
    }

    public void setCallAfter(boolean callAfter) {
        this.callAfter = callAfter;
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
