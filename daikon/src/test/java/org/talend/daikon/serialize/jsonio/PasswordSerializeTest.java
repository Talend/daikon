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
package org.talend.daikon.serialize.jsonio;

import static org.junit.Assert.assertEquals;
import static org.talend.daikon.properties.property.PropertyFactory.newProperty;

import java.util.EnumSet;

import org.junit.Test;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.security.CryptoHelper;
import org.talend.daikon.serialize.SerializerDeserializer;

public class PasswordSerializeTest {

    @Test
    public void testPasswordSerialized() {
        BasicProperties basicProp = new BasicProperties("test");
        basicProp.init();
        basicProp.userPassword.userId.setValue("myUser");
        basicProp.userPassword.password.setValue("myPassword");

        String serialized = basicProp.toSerialized();
        BasicProperties deserializedProp = SerializerDeserializer.fromSerializedPersistent(serialized,
                BasicProperties.class).object;
        assertEquals("myUser", deserializedProp.userPassword.userId.getValue());
        assertEquals("myPassword", deserializedProp.userPassword.password.getValue());
    }

    class BasicProperties extends PropertiesImpl {

        public UserPasswordProperties userPassword = new UserPasswordProperties("userPassword");

        public NestedProperties nested;

        public BasicProperties(String name) {
            super(name);
        }

        @Override
        public void setupProperties() {
            super.setupProperties();
            nested = new NestedProperties("nested");
            nested.userPassword = userPassword;
        }

        protected void encryptData(Property property, boolean encrypt) {

            if (property.getStoredValue() == null) {
                return;
            }

            if (property.isFlag(Property.Flags.ENCRYPT)) {
                String value = (String) property.getStoredValue();
                CryptoHelper ch = new CryptoHelper(CryptoHelper.PASSPHRASE);
                if (encrypt) {
                    property.setStoredValue(ch.encrypt(value));
                } else {
                    super.encryptData(property, encrypt);
                }
            }
        }
    }

    class NestedProperties extends PropertiesImpl {

        public UserPasswordProperties userPassword = new UserPasswordProperties("userPassword");

        public NestedProperties(String name) {
            super(name);
        }

    }

    class UserPasswordProperties extends PropertiesImpl {

        public Property<String> userId = newProperty("userId").setRequired(); //$NON-NLS-1$

        public Property<String> password = newProperty("password").setRequired()
                .setFlags(EnumSet.of(Property.Flags.ENCRYPT, Property.Flags.SUPPRESS_LOGGING));

        public UserPasswordProperties(String name) {
            super(name);
        }

    }

}
