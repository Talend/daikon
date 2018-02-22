package org.talend.daikon.logging;

import com.google.gson.Gson;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.lang.reflect.Type;
import java.util.Map;
import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestLine;
import feign.RequestTemplate;
import feign.codec.Decoder;
import feign.codec.Encoder;
import static org.junit.Assert.assertNotNull;

public class FeignRequestInterceptorTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Rule
    public final MockWebServer server = new MockWebServer();

    @Test
    public void testSingleInterceptor() throws Exception {
        server.enqueue(new MockResponse().setBody("foo"));

        TestInterface api = new TestInterfaceBuilder().requestInterceptor(new TalendFeignRequestInterceptor())
                .target("http://localhost:" + server.getPort());

        api.post();

        assertNotNull(server.takeRequest().getHeaders());
    }

    @Test
    public void testMultipleInterceptor() throws Exception {
        server.enqueue(new MockResponse().setBody("foo"));

        TestInterface api = new TestInterfaceBuilder().requestInterceptor(new TalendFeignRequestInterceptor())
                .requestInterceptor(new UserAgentInterceptor()).target("http://localhost:" + server.getPort());

        api.post();

        assertNotNull(server.takeRequest().getHeaders());
    }

    static class UserAgentInterceptor implements RequestInterceptor {

        @Override
        public void apply(RequestTemplate template) {
            template.header("User-Agent", "Feign");
        }

    }

    interface TestInterface {

        @RequestLine("POST /")
        String post();
    }

    static final class TestInterfaceBuilder {

        private final Feign.Builder delegate = new Feign.Builder().decoder(new Decoder.Default()).encoder(new Encoder() {

            @Override
            public void encode(Object object, Type bodyType, RequestTemplate template) {
                if (object instanceof Map) {
                    template.body(new Gson().toJson(object));
                } else {
                    template.body(object.toString());
                }
            }
        });

        TestInterfaceBuilder requestInterceptor(RequestInterceptor requestInterceptor) {
            delegate.requestInterceptor(requestInterceptor);
            return this;
        }

        TestInterface target(String url) {
            return delegate.target(TestInterface.class, url);
        }
    }

}