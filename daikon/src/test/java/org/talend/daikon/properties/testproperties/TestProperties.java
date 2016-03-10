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

import static org.talend.daikon.properties.PropertyFactory.*;
import static org.talend.daikon.properties.presentation.Widget.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.talend.daikon.properties.PresentationItem;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.ValidationResult;
import org.talend.daikon.properties.ValidationResult.Result;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.presentation.Widget.WidgetType;
import org.talend.daikon.properties.service.Repository;
import org.talend.daikon.properties.testproperties.nestedprop.NestedProperties;
import org.talend.daikon.properties.testproperties.nestedprop.inherited.InheritedProperties;
import org.talend.daikon.properties.Property.Type;

public class TestProperties extends Properties {

    public static final String USER_ID_PROP_NAME = "userId"; //$NON-NLS-1$

    public Form mainForm;

    public Form restoreForm;

    public PresentationItem testPI = new PresentationItem("testPI", "testPI display name");

    public Property userId = (Property) newProperty(USER_ID_PROP_NAME).setRequired(true);

    public Property password = ((Property) newProperty("password").setRequired(true))
            .setFlags(EnumSet.of(Property.Flags.ENCRYPT, Property.Flags.SUPPRESS_LOGGING));

    public Property nameList = newProperty("nameList");

    public Property nameListRef = newProperty("nameListRef");

    public Property integer = newProperty(Type.INT, "integer");

    public Property decimal = newProperty(Type.INT, "decimal");

    public Property date = newProperty(Type.DATE, "date");

    public Property dateTime = newProperty(Type.DATETIME, "dateTime");

    // Used in testing refreshLayout
    public Property suppressDate = newProperty(Type.BOOLEAN, "suppressDate");

    public Property initLater = null;

    public NestedProperties nestedInitLater = null;

    public NestedProperties nestedProps = new NestedProperties("nestedProps");

    public PropertiesWithDefinedI18N nestedProp2 = new PropertiesWithDefinedI18N("nestedProp2");

    public InheritedProperties nestedProp3 = new InheritedProperties("nestedProp3");

    public static final String TESTCOMPONENT = "TestComponent";

    public TestProperties(String name) {
        super(name);
    }

    public ValidationResult beforeNameList() {
        List values = new ArrayList<>();
        Collections.addAll(values, new String[] { "name1", "name2", "name3" });
        nameList.setPossibleValues(values);
        return ValidationResult.OK;
    }

    public void beforeNameListRef() {
        List values = new ArrayList<>();
        Collections.addAll(values, new String[] { "namer1", "namer2", "namer3" });
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
        Form form = Form.create(this, Form.MAIN, "Test Component");
        mainForm = form;
        form.addRow(userId);
        form.addRow(widget(password).setWidgetType(WidgetType.HIDDEN_TEXT));
        form.addRow(testPI);
        form.addRow(widget(nameList).setWidgetType(Widget.WidgetType.NAME_SELECTION_AREA));
        form.addRow(widget(nameListRef).setWidgetType(Widget.WidgetType.NAME_SELECTION_REFERENCE));

        form = Form.create(this, "restoreTest", "Restore Test");
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
            if (suppressDate.getBooleanValue()) {
                form.getWidget("date").setVisible(false);
            }
        }
    }

}
