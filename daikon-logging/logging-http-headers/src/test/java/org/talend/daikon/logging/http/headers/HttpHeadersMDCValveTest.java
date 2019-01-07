package org.talend.daikon.logging.http.headers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { HttpHeadersMDCValveTest.TestApp.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpHeadersMDCValveTest extends AbstractHttpHeadersMDCTest {

    private static final HttpHeadersMDCValve THE_VALVE = new HttpHeadersMDCValve();

    @Override
    protected void setReplaceRemoteAddrWithForwardedFor(boolean replaceRemoteAddrWithForwardedFor) {
        THE_VALVE.setReplaceRemoteAddrWithForwardedFor(replaceRemoteAddrWithForwardedFor);
    }

    @SpringBootApplication(exclude = HttpHeadersMDCSpringConfig.class)
    static class TestApp {

        @Bean
        public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> tomcatContextCustomizer() {
            return (x -> {
                if (x instanceof TomcatServletWebServerFactory) {
                    ((TomcatServletWebServerFactory) x).addContextCustomizers(y -> {
                        y.getPipeline().addValve(THE_VALVE);
                    });
                }
            });
        }
    }
}
