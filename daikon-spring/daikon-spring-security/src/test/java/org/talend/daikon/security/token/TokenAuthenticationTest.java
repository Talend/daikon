package org.talend.daikon.security.token;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;

public class TokenAuthenticationTest {

    private Authentication authentication;

    @Before
    public void setUp() {
        authentication = TokenAuthentication.INSTANCE;
    }

    @Test
    public void shouldHaveExpectedInformation() {
        assertTrue(authentication.isAuthenticated());
        assertEquals(TokenAuthentication.ADMIN_TOKEN_AUTHENTICATION, authentication.getDetails());
        assertNull(authentication.getCredentials());
        assertEquals(TokenAuthentication.ADMIN_TOKEN_AUTHENTICATION, authentication.getPrincipal());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowAuthenticatedChange() {
        authentication.setAuthenticated(false);
    }

    @Test
    public void shouldAllowAuthenticatedEnable() {
        // Where setAuthenticated(false) is *not* possible, setAuthenticated(true) is still allowed.
        authentication.setAuthenticated(true);
    }
}
