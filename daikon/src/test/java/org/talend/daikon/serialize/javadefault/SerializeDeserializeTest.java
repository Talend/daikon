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

package org.talend.daikon.serialize.javadefault;

import java.io.*;
import java.text.ParseException;

import org.junit.Test;
import org.talend.daikon.serialize.FullExampleProperties;
import org.talend.daikon.serialize.FullExampleTestUtil;

public class SerializeDeserializeTest {

    @Test
    public void test() throws IOException, ClassNotFoundException, ParseException {
        FullExampleProperties properties = FullExampleTestUtil.createASetupFullExampleProperties();

        ByteArrayOutputStream outStore = new ByteArrayOutputStream();
        ObjectOutputStream ser = new ObjectOutputStream(outStore);
        ser.writeObject(properties);
        ser.close();
        ByteArrayInputStream inStore = new ByteArrayInputStream(outStore.toByteArray());
        outStore.close();

        ObjectInputStream des = new ObjectInputStream(inStore);
        FullExampleProperties copiedProperties = (FullExampleProperties) des.readObject();
        des.close();
        inStore.close();

        FullExampleTestUtil.assertPropertiesValueAreEquals(properties, copiedProperties);
    }

}
