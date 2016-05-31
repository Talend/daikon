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
package org.talend.daikon.properties.testproperties.nestedprop;

import static org.talend.daikon.properties.property.PropertyFactory.*;

import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;

public class NestedProperties extends PropertiesImpl {

    public static final String A_GREAT_PROP_NAME = "aGreatProperty"; //$NON-NLS-1$

    public Property<String> aGreatProperty = newProperty(A_GREAT_PROP_NAME);

    public Property<String> anotherProp = newString("anotherProp");

    public Property<Boolean> booleanProp = newBoolean("booleanProp");

    public NestedNestedProperties nestedProp = new NestedNestedProperties("nestedProp");

    public NestedProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form form = Form.create(this, Form.MAIN);
        form.addRow(aGreatProperty);
        form.addRow(anotherProp);
    }

    @Override
    public void refreshLayout(Form form) {
        // change visibility according to anotherProp value
        form.getWidget(anotherProp.getName()).setHidden(booleanProp.getValue());
    }
}