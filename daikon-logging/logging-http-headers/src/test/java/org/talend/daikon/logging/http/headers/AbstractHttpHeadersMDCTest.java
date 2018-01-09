package org.talend.daikon.logging.http.headers;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.context.embedded.LocalServerPort;

import java.util.Map;
import java.util.UUID;

/**
 *
 */
public abstract class AbstractHttpHeadersMDCTest {

    @LocalServerPort
    int httpPort;

    public void testFilter() throws Exception {
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
}
