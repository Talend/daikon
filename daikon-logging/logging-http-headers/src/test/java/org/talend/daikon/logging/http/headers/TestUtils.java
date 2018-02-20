package org.talend.daikon.logging.http.headers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

/**
 *
 */
public class TestUtils {

    private TestUtils() {
    }

    public static String makeRequest(int port, String id, String forwardedFor) {
        return makeRequest(HttpMethod.GET, port, id, forwardedFor);
    }

    public static String makeRequest(HttpMethod method, int port, String id, String forwardedFor) {
        final RestOperations client = new RestTemplate();
        final HttpHeaders headers = new HttpHeaders();

        headers.add("X-Forwarded-For", forwardedFor);

        final HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = client.exchange("http://127.0.0.1:" + port + "/" + id, method, entity, String.class);

        return response.getBody();
    }
}
