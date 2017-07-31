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

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.talend.daikon.properties.TestPropertiesList.TestProperties;

public class PropertiesListTest {

    @Test
    public void testSetRows() {
        PropertiesList<TestProperties> propertiesList = createPropertiesList();
        TestProperties row1 = propertiesList.getNestedPropertiesFactory().createAndInit("row1");
        TestProperties row2 = propertiesList.getNestedPropertiesFactory().createAndInit("row2");
        TestProperties row3 = propertiesList.getNestedPropertiesFactory().createAndInit("row3");
        propertiesList.setRows(Arrays.asList(row1, row2, row3));

        // check rows quantity and order
        ArrayList<TestProperties> subProps = new ArrayList<>(propertiesList.getPropertiesList());

        Assert.assertEquals("Number of added properties is wrong", 3, subProps.size());
        Assert.assertEquals(row1, subProps.get(0));
        Assert.assertEquals(row2, subProps.get(1));
        Assert.assertEquals(row3, subProps.get(2));
    }

    @Test
    public void testReSetRows() {
        PropertiesList<TestProperties> propertiesList = createPropertiesList();
        propertiesList.init();
        TestProperties row1 = propertiesList.getNestedPropertiesFactory().createAndInit("row1");
        propertiesList.setRows(Arrays.asList(row1));

        TestProperties row2 = propertiesList.getNestedPropertiesFactory().createAndInit("row2");
        TestProperties row3 = propertiesList.getNestedPropertiesFactory().createAndInit("row3");
        propertiesList.setRows(Arrays.asList(row2, row3));

        // check rows quantity and order
        ArrayList<TestProperties> subProps = new ArrayList<>(propertiesList.getPropertiesList());

        Assert.assertEquals("Number of added properties is wrong", 2, subProps.size());
        Assert.assertEquals(row2, subProps.get(0));
        Assert.assertEquals(row3, subProps.get(1));
    }

    @Test
    public void testAddRow() {
        PropertiesList<TestProperties> propertiesList = createPropertiesList();
        propertiesList.init();
        TestProperties row1 = propertiesList.getNestedPropertiesFactory().createAndInit("row1");
        propertiesList.addRow(row1);

        ArrayList<TestProperties> subProps = new ArrayList<>(propertiesList.getPropertiesList());

        Assert.assertEquals("Number of added properties is wrong", 1, subProps.size());
        Assert.assertEquals(row1, subProps.get(0));
    }

    @Test
    public void testAddRows() {
        PropertiesList<TestProperties> propertiesList = createPropertiesList();
        TestProperties row1 = propertiesList.getNestedPropertiesFactory().createAndInit("row1");
        TestProperties row2 = propertiesList.getNestedPropertiesFactory().createAndInit("row2");
        TestProperties row3 = propertiesList.getNestedPropertiesFactory().createAndInit("row3");
        propertiesList.addAllRows(Arrays.asList(row1, row2, row3));

        // check rows quantity and order
        ArrayList<TestProperties> subProps = new ArrayList<>(propertiesList.getPropertiesList());

        Assert.assertEquals("Number of added properties is wrong", 3, subProps.size());
        Assert.assertEquals(row1, subProps.get(0));
        Assert.assertEquals(row2, subProps.get(1));
        Assert.assertEquals(row3, subProps.get(2));

        TestProperties row4 = propertiesList.getNestedPropertiesFactory().createAndInit("row4");
        TestProperties row5 = propertiesList.getNestedPropertiesFactory().createAndInit("row5");
        propertiesList.addAllRows(Arrays.asList(row4, row5));
        subProps = new ArrayList<>(propertiesList.getPropertiesList());
        Assert.assertEquals("Number of added properties is wrong", 5, subProps.size());
        Assert.assertEquals(row4, subProps.get(3));
        Assert.assertEquals(row5, subProps.get(4));
    }

    @Test
    public void testCreateAndAddRow() {
        PropertiesList<TestProperties> propertiesList = createPropertiesList();
        TestProperties row = propertiesList.createAndAddRow();

        ArrayList<TestProperties> subProps = new ArrayList<>(propertiesList.getPropertiesList());

        Assert.assertEquals(1, subProps.size());
        Assert.assertEquals(row, subProps.get(0));
    }

    private PropertiesList<TestProperties> createPropertiesList() {
        PropertiesList<TestProperties> propertiesList = new PropertiesList<>("propertiesList",
                new PropertiesList.NestedPropertiesFactory<TestProperties>() {

                    @Override
                    public TestProperties createAndInit(String name) {
                        return (TestProperties) new TestProperties(name).init();
                    }

                });
        propertiesList.init();
        return propertiesList;
    }

}
