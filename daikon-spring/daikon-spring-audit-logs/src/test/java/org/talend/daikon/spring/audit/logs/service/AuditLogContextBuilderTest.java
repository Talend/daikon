package org.talend.daikon.spring.audit.logs.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.talend.logging.audit.Context;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AuditLogContextBuilderTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testBuildAllFields() {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("entity", "user");
        responseBody.put("error", false);

        HttpStatus httpStatus = HttpStatus.ACCEPTED;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setRemoteHost("localhost");
        request.setRemotePort(80);
        request.setRequestURI("/");
        request.setMethod("POST");
        request.addHeader("User-Agent", "user agent");

        String fakeUsername = "fakeUser";
        String accountId = "accountId";
        String userId = "userId";
        String email = "fakeUser@talend.com";

        String applicationId = "TTT";
        String eventType = "a type";
        String eventCategory = "a category";
        String eventOperation = "an operation";
        String timestamp = OffsetDateTime.now().toString();
        Context context = AuditLogContextBuilder.create().withTimestamp(timestamp).withLogId(UUID.randomUUID())
                .withRequestId(UUID.randomUUID()).withApplicationId(applicationId).withEventType(eventType)
                .withEventOperation(eventOperation).withEventCategory(eventCategory).withUserId(userId).withUsername(fakeUsername)
                .withEmail(email).withAccountId(accountId).withRequest(request, "post request")
                .withResponse(httpStatus.value(), responseBody).build();

        assertNotNull(context.get("timestamp"));
        assertNotNull(context.get("logId"));
        assertNotNull(context.get("requestId"));
        assertEquals("accountId", context.get("accountId"));
        assertEquals("userId", context.get("userId"));
        assertEquals("fakeUser", context.get("username"));
        assertEquals("fakeUser@talend.com", context.get("email"));
        assertEquals("TTT", context.get("applicationId"));
        assertEquals("a type", context.get("eventType"));
        assertEquals("a category", context.get("eventCategory"));
        assertEquals("an operation", context.get("eventOperation"));
        assertEquals("127.0.0.1", context.get("clientIp"));
        assertEquals("{\"url\":\"http://localhost/\",\"method\":\"POST\",\"userAgent\":\"user agent\",\"body\":\"post request\"}",
                context.get("request"));
        assertEquals("{\"code\":\"202\",\"body\":{\"error\":false,\"entity\":\"user\"}}", context.get("response"));
    }

    @Test
    public void testBuildWithIncompleteRequestAndResponse() {
        HttpStatus httpStatus = HttpStatus.ACCEPTED;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setRemoteHost("localhost");
        request.setRemotePort(80);
        request.setRequestURI("/");
        request.setMethod(null);
        request.addHeader("User-Agent", "user agent");

        String fakeUsername = "fakeUser";
        String accountId = "accountId";
        String userId = "userId";
        String email = "fakeUser@talend.com";

        String applicationId = "TTT";
        String eventType = "a type";
        String eventCategory = "a category";
        String eventOperation = "an operation";
        String timestamp = OffsetDateTime.now().toString();

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("audit log context is incomplete, missing information: [METHOD]");

        AuditLogContextBuilder.create().withTimestamp(timestamp).withLogId(UUID.randomUUID()).withRequestId(UUID.randomUUID())
                .withApplicationId(applicationId).withEventType(eventType).withEventOperation(eventOperation)
                .withEventCategory(eventCategory).withUserId(userId).withUsername(fakeUsername).withEmail(email)
                .withAccountId(accountId).withRequest(request, null).withResponse(httpStatus.value(), null).build();
    }

    @Test
    public void testBuildEmptyNotMandatoryFields() {
        HttpStatus httpStatus = HttpStatus.ACCEPTED;
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setRemoteHost("localhost");
        request.setRemotePort(80);
        request.setRequestURI("/");
        request.setMethod("POST");

        String applicationId = "TTT";
        String eventType = "a type";
        String eventCategory = "a category";
        String eventOperation = "an operation";
        String timestamp = OffsetDateTime.now().toString();
        Context context = AuditLogContextBuilder.create().withTimestamp(timestamp).withLogId(UUID.randomUUID())
                .withRequestId(UUID.randomUUID()).withApplicationId(applicationId).withEventType(eventType)
                .withEventOperation(eventOperation).withEventCategory(eventCategory).withRequest(request, null)
                .withResponse(httpStatus.value(), null).build();

        assertNotNull(context.get("timestamp"));
        assertNotNull(context.get("logId"));
        assertNotNull(context.get("requestId"));
        assertEquals("TTT", context.get("applicationId"));
        assertEquals("a type", context.get("eventType"));
        assertEquals("a category", context.get("eventCategory"));
        assertEquals("an operation", context.get("eventOperation"));
        assertEquals("127.0.0.1", context.get("clientIp"));
        assertEquals("{\"url\":\"http://localhost/\",\"method\":\"POST\"}", context.get("request"));
        assertEquals("{\"code\":\"202\"}", context.get("response"));
    }

    @Test
    public void testBuildAllFieldsEmpty() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("audit log context is incomplete, missing information: "
                + "[TIMESTAMP, REQUEST_ID, LOG_ID, APPLICATION_ID, EVENT_TYPE, EVENT_CATEGORY,"
                + " EVENT_OPERATION, CLIENT_IP, REQUEST, RESPONSE, URL, METHOD, RESPONSE_CODE]");

        AuditLogContextBuilder.create().build();
    }

    @Test
    public void testBuildNullValuesAreRemoved() {
        AuditLogContextBuilder builder = AuditLogContextBuilder.create();
        try {
            builder.withEmail("email").withUsername(null).withRequestMethod("PUT").withRequestBody(null).withResponseCode(200)
                    .build();
        } catch (RuntimeException e) {
            // do nothing
        }
        assertEquals(3, builder.getContext().size());
        assertEquals("email", builder.getContext().get("email"));
        assertEquals("{\"method\":\"PUT\"}", builder.getContext().get("request"));
        assertEquals("{\"code\":\"200\"}", builder.getContext().get("response"));
    }

    @Test
    public void testBuildValuesCanBeOverriden() {
        AuditLogContextBuilder builder = AuditLogContextBuilder.create();
        try {
            builder.withResponseCode(200);
            // override
            builder.withResponseCode(500).build();
        } catch (RuntimeException e) {
            // do nothing
        }
        assertEquals(1, builder.getContext().size());
        assertEquals("{\"code\":\"500\"}", builder.getContext().get("response"));
    }

}
