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
package org.talend.daikon.properties.testproperties;

import static org.talend.daikon.properties.presentation.Widget.widget;
import static org.talend.daikon.properties.property.PropertyFactory.newBoolean;
import static org.talend.daikon.properties.property.PropertyFactory.newDate;
import static org.talend.daikon.properties.property.PropertyFactory.newEnum;
import static org.talend.daikon.properties.property.PropertyFactory.newInteger;
import static org.talend.daikon.properties.property.PropertyFactory.newProperty;
import static org.talend.daikon.properties.property.PropertyFactory.newSchema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.talend.daikon.properties.PresentationItem;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.service.Repository;
import org.talend.daikon.properties.testproperties.nestedprop.NestedProperties;
import org.talend.daikon.properties.testproperties.nestedprop.inherited.InheritedProperties;

public class TestProperties extends PropertiesImpl {

    public static final String USER_ID_PROP_NAME = "userId"; //$NON-NLS-1$

    public Form mainForm;

    public Form restoreForm;

    public PresentationItem testPI = new PresentationItem("testPI", "testPI display name");

    public Property<String> userId = newProperty(USER_ID_PROP_NAME).setRequired();

    public Property<String> password = newProperty("password").setRequired()
            .setFlags(EnumSet.of(Property.Flags.ENCRYPT, Property.Flags.SUPPRESS_LOGGING));

    public Property<String> nameList = newProperty("nameList");

    public Property<String> nameListRef = newProperty("nameListRef");

    public Property<Integer> integer = newInteger("integer");

    public Property<Integer> decimal = newInteger("decimal");

    public Property<java.util.Date> date = newDate("date");

    public Property<java.util.Date> dateTime = newDate("dateTime");

    public Property<Schema> schema = newSchema("schema")
            .setValue(SchemaBuilder.builder().record("EmptyRecord").fields().endRecord());

    // Used in testing refreshLayout
    public Property<Boolean> suppressDate = newBoolean("suppressDate");

    public Property<String> initLater = null;

    public NestedProperties nestedInitLater = null;

    public NestedProperties nestedProps = new NestedProperties("nestedProps");

    public PropertiesWithDefinedI18N nestedProp2 = new PropertiesWithDefinedI18N("nestedProp2");

    public InheritedProperties nestedProp3 = new InheritedProperties("nestedProp3");

    enum Foo {
        FOO,
        BAR,
        FOOBAR
    }

    public Property<Foo> enumFoo = newEnum("enumFoo", Foo.class);

    public static final String TESTCOMPONENT = "TestComponent";

    public TestProperties(String name) {
        super(name);
    }

    public ValidationResult beforeNameList() {
        List<String> values = new ArrayList<>();
        Collections.addAll(values, "name1", "name2", "name3");
        nameList.setPossibleValues(values);
        return ValidationResult.OK;
    }

    public void beforeNameListRef() {
        List<String> values = new ArrayList<>();
        Collections.addAll(values, "namer1", "namer2", "namer3");
        nameListRef.setPossibleValues(values);
    }

    public ValidationResult afterFormFinishMain(Repository<Properties> repo) {
        return new ValidationResult().setStatus(Result.ERROR);
    }

    public ValidationResult afterInteger() {
        return new ValidationResult().setStatus(Result.WARNING);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        initLater = newProperty("initLater");
        nestedInitLater = new NestedProperties("nestedInitLater");
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form form = Form.create(this, Form.MAIN);
        mainForm = form;
        form.addRow(userId);
        form.addRow(widget(password).setWidgetType(Widget.HIDDEN_TEXT_WIDGET_TYPE));
        form.addRow(testPI);
        form.addRow(widget(nameList).setWidgetType(Widget.NAME_SELECTION_AREA_WIDGET_TYPE));
        form.addRow(widget(nameListRef).setWidgetType(Widget.NAME_SELECTION_REFERENCE_WIDGET_TYPE));

        form = Form.create(this, "restoreTest");
        restoreForm = form;
        form.addRow(userId);
        form.addRow(nameList);
        form.addRow(integer);
        form.addRow(decimal);
        form.addRow(date);
        form.addRow(dateTime);
        form.addRow(nestedProps.getForm(Form.MAIN));
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        if (form.getName().equals("restoreTest")) {
            if (suppressDate.getValue()) {
                form.getWidget("date").setHidden(true);
            }
        }
    }

}
