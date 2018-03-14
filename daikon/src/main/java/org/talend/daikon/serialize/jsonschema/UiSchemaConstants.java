package org.talend.daikon.serialize.jsonschema;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.serialize.jsonschema.mapping.Mapper;
import org.talend.daikon.serialize.jsonschema.mapping.NestedPropertiesFieldMapper;
import org.talend.daikon.serialize.jsonschema.mapping.WidgetMapper;

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
     * Build-in tag. Represent the widget autofocus
     */
    public static final String TAG_AUTO_FOCUS = "ui:autofocus";

    /**
     * Custom tag. Represent the widget trigger. It could be
     * TRIGGER_AFTER/TRIGGER_BEFORE_PRESENT/TRIGGER_BEFORE_ACTIVATE/TRIGGER_VALIDATE
     */
    public static final String TAG_TRIGGER = "ui:trigger";

    /**
     * Build-in tag. Represent the widget options is build-in
     */
    public static final String TAG_OPTIONS = "ui:options";

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
     * Radio field
     */
    public static final String TYPE_RADIO = "radio";

    /**
     * Select field
     */
    public static final String TYPE_SELECT = "select";

    /**
     * Select field
     */
    public static final String TYPE_DATALIST = "datalist";

    /**
     * Built-in widget type. Multiple lines text field
     */
    public static final String TYPE_TEXT_AREA = "textarea";

    /**
     * Built-in widget type.
     */
    public static final String TYPE_CODE = "code";

    /**
     * Custom widget type. Disply a table, with a fixed header, and user can add row below the header
     */
    // Disabled for products that create forms via JSON Schema. Can be restored when Talend ui
    // forms have a schema editor.
    // public static final String CUSTOM_TYPE_TABLE = "table";
    public static final String CUSTOM_TYPE_TABLE = "hidden";

    /**
     * Custom widget type. Display a schema editor, which let the user configure the Columns metadata
     */
    // Disabled for products that create forms via JSON Schema. Can be restored when Talend ui
    // forms have a schema editor.
    // public static final String CUSTOM_TYPE_SCHEMA = "schema";
    public static final String CUSTOM_TYPE_SCHEMA = "hidden";

    /**
     * Custom widget type. Display a button
     */
    public static final String CUSTOM_TYPE_BUTTON = "button";

    /**
     * This widget represents a multiple checkbox which allow to select or deselect each value among a list of values.
     * It is backed by a Property<List<String>> for the java model where the possible values are the one displayed in
     * the widget for selection.
     */
    public static final String TYPE_LIST_VIEW = "listview";

    /**
     * This widget will open a Wizard and let the user select an object inside it. For example, it will be used
     * to select a Dataprep Preparation from the Dataprep Picker.
     */
    public static final String TYPE_SELECT_WIZARD = "selectWizard";

    /**
     * This widget will create a link button that will move the user to another webpage.
     */
    public static final String TYPE_EXTERNAL_LINK = "externalLink";

    public static final String TYPE_COLUMNS = "columns";

    /*
     * ui:field values. ui:field is a more high-level element than a ui:widget. Widgets are used to represent singular
     * form element, for example a checkbox or a list of such elements. In order to present more complex elements, like
     * nested properties, we need to use ui:field.
     */
    /**
     * Value for nested properties ui:field type. It will be presented at web-ui as a list of complex properties. Each
     * properties object will be presented in it's own subform.
     */
    public static final String COLLAPSIBLE_FIELDSET = "CollapsibleFieldset";

    // Mapping between Widget type and ui:options type
    private static Map<String, Map<String, String>> WIDGET_OPTIONS_MAPPING = new HashMap<>();

    private static Map<String, Mapper> UI_MAPPERS = new HashMap<>();

    static {
        Map<String, String> options = new HashMap<>();
        options.put("inline", "true");
        WIDGET_OPTIONS_MAPPING.put(Widget.RADIO_WIDGET_TYPE, options);

        options = new HashMap<>();
        options.put(Widget.CODE_SYNTAX_WIDGET_CONF, "python");
        WIDGET_OPTIONS_MAPPING.put(Widget.CODE_WIDGET_TYPE, options);

        options = new HashMap<>();
        options.put(Widget.NESTED_PROPERTIES_TYPE_OPTION, "default");
        WIDGET_OPTIONS_MAPPING.put(Widget.NESTED_PROPERTIES, options);
        WIDGET_OPTIONS_MAPPING.put(Widget.UNCOLLAPSIBLE_NESTED_PROPERTIES, options);

        options = new HashMap<>();
        options.put(Widget.NESTED_PROPERTIES_TYPE_OPTION, "default");
        WIDGET_OPTIONS_MAPPING.put(Widget.COLUMNS_PROPERTIES, options);

        // null means use the default
        // WIDGET_MAPPING.put(Widget.DEFAULT_WIDGET_TYPE, null);
        // WIDGET_MAPPING.put(Widget.NAME_SELECTION_AREA_WIDGET_TYPE, null);
        // WIDGET_MAPPING.put(Widget.NAME_SELECTION_REFERENCE_WIDGET_TYPE, null);
        // WIDGET_MAPPING.put(Widget.COMPONENT_REFERENCE_WIDGET_TYPE, null);
        // WIDGET_MAPPING.put(Widget.ENUMERATION_WIDGET_TYPE, null);

        // custom widget type for UISchema
        UI_MAPPERS.put(Widget.TABLE_WIDGET_TYPE, new WidgetMapper(UiSchemaConstants.CUSTOM_TYPE_TABLE));
        UI_MAPPERS.put(Widget.SCHEMA_EDITOR_WIDGET_TYPE, new WidgetMapper(UiSchemaConstants.CUSTOM_TYPE_SCHEMA));
        UI_MAPPERS.put(Widget.SCHEMA_REFERENCE_WIDGET_TYPE, new WidgetMapper(UiSchemaConstants.CUSTOM_TYPE_SCHEMA));
        UI_MAPPERS.put(Widget.BUTTON_WIDGET_TYPE, new WidgetMapper(UiSchemaConstants.CUSTOM_TYPE_BUTTON));
        // default widget type for UISchema
        UI_MAPPERS.put(Widget.HIDDEN_TEXT_WIDGET_TYPE, new WidgetMapper(UiSchemaConstants.TYPE_PASSWORD));
        UI_MAPPERS.put(Widget.FILE_WIDGET_TYPE, new WidgetMapper(UiSchemaConstants.TYPE_FILE));
        UI_MAPPERS.put(Widget.TEXT_AREA_WIDGET_TYPE, new WidgetMapper(UiSchemaConstants.TYPE_TEXT_AREA));

        UI_MAPPERS.put(Widget.RADIO_WIDGET_TYPE, new WidgetMapper(UiSchemaConstants.TYPE_RADIO));

        UI_MAPPERS.put(Widget.SELECT_WIDGET_TYPE, new WidgetMapper(UiSchemaConstants.TYPE_SELECT));
        UI_MAPPERS.put(Widget.DATALIST_WIDGET_TYPE, new WidgetMapper(UiSchemaConstants.TYPE_DATALIST));
        UI_MAPPERS.put(Widget.MULTIPLE_VALUE_SELECTOR_WIDGET_TYPE, new WidgetMapper(UiSchemaConstants.TYPE_LIST_VIEW));
        UI_MAPPERS.put(Widget.CODE_WIDGET_TYPE, new WidgetMapper(UiSchemaConstants.TYPE_CODE));

        UI_MAPPERS.put(Widget.SELECT_WIZARD_WIDGET_TYPE, new WidgetMapper(UiSchemaConstants.TYPE_SELECT_WIZARD));
        UI_MAPPERS.put(Widget.EXTERNAL_LINK_WIDGET_TYPE, new WidgetMapper(UiSchemaConstants.TYPE_EXTERNAL_LINK));

        UI_MAPPERS.put(Widget.NESTED_PROPERTIES, new NestedPropertiesFieldMapper(UiSchemaConstants.COLLAPSIBLE_FIELDSET));

        // for the UI, a datalist with an empty "ui:field" will be considered as an uncollapsible nested fieldset.
        UI_MAPPERS.put(Widget.UNCOLLAPSIBLE_NESTED_PROPERTIES, new NestedPropertiesFieldMapper(""));

        // for the UI, the UI need the "columns" field to be a widget instead of a tag.
        UI_MAPPERS.put(Widget.COLUMNS_PROPERTIES,
                new NestedPropertiesFieldMapper(UiSchemaConstants.TAG_WIDGET, UiSchemaConstants.TYPE_COLUMNS));
        UI_MAPPERS = Collections.unmodifiableMap(UI_MAPPERS);
    }

    public static Map<String, Map<String, String>> getWidgetOptionsMapping() {
        return WIDGET_OPTIONS_MAPPING;
    }

    public static Map<String, Mapper> getUiMappers() {
        return UI_MAPPERS;
    }

}
