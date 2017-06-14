// ============================================================================
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.daikon.security.access;

import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.talend.daikon.exception.TalendRuntimeException;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RequiresAuthorityConfiguration.class)
public class RequiresAuthorityAspectTest {

    private static final AnonymousAuthenticationToken GRANTED = new AnonymousAuthenticationToken("granted", //
            new Object(), //
            singleton(new SimpleGrantedAuthority("TestComponentExec")));

    @Autowired
    RequiresTestComponent component;

    @Before
    public void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void shouldInvokeSuccessfully() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(GRANTED);
        assertEquals("secret string", component.execute());
    }

    @Test
    public void shouldFailToInvoke() throws Exception {
        try {
            component.execute();
            fail("Expected an error.");
        } catch (TalendRuntimeException e) {
            assertEquals(403, e.getCode().getHttpStatus());
        }
    }

    @Test
    public void shouldUseAuthorityWhenValueDefined() throws Exception {
        try {
            component.authorityValuePriority();
            fail("Expected an error.");
        } catch (TalendRuntimeException e) {
            assertEquals(403, e.getCode().getHttpStatus());
        }
    }

    @Test
    public void shouldFailToInvokeWithFallback() throws Exception {
        assertEquals("", component.executeWithFallback());
    }

    @Test
    public void shouldSuccessfullyInvokeWithFallback() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(GRANTED);
        assertEquals("secret string", component.executeWithFallback());
    }

    @Test
    public void shouldInvokeUnprotected() throws Exception {
        assertEquals("My details", component.getDetails());
    }
}