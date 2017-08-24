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
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.serialize.PostDeserializeSetup;

/**
 * A parent propertie containing some child properties
 */
public class ParentProperties extends PropertiesImpl implements SerializeSetVersion {

    private static final long serialVersionUID = -2970103646514440665L;

    public final Property<String> versionZeroProp = newString("versionZeroProp");

    public final ChildProperties versionZeroNestedProp = new ChildProperties("versionZeroNestedProp");

    public final Property<String> versionOneProp = newString("versionOneProp");

    public ParentProperties(String name) {
        super(name);
    }

    @Override
    public void setupProperties() {
        super.setupProperties();
        versionZeroProp.setValue("I'm born since version 0");
    }

    @Override
    public int getVersionNumber() {
        return 1;
    }

    @Override
    public boolean postDeserialize(int version, PostDeserializeSetup setup, boolean persistent) {
        boolean migrated = super.postDeserialize(version, setup, persistent);
        if (version < getVersionNumber()) {
            if (versionOneProp.getValue() == null) {
                versionOneProp.setValue("I'm born since version 1");
                migrated = true;
            }
        }

        return migrated;
    }

}
