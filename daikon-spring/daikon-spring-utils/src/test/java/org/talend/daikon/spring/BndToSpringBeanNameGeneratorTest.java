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
package org.talend.daikon.spring;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ComponentScan(basePackages = "org.talend.daikon.spring", nameGenerator = BndToSpringBeanNameGenerator.class, includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = aQute.bnd.annotation.component.Component.class), excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*Osgi"))
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = BndToSpringBeanNameGeneratorTest.class)
public class BndToSpringBeanNameGeneratorTest {

    @Autowired
    private ApplicationContext appContext;

    @Test
    public void testBndBeanExists() {
        assertTrue(appContext.containsBean("MyBean"));
    }

}
