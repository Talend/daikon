// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.serialize.migration;

import static org.talend.daikon.properties.property.PropertyFactory.newString;

import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.serialize.PostDeserializeSetup;

/**
 * A child properties used in {@link ParentProperties}
 */
public class ChildProperties extends PropertiesImpl implements SerializeSetVersion {

    private static final long serialVersionUID = -8028841548094758928L;

    public final Property<String> versionOneProp = newString("versionOneProp");

    public final Property<String> versionTwoProp = newString("versionTwoProp");

    public ChildProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        versionOneProp.setValue("I'm born since version 1");
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form mainForm = new Form(this, Form.MAIN);
    }

    @Override
    public void refreshLayout(Form form) {
        super.refreshLayout(form);
        switch (versionTwoProp.getValue()) {
        case "aValue":
            break;

        }
    }

    @Override
    public int getVersionNumber() {
        return 2;
    }

    @Override
    public boolean postDeserialize(int version, PostDeserializeSetup setup, boolean persistent) {

        boolean migrated = super.postDeserialize(version, setup, persistent);
        if (version < getVersionNumber()) {
            if (versionTwoProp.getValue() == null) {
                versionTwoProp.setValue("I'm born since version 2");
                migrated = true;
            }
        }
        return migrated;
    }

}
