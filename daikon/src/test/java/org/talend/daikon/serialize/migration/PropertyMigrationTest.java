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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.serialize.SerializerDeserializer.Deserialized;

/**
 * Properties migration test after deserialization
 */
public class PropertyMigrationTest {

    private static final String propertyWithNestedPropToMigrate = "{\"@type\":\"org.talend.daikon.serialize.migration.ParentProperties\",\"versionZeroProp\":{\"@type\":\"org.talend.daikon.properties.property.StringProperty\",\"possibleValues2\":null,\"flags\":null,\"storedValue\":\"I'm born since version 0\",\"children\":{\"@type\":\"java.util.ArrayList\"},\"taggedValues\":{\"@type\":\"java.util.HashMap\"},\"size\":-1,\"occurMinTimes\":0,\"occurMaxTimes\":0,\"precision\":0,\"pattern\":null,\"nullable\":false,\"possibleValues\":null,\"currentType\":\"java.lang.String\",\"name\":\"versionZeroProp\",\"tags\":null},\"versionZeroNestedProp\":{\"__version\":1,\"versionOneProp\":{\"@type\":\"org.talend.daikon.properties.property.StringProperty\",\"possibleValues2\":null,\"flags\":null,\"storedValue\":\"I'm born since version 1\",\"children\":{\"@type\":\"java.util.ArrayList\"},\"taggedValues\":{\"@type\":\"java.util.HashMap\"},\"size\":-1,\"occurMinTimes\":0,\"occurMaxTimes\":0,\"precision\":0,\"pattern\":null,\"nullable\":false,\"possibleValues\":null,\"currentType\":\"java.lang.String\",\"name\":\"versionOneProp\",\"tags\":null},\"name\":\"versionZeroNestedProp\",\"validationResult\":null,\"tags\":null},\"name\":\"parent\",\"validationResult\":null,\"tags\":null}";

    @Test
    public void postDeserializationTest() {
        Deserialized<ParentProperties> deser = Properties.Helper.fromSerializedPersistent(propertyWithNestedPropToMigrate,
                ParentProperties.class);

        ParentProperties properties = deser.object;
        assertTrue("should be true, but not", deser.migrated);
        assertEquals("I'm born since version 0", properties.versionZeroProp.getValue());
        assertEquals("I'm born since version 1", properties.versionZeroNestedProp.versionOneProp.getValue());

        // added properties after serialization
        assertEquals("I'm born since version 1", properties.versionOneProp.getValue());
        assertEquals("I'm born since version 2", properties.versionZeroNestedProp.versionTwoProp.getValue());
    }
}
