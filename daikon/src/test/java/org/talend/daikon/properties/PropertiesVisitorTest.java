// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.properties.property.PropertyVisitor;

public class PropertiesVisitorTest {

    Properties foo = new Properties("foo") {

        public Property one = PropertyFactory.newString("one");

        public Properties two = new Properties("two") {

            public Property three = PropertyFactory.newString("three");

            public Properties four = new Properties("four") {
                //
            };

        };
    };

    @Test
    public void testVisitor() {
        final AtomicInteger propertiesCount = new AtomicInteger();
        final AtomicInteger propertyCount = new AtomicInteger();
        foo.accept(new AnyPropertyVisitor() {

            @Override
            public void visit(Properties properties, Properties parent) {
                propertiesCount.incrementAndGet();
                String name = properties.getName();
                if (!name.equals("foo") && !name.equals("two") && !name.equals("four")) {
                    fail("visitor should not visit this :" + properties);
                }
            }

            @Override
            public void visit(Property property, Properties parent) {
                propertyCount.incrementAndGet();
                String name = property.getName();
                if (!name.equals("one") && !name.equals("three")) {
                    fail("visitor should not visit this :" + property);
                }
            }
        }, null);
        assertEquals(3, propertiesCount.get());
        assertEquals(2, propertyCount.get());
    }

    @Test
    public void testPropertyVisitor() {
        final AtomicInteger propertyCount = new AtomicInteger();
        foo.accept(new PropertyVisitor() {

            @Override
            public void visit(Property property, Properties parent) {
                propertyCount.incrementAndGet();
                String name = property.getName();
                if (!name.equals("one") && !name.equals("three")) {
                    fail("visitor should not visit this :" + property);
                }
            }
        }, null);
        assertEquals(2, propertyCount.get());
    }

    @Test
    public void testPropertiesVisitor() {
        final AtomicInteger propertiesCount = new AtomicInteger();
        foo.accept(new PropertiesVisitor() {

            @Override
            public void visit(Properties properties, Properties parent) {
                propertiesCount.incrementAndGet();
                String name = properties.getName();
                if (!name.equals("foo") && !name.equals("two") && !name.equals("four")) {
                    fail("visitor should not visit this :" + properties);
                }
            }
        }, null);
        assertEquals(3, propertiesCount.get());
    }

}
