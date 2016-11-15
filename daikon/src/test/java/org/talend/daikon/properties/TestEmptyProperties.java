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
package org.talend.daikon.properties;

import static org.talend.daikon.properties.property.PropertyFactory.newString;

import java.io.Serializable;
import java.text.ParseException;

import org.talend.daikon.properties.property.Property;

public class TestEmptyProperties extends PropertiesImpl implements Serializable {

    public static class InnerProperties extends PropertiesImpl {

        public Property<String> innerProperty = newString("innerProperty", "initialInnerValue");

        public InnerProperties(String name) {
            super(name);
        }

    }

    public TestEmptyProperties(String name) {
        super(name);
    }

    public InnerProperties innerProperties = new InnerProperties("innerProperties");

    public Property<String> aProperty = newString("aProperty", "initalValue");

    static public TestEmptyProperties createASetupOptionalProperties() throws ParseException {
        TestEmptyProperties properties = (TestEmptyProperties) new TestEmptyProperties("optionnalExample").init();
        return properties;
    }

}
