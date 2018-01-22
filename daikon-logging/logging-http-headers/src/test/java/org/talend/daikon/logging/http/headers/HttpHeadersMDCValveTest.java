package org.talend.daikon.logging.http.headers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.test.context.SpringBootTest;
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
