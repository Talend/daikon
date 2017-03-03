package org.talend.daikon.serialize.jsonschema;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.talend.daikon.properties.presentation.Widget;

/**
 * https://github.com/mozilla-services/react-jsonschema-form
 */
public class UiSchemaConstants {

    /**
     * Build-in tag. Represent the widget type is build-in
     */
    public static final String TAG_WIDGET = "ui:widget";

    /**
     * Build-in tag. Represent the widget row order, do not support column order
     */
    public static final String TAG_ORDER = "ui:order";

    /**
     * Custom tag. Represent the widget trigger. It could be
     * TRIGGER_AFTER/TRIGGER_BEFORE_PRESENT/TRIGGER_BEFORE_ACTIVATE/TRIGGER_VALIDATE
     */
    public static final String TAG_TRIGGER = "ui:trigger";

    /** @deprecated please use PropertyTrigger#AFTER */
    @Deprecated
    public static final String TRIGGER_AFTER = PropertyTrigger.AFTER.name();

    /** @deprecated please use PropertyTrigger#BEFORE_PRESENT */
    @Deprecated
    public static final String TRIGGER_BEFORE_PRESENT = PropertyTrigger.BEFORE_PRESENT.name();

    /** @deprecated please use PropertyTrigger#BEFORE_ACTIVE */
    @Deprecated
    public static final String TRIGGER_BEFORE_ACTIVATE = PropertyTrigger.BEFORE_ACTIVE.name();

    /** @deprecated please use PropertyTrigger#VALIDATE */
    @Deprecated
    public static final String TRIGGER_VALIDATE = PropertyTrigger.VALIDATE.name();

    /** @deprecated please use PropertyTrigger#SHOW_FORM */
    @Deprecated
    public static final String TRIGGER_SHOW_FORM = PropertyTrigger.SHOW_FORM.name();

    /**
     * Built-in widget type. Display the character as * to hidden the actually character
     */
    public static final String TYPE_PASSWORD = "password";

    /**
     * Built-in widget type. With a button which let the user select file from local system
     */
    public static final String TYPE_FILE = "file";

    /**
     * Built-in widget type. Do not display
     */
    public static final String TYPE_HIDDEN = "hidden";

    /**
     * Built-in widget type. Multiple lines text field
     */
    public static final String TYPE_TEXT_AREA = "textarea";

    /**
     * Custom widget type. Disply a table, with a fixed header, and user can add row below the header
     */
    public static final String CUSTOM_TYPE_TABLE = "table";

    /**
     * Custom widget type. Display a schema editor, which let the user configure the Columns metadata
     */
    public static final String CUSTOM_TYPE_SCHEMA = "schema";

    /**
     * Custom widget type. Display a button
     */
    public static final String CUSTOM_TYPE_BUTTON = "button";

    // Mapping between Widget type and ui-schema type
    private static Map<String, String> WIDGET_MAPPING = new HashMap<>();

    static {
        // custom widget type for UISchema
        WIDGET_MAPPING.put(Widget.TABLE_WIDGET_TYPE, UiSchemaConstants.CUSTOM_TYPE_TABLE);
        WIDGET_MAPPING.put(Widget.SCHEMA_EDITOR_WIDGET_TYPE, UiSchemaConstants.CUSTOM_TYPE_SCHEMA);
        WIDGET_MAPPING.put(Widget.SCHEMA_REFERENCE_WIDGET_TYPE, UiSchemaConstants.CUSTOM_TYPE_SCHEMA);
        WIDGET_MAPPING.put(Widget.BUTTON_WIDGET_TYPE, UiSchemaConstants.CUSTOM_TYPE_BUTTON);
        // default widget type for UISchema
        WIDGET_MAPPING.put(Widget.HIDDEN_TEXT_WIDGET_TYPE, UiSchemaConstants.TYPE_PASSWORD);
        WIDGET_MAPPING.put(Widget.FILE_WIDGET_TYPE, UiSchemaConstants.TYPE_FILE);
        WIDGET_MAPPING.put(Widget.TEXT_AREA_WIDGET_TYPE, UiSchemaConstants.TYPE_TEXT_AREA);
        // null means use the default
        // WIDGET_MAPPING.put(Widget.DEFAULT_WIDGET_TYPE, null);
        // WIDGET_MAPPING.put(Widget.NAME_SELECTION_AREA_WIDGET_TYPE, null);
        // WIDGET_MAPPING.put(Widget.NAME_SELECTION_REFERENCE_WIDGET_TYPE, null);
        // WIDGET_MAPPING.put(Widget.COMPONENT_REFERENCE_WIDGET_TYPE, null);
        // WIDGET_MAPPING.put(Widget.ENUMERATION_WIDGET_TYPE, null);
        WIDGET_MAPPING = Collections.unmodifiableMap(WIDGET_MAPPING);
    }

    public static Map<String, String> getWidgetMapping() {
        return WIDGET_MAPPING;
    }

}
