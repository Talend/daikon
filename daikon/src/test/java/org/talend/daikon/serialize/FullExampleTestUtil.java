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

package org.talend.daikon.serialize;

import org.apache.avro.SchemaBuilder;
import org.talend.daikon.NamedThing;
import org.talend.daikon.avro.SchemaConstants;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.property.Property;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FullExampleTestUtil {

    static public FullExampleProperties createASetupFullExampleProperties() throws ParseException {
        FullExampleProperties properties = (FullExampleProperties) new FullExampleProperties("fullexample").init();
        properties.stringProp.setValue("abc");
        properties.integerProp.setValue(1);
        properties.longProp.setValue(100l);
        properties.hideStringPropProp.setValue(false);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        properties.dateProp.setValue(df.parse("2016-10-05T01:23:45.000Z"));
        properties.schema.setValue(
                SchemaBuilder.builder().record("test").prop(SchemaConstants.INCLUDE_ALL_FIELDS, "true").fields().endRecord());

        properties.commonProp.colEnum.setValue(FullExampleProperties.CommonProperties.ColEnum.FOO);
        properties.commonProp.colBoolean.setValue(true);
        properties.commonProp.colString.setValue("common_abc");

        properties.tableProp.colListBoolean.setValue(Arrays.asList(new Boolean[] { true, false, true }));
        properties.tableProp.colListEnum.setValue(Arrays
                .asList(new FullExampleProperties.TableProperties.ColEnum[] { FullExampleProperties.TableProperties.ColEnum.FOO,
                        FullExampleProperties.TableProperties.ColEnum.BAR, FullExampleProperties.TableProperties.ColEnum.FOO }));
        properties.tableProp.colListString.setValue(Arrays.asList(new String[] { "a", "b", "c" }));
        return properties;
    }

    static public void comparePropertiesValue(Properties expectedProperties, Properties actualProperties) {
        List<NamedThing> expectedProps = expectedProperties.getProperties();
        for (NamedThing expectedProp : expectedProps) {
            String propName = expectedProp.getName();
            NamedThing actualProp = actualProperties.getProperty(propName);
            assertNotNull(propName, actualProp);
            if (expectedProp instanceof Properties) {
                assertTrue(propName + " should Properties", actualProp instanceof Properties);
                comparePropertiesValue((Properties) expectedProp, (Properties) actualProp);
            } else if (expectedProp instanceof Property) {
                assertTrue(propName + " should Property", actualProp instanceof Property);
                assertEquals(propName, ((Property) expectedProp).getValue(), ((Property) actualProp).getValue());
            }
        }

    }
}
