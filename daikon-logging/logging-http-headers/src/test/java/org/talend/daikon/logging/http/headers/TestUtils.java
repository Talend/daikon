package org.talend.daikon.logging.http.headers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 */
public class TestUtils {

    public static final String LOCAL_ADDRESS;

    static {
        try {
            LOCAL_ADDRESS = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unable to resolve localhost address", e);
        }
    }

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

        ResponseEntity<String> response = client.exchange("http://" + LOCAL_ADDRESS + ":" + port + "/" + id, method, entity, String.class);

        return response.getBody();
    }
}
