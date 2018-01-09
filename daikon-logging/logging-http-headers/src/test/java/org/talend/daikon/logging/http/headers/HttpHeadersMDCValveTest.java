package org.talend.daikon.logging.http.headers;

import org.apache.catalina.Context;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { HttpHeadersMDCValveTest.TestApp.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpHeadersMDCValveTest extends AbstractHttpHeadersMDCTest {

    private static final HttpHeadersMDCValve THE_VALVE = new HttpHeadersMDCValve();

    @Test
    @Override
    public void testFilter() throws Exception {
        THE_VALVE.setReplaceRemoteAddrWithForwardedFor(false);
        super.testFilter();
    }

    @Test
    @Override
    public void testFilterReplace() throws Exception {
        THE_VALVE.setReplaceRemoteAddrWithForwardedFor(true);
        super.testFilterReplace();
    }

    @SpringBootApplication
    static class TestApp {

        @Bean
        public EmbeddedServletContainerCustomizer tomcatContextCustomizer() {
            return (x -> {
                if (x instanceof TomcatEmbeddedServletContainerFactory) {
                    ((TomcatEmbeddedServletContainerFactory) x).addContextCustomizers(y -> {
                        y.getPipeline().addValve(THE_VALVE);
                    });
                }
            });
        }
    }
}
