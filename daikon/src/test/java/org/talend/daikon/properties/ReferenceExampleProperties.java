package org.talend.daikon.properties;

import static org.talend.daikon.properties.property.PropertyFactory.newString;

import org.talend.daikon.properties.presentation.Form;
import org.talend.daikon.properties.property.Property;

public class ReferenceExampleProperties extends PropertiesImpl {

    public Property<String> parentProp = newString("parentProp", "initialparentValue");

    public ReferenceProperties<TestAProperties> testAPropReference = new ReferenceProperties<>("testAPropReference",
            TestAProperties.TEST_A_PROPERTIES_DEFINTION_NAME);

    // this has no callback and used to ensure it does not trigger any exception during callback check upton ref
    // resolution
    public ReferenceProperties<TestAProperties> testAPropReference2 = new ReferenceProperties<>("testAPropReference2",
            TestAProperties.TEST_A_PROPERTIES_DEFINTION_NAME);

    public boolean afterRefCallbackCalled;

    public ReferenceExampleProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        new Form(this, Form.MAIN);
    }

    public void afterTestAPropReference() {
        afterRefCallbackCalled = true;
    }

    public static class TestAProperties extends PropertiesImpl {

        public static final String TEST_A_PROPERTIES_DEFINTION_NAME = "TestAPropertiesDefintionName";

        public Property<String> aProp = newString("aProp", "initialaPropValue");

        public ReferenceProperties<TestBProperties> testBPropReference = new ReferenceProperties<>("testBPropReference",
                TestBProperties.TEST_B_PROPERTIES_DEFINTION_NAME);

        public TestAProperties(String name) {
            super(name);
        }

    }

    public static class TestBProperties extends PropertiesImpl {

        public static final String TEST_B_PROPERTIES_DEFINTION_NAME = "TestBPropertiesDefintionName";

        public Property<String> bProp = newString("bProp", "initialaPropValue");

        public TestBProperties(String name) {
            super(name);
        }

    }

}
