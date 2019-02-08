package org.talend.daikon.security.token;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TokenAuthenticationTest {

    private TokenAuthentication authentication;

    @Before
    public void setUp() {
        authentication = new TokenAuthentication();
    }

    @Test
    public void shouldHaveExpectedInformation() {
        assertTrue(authentication.isAuthenticated());
        assertEquals(TokenAuthentication.ADMIN_TOKEN_AUTHENTICATION, authentication.getDetails());
        assertNull(authentication.getCredentials());
        assertNull(authentication.getPrincipal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowAuthenticatedChange() {
        authentication.setAuthenticated(false);
    }

    @Test
    public void shouldAllowAuthenticatedEnable() {
        authentication.setAuthenticated(true);
    }
}