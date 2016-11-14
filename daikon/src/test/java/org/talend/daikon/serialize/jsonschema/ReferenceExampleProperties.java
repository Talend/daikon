package org.talend.daikon.serialize.jsonschema;

import static org.talend.daikon.properties.property.PropertyFactory.newString;

import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.ReferenceProperties;
import org.talend.daikon.properties.ReferenceProperties.ReferenceType;
import org.talend.daikon.properties.property.Property;

public class ReferenceExampleProperties extends PropertiesImpl {

    public Property<String> parentProp = newString("parentProp", "initialparentValue");

    public TestReferenceProperties reference = new TestReferenceProperties("reference");

    public ReferenceExampleProperties(String name) {
        super(name);
        reference.componentType.setValue("TestReference");
        reference.referenceType.setValue(ReferenceType.COMPONENT_TYPE);
    }

    public static class TestReferenceProperties extends PropertiesImpl implements ReferenceProperties {

        public Property<String> childProp = newString("childProp", "initialChildValue");

        public TestReferenceProperties(String name) {
            super(name);
        }

    }

}
