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
     * Build-in tag. Represent the widget type is custom
     */
    public static final String TAG_CUSTOM_WIDGET = "ui:field";

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

    /** @deprecated please use PropertyTrigger#after */
    @Deprecated
    public static final String TRIGGER_AFTER = PropertyTrigger.after.name();

    /** @deprecated please use PropertyTrigger#beforePresent */
    @Deprecated
    public static final String TRIGGER_BEFORE_PRESENT = PropertyTrigger.beforePresent.name();

    /** @deprecated please use PropertyTrigger#beforeActive */
    @Deprecated
    public static final String TRIGGER_BEFORE_ACTIVATE = PropertyTrigger.beforeActive.name();

    /** @deprecated please use PropertyTrigger#validate */
    @Deprecated
    public static final String TRIGGER_VALIDATE = PropertyTrigger.validate.name();

    /** @deprecated please use PropertyTrigger#showForm */
    @Deprecated
    public static final String TRIGGER_SHOW_FORM = PropertyTrigger.showForm.name();

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

    // Mapping between Widget type and ui-schema custom type, which is not supported out-of-box
    private static Map<String, String> CUSTOM_WIDGET_MAPPING = new HashMap<>();

    static {
        // table is a custom widget type for UISchema
        CUSTOM_WIDGET_MAPPING.put(Widget.TABLE_WIDGET_TYPE, UiSchemaConstants.CUSTOM_TYPE_TABLE);
        CUSTOM_WIDGET_MAPPING.put(Widget.SCHEMA_EDITOR_WIDGET_TYPE, UiSchemaConstants.CUSTOM_TYPE_SCHEMA);
        CUSTOM_WIDGET_MAPPING.put(Widget.SCHEMA_REFERENCE_WIDGET_TYPE, UiSchemaConstants.CUSTOM_TYPE_SCHEMA);
        CUSTOM_WIDGET_MAPPING.put(Widget.BUTTON_WIDGET_TYPE, UiSchemaConstants.CUSTOM_TYPE_BUTTON);

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
        CUSTOM_WIDGET_MAPPING = Collections.unmodifiableMap(CUSTOM_WIDGET_MAPPING);
    }

    public static Map<String, String> getWidgetMapping() {
        return WIDGET_MAPPING;
    }

    public static Map<String, String> getCustomWidgetMapping() {
        return CUSTOM_WIDGET_MAPPING;
    }
}
