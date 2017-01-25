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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.talend.daikon.properties.property.Property;
import org.talend.daikon.properties.property.PropertyFactory;
import org.talend.daikon.properties.property.PropertyVisitor;

public class PropertiesVisitorTest {

    private Properties foo = new PropertiesImpl("foo") {

        public Property one = PropertyFactory.newString("one");

        public Properties onea = new PropertiesImpl("onea");

        public Properties two = new PropertiesImpl("two") {

            public Property three = PropertyFactory.newString("three");

            public Properties four = new PropertiesImpl("four") {
                //
            };

            public Properties nested = onea;

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
                if (!name.equals("foo") && !name.equals("onea") && !name.equals("two") && !name.equals("four")) {
                    fail("visitor should not visit this :" + properties);
                }
            }

            @Override
            public void visit(Property property, Properties parent) {
                propertyCount.incrementAndGet();
                String name = property.getName();
                if (!"one".equals(name) && !"three".equals(name)) {
                    fail("visitor should not visit this :" + property);
                }
            }
        }, null);
        assertEquals(4, propertiesCount.get());
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
                if (!"one".equals(name) && !"three".equals(name)) {
                    fail("visitor should not visit this :" + property);
                }
            }
        }, null);
        assertEquals(2, propertyCount.get());
    }

    @Test
    public void testPropertiesVisitor() {
        final Set<Properties> visited = new HashSet();
        final AtomicInteger propertiesCount = new AtomicInteger();
        foo.accept(new PropertiesVisitor() {

            @Override
            public void visit(Properties properties, Properties parent) {
                propertiesCount.incrementAndGet();
                String name = properties.getName();
                if (!"foo".equals(name) && !"onea".equals(name) && !"two".equals(name) && !"four".equals(name)) {
                    fail("visitor should not visit this :" + properties);
                }
                if (visited.contains(properties))
                    fail("Multiple visits: " + properties);
                visited.add(properties);
            }
        }, null);
        assertEquals(4, propertiesCount.get());
    }

}
