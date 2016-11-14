package org.talend.daikon.serialize.jsonschema;

import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newBoolean;
import static org.talend.daikon.properties.property.PropertyFactory.newDate;
import static org.talend.daikon.properties.property.PropertyFactory.newEnum;
import static org.talend.daikon.properties.property.PropertyFactory.newEnumList;
import static org.talend.daikon.properties.property.PropertyFactory.newInteger;
import static org.talend.daikon.properties.property.PropertyFactory.newProperty;
import static org.talend.daikon.properties.property.PropertyFactory.newSchema;
import static org.talend.daikon.properties.property.PropertyFactory.newString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.commons.lang3.reflect.TypeLiteral;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.properties.PresentationItem;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.EnumListProperty;
import org.talend.daikon.properties.property.EnumProperty;
import org.talend.daikon.properties.property.Property;

/**
 * TODO This is a duplicate one by org.talend.components.fullexample.FullExampleProperties, but for test json schema
 * serialize, can be moved or removed if there is a better way
 */
public class FullExampleProperties extends PropertiesImpl {

    /**
     * table property to use with table widget.
     */
    static final String POPUP_FORM_NAME = "popup";

    /** use the default widget for this String type */
    public final Property<String> stringProp = newString("stringProp", "initialValue");

    /** this shall hide stringProp widget according to it value. */
    public final Property<Boolean> hideStringPropProp = newBoolean("hideStringPropProp", false);

    /**
     * property to check the {@link WidgetType#NAME_SELECTION_AREA} and {@link WidgetType#NAME_SELECTION_REFERENCE}
     * widgets.
     */
    public final Property<String> multipleSelectionProp = newProperty("multipleSelectionProp");

    /** checking {@link WidgetType#BUTTON} */
    public final PresentationItem showNewForm = new PresentationItem("showNewForm", "Show new form");

    /** checking {@link WidgetType#TABLE} */
    public final TableProperties tableProp = new TableProperties("tableProp");

    // TODO some Component Reference widget use case.
    /** reuse common properties */
    public final CommonProperties commonProp = new CommonProperties("commonProp");

    /** checking {@link WidgetType#FILE} */
    public final Property<String> filepathProp = newString("filepathProp");

    /** checking {@link WidgetType#HIDDEN_TEXT} */
    public final Property<String> hiddenTextProp = newString("hiddenTextProp");

    /** use the default widget for this Date type */
    public final Property<Integer> integerProp = newInteger("integerProp").setRequired();

    /** use the default widget for this Integer type */
    public final Property<Date> dateProp = newDate("dateProp").setRequired();

    /** checking {@link WidgetType#TEXT_AREA} */
    public final Property<String> textareaProp = newString("textareaProp");

    /**
     * uses 2 widgets, {@link WidgetType#SCHEMA_EDITOR} in the Main form and {@link WidgetType#SCHEMA_REFERENCE} on the
     * REFERENCE form
     */
    public final Property<Schema> schema = newSchema("schema"); //$NON-NLS-1$

    public final PresentationItem validateAllCallbackCalled = new PresentationItem("validateAllCallbackCalled",
            "Validate All Callbacks called");

    private List<String> methodCalled = new ArrayList<>();

    public FullExampleProperties(String name) {
        super(name);
    }

    private void thisMethodWasCalled() {
        methodCalled.add(Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        // setup multipleSelectionProp
        ArrayList<NamedThing> multipleSelectableList = new ArrayList<NamedThing>();
        multipleSelectableList.add(new SimpleNamedThing("foo"));
        multipleSelectableList.add(new SimpleNamedThing("bar"));
        multipleSelectableList.add(new SimpleNamedThing("foobar"));
        multipleSelectionProp.setPossibleValues(multipleSelectableList);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
        mainForm.addRow(stringProp);
        mainForm.addRow(widget(schema).setWidgetType(Widget.SCHEMA_EDITOR_WIDGET_TYPE));
        mainForm.addRow(widget(schema).setWidgetType(Widget.SCHEMA_REFERENCE_WIDGET_TYPE));
        mainForm.addRow(widget(multipleSelectionProp).setWidgetType(Widget.NAME_SELECTION_AREA_WIDGET_TYPE));
        mainForm.addRow(widget(multipleSelectionProp).setWidgetType(Widget.NAME_SELECTION_REFERENCE_WIDGET_TYPE));
        mainForm.addRow(widget(showNewForm).setWidgetType(Widget.BUTTON_WIDGET_TYPE));
        Form popUpForm = new Form(this, POPUP_FORM_NAME);
        showNewForm.setFormtoShow(popUpForm);
        mainForm.addColumn(commonProp);
        mainForm.addColumn(widget(hiddenTextProp).setWidgetType(Widget.HIDDEN_TEXT_WIDGET_TYPE));
        mainForm.addColumn(widget(filepathProp).setWidgetType(Widget.FILE_WIDGET_TYPE));
        mainForm.addRow(integerProp);
        mainForm.addRow(dateProp);
        mainForm.addRow(widget(tableProp).setWidgetType(Widget.TABLE_WIDGET_TYPE));
        Form advancedForm = new Form(this, Form.ADVANCED);
        advancedForm.addRow(widget(textareaProp).setWidgetType(Widget.TEXT_AREA_WIDGET_TYPE));
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        if (Form.MAIN.equals(form.getName())) {
            form.getWidget(stringProp.getName()).setHidden(hideStringPropProp.getValue());
        } // else do nothing
    }

    // callback for beforeFormPresent
    public void beforeFormPresentPopup() {
        thisMethodWasCalled();
    }

    public void afterHideStringPropProp() {
        thisMethodWasCalled();
        refreshLayout(getForm(Form.MAIN));
    }

    // implements all dynamic callbacks for the string property and it's default widget.
    // stringProp
    public void validateStringProp() {
        thisMethodWasCalled();
    }

    public void beforeStringProp() {
        thisMethodWasCalled();
    }

    public void beforeStringPropPresent() {
        thisMethodWasCalled();
    }

    public void afterStringProp() {
        thisMethodWasCalled();
    }

    // Schema
    public void validateSchema() {
        thisMethodWasCalled();
    }

    public void beforeSchema() {
        thisMethodWasCalled();
    }

    public void beforeSchemaPresent() {
        thisMethodWasCalled();
    }

    public void afterSchema() {
        thisMethodWasCalled();
    }

    // MultipleSelectionProp
    public void validateMultipleSelectionProp() {
        thisMethodWasCalled();
    }

    public void beforeMultipleSelectionProp() {
        thisMethodWasCalled();
    }

    public void beforeMultipleSelectionPropPresent() {
        thisMethodWasCalled();
    }

    public void afterMultipleSelectionProp() {
        thisMethodWasCalled();
    }

    // FilepathProp
    public void validateFilepathProp() {
        thisMethodWasCalled();
    }

    public void beforeFilepathProp() {
        thisMethodWasCalled();
    }

    public void beforeFilepathPropPresent() {
        thisMethodWasCalled();
    }

    public void afterFilepathProp() {
        thisMethodWasCalled();
    }

    // HiddenTextProp
    public void validateHiddenTextProp() {
        thisMethodWasCalled();
    }

    public void beforeHiddenTextProp() {
        thisMethodWasCalled();
    }

    public void beforeHiddenTextPropPresent() {
        thisMethodWasCalled();
    }

    public void afterHiddenTextProp() {
        thisMethodWasCalled();
    }

    // ShowNewForm
    public void validateShowNewForm() {
        thisMethodWasCalled();
    }

    public void beforeShowNewForm() {
        thisMethodWasCalled();
    }

    public void beforeShowNewFormPresent() {
        thisMethodWasCalled();
    }

    public void afterShowNewForm() {
        thisMethodWasCalled();
    }

    public ValidationResult validateValidateAllCallbackCalled() {
        if (methodCalled.size() == 25) {
            return ValidationResult.OK;
        } else {
            return new ValidationResult().setStatus(Result.ERROR).setMessage("some method where not called :" + methodCalled);
        }
    }

    public static class CommonProperties extends PropertiesImpl {

        public final Property<String> colString = newString("colString");

        public final EnumProperty<ColEnum> colEnum = newEnum("colEnum", ColEnum.class);

        public final Property<Boolean> colBoolean = newBoolean("colBoolean");

        public CommonProperties(String name) {
            super(name);
        }

        @Override
        public void setupLayout() {
            Form mainForm = new Form(this, Form.MAIN);
            mainForm.addRow(colString);
            mainForm.addRow(colEnum);
            mainForm.addRow(colBoolean);
        }

        public enum ColEnum {
            FOO,
            BAR
        }
    }

    public static class TableProperties extends PropertiesImpl {

        public static final TypeLiteral<List<ColEnum>> LIST_ENUM_TYPE = new TypeLiteral<List<ColEnum>>() {// empty
        };

        private static final TypeLiteral<List<String>> LIST_STRING_TYPE = new TypeLiteral<List<String>>() {// empty
        };

        private static final TypeLiteral<List<Boolean>> LIST_BOOLEAN_TYPE = new TypeLiteral<List<Boolean>>() {// empty
        };

        public Property<List<String>> colListString = newProperty(LIST_STRING_TYPE, "colListString");

        public EnumListProperty<ColEnum> colListEnum = newEnumList("colListEnum", LIST_ENUM_TYPE);

        public Property<List<Boolean>> colListBoolean = newProperty(LIST_BOOLEAN_TYPE, "colListBoolean");

        public TableProperties(String name) {
            super(name);
        }

        @Override
        public void setupLayout() {
            super.setupLayout();
            Form mainForm = new Form(this, Form.MAIN);
            mainForm.addColumn(colListString);
            mainForm.addColumn(colListEnum);
            mainForm.addColumn(colListBoolean);
        }

        public enum ColEnum {
            FOO,
            BAR
        }
    }

}
