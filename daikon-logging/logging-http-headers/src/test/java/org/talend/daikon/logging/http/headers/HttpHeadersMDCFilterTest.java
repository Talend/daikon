package org.talend.daikon.logging.http.headers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { HttpHeadersMDCFilterTest.TestApp.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpHeadersMDCFilterTest extends AbstractHttpHeadersMDCTest {

    private static HttpHeadersMDCFilter THE_FILTER;

    @Override
    protected void setReplaceRemoteAddrWithForwardedFor(boolean replaceRemoteAddrWithForwardedFor) {
        THE_FILTER.setReplaceRemoteAddrWithForwardedFor(replaceRemoteAddrWithForwardedFor);
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
        BeanPostProcessor filterPostProcessor() {
            return new BeanPostProcessor() {

                @Override
                public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
                    if (o instanceof FilterRegistrationBean) {
                        FilterRegistrationBean frb = (FilterRegistrationBean) o;
                        if (frb.getFilter() instanceof HttpHeadersMDCFilter) {
                            THE_FILTER = (HttpHeadersMDCFilter) frb.getFilter();
                        }
                    }
                    return o;
                }

                @Override
                public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
                    return o;
                }
            };
        }
    }
}
