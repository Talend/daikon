package org.talend.daikon.properties;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PresentationItemTest {

    static class PresItemProperties extends PropertiesImpl {

        public final PresentationItem buttonValue = new PresentationItem("buttonValue");

        public PresItemProperties(String name) {
            super(name);
        }

    }

    @Test
    public void testDeserializeI18N() {
        PresItemProperties props = new PresItemProperties("test");
        props.init();
        String s = props.toSerialized();
        assertEquals("Press me", props.buttonValue.getDisplayName());
        PresItemProperties desProp = Properties.Helper.fromSerializedPersistent(s, PresItemProperties.class).object;
        assertEquals("Press me", desProp.buttonValue.getDisplayName());
    }

}
