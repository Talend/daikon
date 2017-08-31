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
package org.talend.daikon.properties;

import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.presentation.Widget;
import org.talend.daikon.properties.property.EnumProperty;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;

public class TestPropertiesList {

    public static enum TestEnum {
        ONE,
        TWO,
        THREE;
    }

    public static class TestProperties extends PropertiesImpl {

        public Property<Integer> intProp = PropertyFactory.newInteger("intProp").setRequired();

        public EnumProperty<TestEnum> stringProp = PropertyFactory.newEnum("stringProp", TestEnum.class);

        public TestProperties(String name) {
            super(name);
        }

        @Override
        public void setupLayout() {
            super.setupLayout();

            Form main = new Form(this, Form.MAIN);
            main.addRow(intProp);
            main.addColumn(stringProp);
        }

        public void afterIntProp() {
            if (intProp.getValue() != null && intProp.getValue() == 1) {
                stringProp.setValue(TestEnum.ONE);
            } else {
                stringProp.setValue(TestEnum.TWO);
            }
        }

    }

    public static class TestComponentProperties extends PropertiesImpl {

        public PropertiesList<TestProperties> filters = new PropertiesList<>("filters", new TestPropertiesFactory());

        public TestComponentProperties(String name) {
            super(name);
            this.filters.setI18nMessageFormatter(getI18nMessageFormatter());
        }

        @Override
        public void setupLayout() {
            super.setupLayout();

            Form main = new Form(this, Form.MAIN);
            main.addRow(Widget.widget(filters).setWidgetType(Widget.NESTED_PROPERTIES));
        }

    }

    public static class TestPropertiesFactory implements PropertiesList.NestedPropertiesFactory<TestProperties>

    {

        @Override
        public TestProperties createAndInit(String name) {
            return (TestProperties) new TestProperties(name).init();
        }
    }
}
