package org.talend.daikon.serialize.jsonschema;

import static org.talend.daikon.properties.presentation.Widget.MULTIPLE_VALUE_SELECTOR_WIDGET_TYPE;
import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newStringList;

import java.util.Arrays;
import java.util.List;

import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public abstract class AbstractSchemaGenerator {

    public abstract PropertiesImpl getNestedProperties(String name);

    protected class AProperties extends PropertiesImpl {

        private static final long serialVersionUID = 1L;

        public final Property<String> myStr = PropertyFactory.newString("myStr");

        public final PropertiesImpl np = getNestedProperties("np");

        public final PropertiesImpl np2 = getNestedProperties("np2");

        public final PropertiesImpl np3 = getNestedProperties("np3");

        public final PropertiesImpl np4 = getNestedProperties("np4");

        public final PropertiesImpl np5 = getNestedProperties("np5");

        public AProperties(String name) {
            super(name);
        }

        @Override
        public void setupLayout() {
            super.setupLayout();
            Form form = new Form(this, "MyForm");
            form.addRow(myStr);
            form.addRow(np.getForm("MyNestedForm"));
            form.addRow(np2);
            form.addRow(widget(np4.getForm("MyNestedForm")).setVisible(false));
            form.addRow(widget(np5).setVisible(false));
            Form anotherForm = new Form(this, "anotherForm");
            anotherForm.addRow(np3);
        }
    }

    protected class StringListProperty extends PropertiesImpl {

        public final Property<List<String>> selectColumnIds = newStringList("selectColumnIds"); // this would hold the selected
        // columns ids

        public StringListProperty(String name) {
            super(name);
        }

        public void setupProperties() {
            super.setupProperties();
            // setup possible values
            selectColumnIds.setPossibleValues(Arrays.asList(new SimpleNamedThing("col1", "Surname"),
                    new SimpleNamedThing("col2", "Name"), new SimpleNamedThing("col3", "Phone")));
            // may setup default selected column like this
            selectColumnIds.setValue((List<String>) selectColumnIds.getPossibleValues());
        }

        public void setupLayout() {
            super.setupLayout();
            Form mainForm = new Form(this, Form.MAIN);
            Widget tableSelectionWidget = widget(selectColumnIds).setWidgetType(MULTIPLE_VALUE_SELECTOR_WIDGET_TYPE);
            mainForm.addRow(tableSelectionWidget);
        }
    }

}
