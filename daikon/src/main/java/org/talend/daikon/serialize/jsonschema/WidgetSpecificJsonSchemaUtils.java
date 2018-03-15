package org.talend.daikon.serialize.jsonschema;

import org.apache.commons.lang3.StringUtils;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * This class contains some methods that are coupling the UI schema and the Json schema. Normally the UISchema and the
 * Json schema should not be coupled. But for some specific cases we need that.
 *
 */
class WidgetSpecificJsonSchemaUtils {

    private WidgetSpecificJsonSchemaUtils() {
    }

    /**
     * Adds the <tt>uniqueItems</tt> <tt>minItems</tt> tags to the <i>schema</i> if the property corresponds to a list view.
     *
     * Normally the UISchema and the Json schema should not be coupled. But for the specific case of the list View component
     * the json schema must be tagged with <tt>uniqueItems</tt> and <tt>minItems</tt>.
     *
     * @param form the specified form
     * @param property the specified property
     * @param schema the schema to enrich with the "uniqueItems" tag
     */
    static void listViewSpecific(Form form, Property property, ObjectNode schema) {

        if (form != null && form.getWidgets() != null) {
            Widget widget = form.getWidget(property.getName());
            if (widget != null) {
                String widgetType = widget.getWidgetType();
                if (StringUtils.equals(Widget.MULTIPLE_VALUE_SELECTOR_WIDGET_TYPE, widgetType)) {
                    schema.put(JsonSchemaConstants.TAG_UNIQUE_ITEMS, true);
                    schema.put(JsonSchemaConstants.TAG_MIN_ITEMS, 1);
                }

                // TDKN-179: ensure "min_items=0" if widget is hidden in form to prevent validation issues.
                if (widget.isHidden()) {
                    schema.put(JsonSchemaConstants.TAG_MIN_ITEMS, 0);
                }
            }

        }
    }

}
