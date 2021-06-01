// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;
import org.talend.daikon.NamedThing;
import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.Property.Flags;
import org.talend.daikon.properties.property.StringProperty;
import org.talend.daikon.security.CryptoHelper;

public class StringPropertyTest {

    @Test
    public void testSetPossibleValuesWithNamedThing() {
        StringProperty stringProperty = new StringProperty("foo");
        ArrayList<NamedThing> namedThings = new ArrayList<>();
        namedThings.add(new SimpleNamedThing("foo", "fo o"));
        namedThings.add(new SimpleNamedThing("bar", "ba r"));
        namedThings.add(new SimpleNamedThing("a null", null));
        stringProperty.setPossibleNamedThingValues(namedThings);
        assertThat((List<String>) stringProperty.getPossibleValues(), contains("foo", "bar", "a null"));
        assertEquals("fo o", stringProperty.getPossibleValuesDisplayName("foo"));
        assertEquals("ba r", stringProperty.getPossibleValuesDisplayName("bar"));
        assertEquals("a null", stringProperty.getPossibleValuesDisplayName("a null"));
        // test that with unknown value, an execption is thrown
        try {
            stringProperty.getPossibleValuesDisplayName("not existing value");
            fail("exception should have been thrown.");
        } catch (TalendRuntimeException e) {
            assertEquals(CommonErrorCodes.UNEXPECTED_ARGUMENT, e.getCode());
        }
    }

    @Test
    public void testSetPossibleValuesWithNamedThingDefaultMethod() {
        StringProperty stringProperty = new StringProperty("foo");
        ArrayList<NamedThing> namedThings = new ArrayList<>();
        namedThings.add(new SimpleNamedThing("foo", "fo o"));
        namedThings.add(new SimpleNamedThing("bar", "ba r"));
        namedThings.add(new SimpleNamedThing("a null", null));
        stringProperty.setPossibleValues(namedThings);
        assertThat((List<String>) stringProperty.getPossibleValues(), contains("foo", "bar", "a null"));
        assertEquals("fo o", stringProperty.getPossibleValuesDisplayName("foo"));
        assertEquals("ba r", stringProperty.getPossibleValuesDisplayName("bar"));
        assertEquals("a null", stringProperty.getPossibleValuesDisplayName("a null"));
        // test that with unknown value, an execption is thrown
        try {
            stringProperty.getPossibleValuesDisplayName("not existing value");
            fail("exception should have been thrown.");
        } catch (TalendRuntimeException e) {
            assertEquals(CommonErrorCodes.UNEXPECTED_ARGUMENT, e.getCode());
        }
    }

    @Test
    public void testSetPossibleValuesNotNamedNamedThing() {
        StringProperty stringProperty = new StringProperty("foo") {// in order to have i18n related to this class
        };
        stringProperty.setPossibleValues("possible.value");
        assertEquals("possible.value", stringProperty.getPossibleValuesDisplayName("possible.value"));
        stringProperty.setPossibleValues("possible.value.2");
        assertEquals("possible value 2 i18n", stringProperty.getPossibleValuesDisplayName("possible.value.2"));

    }

    @Test
    public void testEncryptStringProp() {
        StringProperty stringProperty = new StringProperty("foo") {// in order to have i18n related to this class

            @Override
            public void encryptStoredValue(boolean encrypt) {

                if (isFlag(Property.Flags.ENCRYPT)) {
                    String value = (String) getStoredValue();
                    CryptoHelper ch = new CryptoHelper(CryptoHelper.PASSPHRASE);
                    if (encrypt) {
                        setStoredValue(ch.encrypt(value));
                    } else {
                        super.encryptStoredValue(encrypt);
                    }
                }
            }
        };
        stringProperty.setValue("foo");
        assertEquals("foo", stringProperty.getValue());
        stringProperty.encryptStoredValue(true);
        // value should be the same cause we did not set the Encrypt flag
        assertEquals("foo", stringProperty.getValue());
        stringProperty.encryptStoredValue(false);
        // value should be the same cause we did not set the Encrypt flag
        assertEquals("foo", stringProperty.getValue());

        // make the property to be encrypted
        stringProperty.setFlags(EnumSet.of(Flags.ENCRYPT));
        assertEquals("foo", stringProperty.getValue());
        stringProperty.encryptStoredValue(true);
        assertNotEquals("foo", stringProperty.getValue());
        stringProperty.encryptStoredValue(false);
        assertEquals("foo", stringProperty.getValue());
    }

    @Test(expected = TalendRuntimeException.class)
    public void testEncryptionFailure() {
        StringProperty stringProperty = new StringProperty("foo") {// in order to have i18n related to this class
        };
        stringProperty.setValue("foo");
        assertEquals("foo", stringProperty.getValue());
        stringProperty.encryptStoredValue(true);
        // value should be the same cause we did not set the Encrypt flag
        assertEquals("foo", stringProperty.getValue());
        stringProperty.encryptStoredValue(false);
        // value should be the same cause we did not set the Encrypt flag
        assertEquals("foo", stringProperty.getValue());

        // make the property to be encrypted
        stringProperty.setFlags(EnumSet.of(Flags.ENCRYPT));
        assertEquals("foo", stringProperty.getValue());
        stringProperty.encryptStoredValue(true);
    }
}
