package org.talend.daikon.logging.http.headers;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.servlet.FilterConfig;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { HttpHeadersMDCFilterTest.TestApp.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpHeadersMDCFilterTest extends AbstractHttpHeadersMDCTest {

    private static HttpHeadersMDCFilter THE_FILTER = new HttpHeadersMDCFilter();

    @Test
    @Override
    public void testFilter() throws Exception {
        THE_FILTER.setReplaceRemoteAddrWithForwardedFor(false);
        super.testFilter();
    }

    @Test
    @Override
    public void testFilterReplace() throws Exception {
        THE_FILTER.setReplaceRemoteAddrWithForwardedFor(true);
        super.testFilterReplace();
    }

    @Test
    public void testInitFilter() throws Exception {
        HttpHeadersMDCFilter filter = new HttpHeadersMDCFilter();

        Assert.assertFalse(filter.isReplaceRemoteAddrWithForwardedFor());

        MockFilterConfig config = new MockFilterConfig();
        config.addInitParameter("replaceRemoteAddrWithForwardedFor", "True");

        filter.init(config);

        Assert.assertTrue(filter.isReplaceRemoteAddrWithForwardedFor());
    }

    @SpringBootApplication
    static class TestApp {

        @Bean
        public FilterRegistrationBean filterRegistrationBean() {
            FilterRegistrationBean answer = new FilterRegistrationBean();

            answer.setFilter(THE_FILTER);
            answer.setOrder(SecurityProperties.DEFAULT_FILTER_ORDER + 1);

            return answer;
        }

    }
}
