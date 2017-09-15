package org.talend.daikon.serialize.jsonschema;

import static org.talend.daikon.properties.presentation.Widget.widget;

import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class FilterRowCriteriaProperties extends PropertiesImpl {

    public Property<String> columnName = PropertyFactory.newString("columnName", "");

    public Property<String> function = PropertyFactory.newString("function", "EMPTY", "EMPTY");

    public Property<String> operator = PropertyFactory.newString("operator", "==");

    public Property<String> value = PropertyFactory.newString("value", "");

    public FilterRowCriteriaProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(widget(columnName).setWidgetType(Widget.DATALIST_WIDGET_TYPE));
        mainForm.addColumn(function);
        mainForm.addColumn(operator);
        mainForm.addColumn(value);
    }
}
