package org.talend.daikon.logging.http.headers;

import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;

/**
 *
 */
public abstract class AbstractHttpHeadersMDCTest {

    @LocalServerPort
    int httpPort;

    public void testFilter() throws Exception {
        setReplaceRemoteAddrWithForwardedFor(false);

        final String testId = UUID.randomUUID().toString();
        final String forwardedFor = "137.137.137.137";

        String response = TestUtils.makeRequest(httpPort, testId, forwardedFor);

        Assert.assertEquals("test:" + testId, response);

        Map<String, String> mdc = TestController.MDCs.get(testId);

        Assert.assertNotNull(mdc);

        Assert.assertEquals("127.0.0.1", mdc.get(HttpHeadersMDC.HTTP_REMOTE_ADDR));
        Assert.assertEquals(forwardedFor, mdc.get(HttpHeadersMDC.HTTP_FORWARDED_FOR));

        String portStr = mdc.get(HttpHeadersMDC.HTTP_REMOTE_PORT);
        int port = -1;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Assert.fail("Wrong port format " + portStr);
        }
        Assert.assertFalse(port < 0 || port > 0xFFFF);

        Assert.assertNotNull(mdc.get(HttpHeadersMDC.HTTP_USER_AGENT));
    }

    public void testFilterReplace() throws Exception {
        setReplaceRemoteAddrWithForwardedFor(true);

        final String testId = UUID.randomUUID().toString();
        final String forwardedFor = "137.137.137.137";

        String response = TestUtils.makeRequest(httpPort, testId, forwardedFor);

        Assert.assertEquals("test:" + testId, response);

        Map<String, String> mdc = TestController.MDCs.get(testId);

        Assert.assertNotNull(mdc);

        Assert.assertEquals(forwardedFor, mdc.get(HttpHeadersMDC.HTTP_REMOTE_ADDR));
        Assert.assertEquals(forwardedFor, mdc.get(HttpHeadersMDC.HTTP_FORWARDED_FOR));

        String portStr = mdc.get(HttpHeadersMDC.HTTP_REMOTE_PORT);
        int port = -1;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Assert.fail("Wrong port format " + portStr);
        }
        Assert.assertFalse(port < 0 || port > 0xFFFF);

        Assert.assertNotNull(mdc.get(HttpHeadersMDC.HTTP_USER_AGENT));
    }

    @Test
    public void testAsync() {
        final String testId = UUID.randomUUID().toString();
        final String forwardedFor = "137.137.137.137";

        String response = TestUtils.makeRequest(HttpMethod.POST, httpPort, testId, forwardedFor);

        Assert.assertEquals("async:" + testId, response);

        Map<String, String> mdc = TestController.MDCs.get(testId);

        Assert.assertNotNull(mdc);

        Assert.assertEquals("127.0.0.1", mdc.get(HttpHeadersMDC.HTTP_REMOTE_ADDR));
        Assert.assertEquals(forwardedFor, mdc.get(HttpHeadersMDC.HTTP_FORWARDED_FOR));

        String portStr = mdc.get(HttpHeadersMDC.HTTP_REMOTE_PORT);
        int port = -1;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Assert.fail("Wrong port format " + portStr);
        }
        Assert.assertFalse(port < 0 || port > 0xFFFF);

        Assert.assertNotNull(mdc.get(HttpHeadersMDC.HTTP_USER_AGENT));
    }

    protected abstract void setReplaceRemoteAddrWithForwardedFor(boolean replaceRemoteAddrWithForwardedFor);
}
