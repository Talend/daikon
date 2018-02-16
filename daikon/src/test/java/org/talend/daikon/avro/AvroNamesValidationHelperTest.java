// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.avro;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link org.talend.daikon.avro.AvroNamesValidationHelper}
 *
 */
public class AvroNamesValidationHelperTest {

    private static final Set<String> AVRO_INVALID_NAMES = new HashSet<String>(Arrays.asList("abstract", "const", "continue",
            "float", "for", "native", "new", "super", "switch", "while", "true", "false", "null"));

    private static final Set<String> AVRO_COMPATIBLE_NAMES = new HashSet<String>(Arrays.asList("_abstract", "_const", "_continue",
            "_float", "_for", "_native", "_new", "_super", "_switch", "_while", "_true", "_false", "_null"));

    @Test
    public void testGetAvroCompatibleName() {
        Set<String> convertedNames = new HashSet<String>();

        for (String name : AVRO_INVALID_NAMES) {
            convertedNames.add(AvroNamesValidationHelper.getAvroCompatibleName(name));
        }

        Assert.assertEquals(convertedNames, AVRO_COMPATIBLE_NAMES);
    }
}
