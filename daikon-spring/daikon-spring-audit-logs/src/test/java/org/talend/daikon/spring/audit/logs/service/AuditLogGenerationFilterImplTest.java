package org.talend.daikon.spring.audit.logs.service;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.StringContains.containsString;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.commons.text.StringEscapeUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.talend.daikon.spring.audit.logs.api.AuditContextFilter;
import org.talend.daikon.spring.audit.logs.api.AuditUserProvider;
import org.talend.daikon.spring.audit.logs.api.GenerateAuditLog;
import org.talend.daikon.spring.audit.logs.api.NoOpAuditContextFilter;
import org.talend.daikon.spring.audit.logs.model.AuditLogFieldEnum;
import org.talend.logging.audit.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AuditLogGenerationFilterConfiguration.class)
@TestPropertySource(properties = { "audit.enabled=false" })
public class AuditLogGenerationFilterImplTest {

    static public class SensitiveFilter implements AuditContextFilter {

        @Override
        public AuditLogContextBuilder filter(AuditLogContextBuilder auditLogContextBuilder, Object requestBody) {
            return auditLogContextBuilder.withRequestBody(((String) requestBody).replace("sensitiveValue", ""));
        }
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private AuditLogGenerationFilterImpl auditLogGenerationFilterImpl;

    @Autowired
    private AuditLogger auditLogger;

    @Autowired
    private AuditUserProvider auditUserProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private ProceedingJoinPoint proceedingJoinPoint;

    private Method method;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    private MockHttpServletResponse response = new MockHttpServletResponse();

    @Before
    public void setUp() {
        // Reset AuditLogger mock
        reset(auditLogger);
        // Mock ProceedingJoinPoint & Method
        proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        method = mock(Method.class);
        MethodSignature methodSignature = mock(MethodSignature.class);
        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);

        auditLogGenerationFilterImpl = new AuditLogGenerationFilterImpl(objectMapper, auditUserProvider, auditLogger);
    }

    /**
     * GIVEN a incomplete context (with missing info)
     * AND the @GenerateAuditLog well configured placed on a RestController method
     * WHEN the method is called (a rest request has been performed)
     * THEN a RuntimeException is thrown
     */
    @Test
    public void testGenerateAuditLogMissingMandatoryField() throws Throwable {

        // GIVEN a incomplete context (with missing info)
        // AND the @GenerateAuditLog well configured placed on a RestController method

        // Mock @GenerateAuditLog annotation as following :
        // @GenerateAuditLog(application = "TMC", eventType = "application security", eventCategory = "user account",
        // eventOperation = "create")
        mockGenerateAuditLog("daikon", "security", "resource", "create", false);

        // Mock no authenticated user
        mockUser(null, null, null, null);

        // Mock minimal HTTP request & response with missing info
        // METHOD is missing
        mockHttpRequest("0.0.0.0", "/resource", null, null, null);
        mockHttpResponse(HttpStatus.OK, null);

        // WHEN the method is called (a rest request has been performed)
        // THEN a RuntimeException is thrown
        exceptionRule.expect(RuntimeException.class);
        exceptionRule.expectMessage("audit log context is incomplete, missing information: ");
        exceptionRule.expectMessage(AuditLogFieldEnum.METHOD.toString());
        auditLogGenerationFilterImpl.auditLogGeneration(proceedingJoinPoint);
    }

    /**
     * GIVEN a minimal context configured
     * AND the @GenerateAuditLog well configured placed on a RestController method
     * WHEN the method is called (a rest request has been performed)
     * THEN an audit log with minimal context must be sent
     */
    @Test
    public void testGenerateAuditLogMinimalContext() throws Throwable {

        // GIVEN a minimal context configured
        // AND the @GenerateAuditLog well configured placed on a RestController method

        // Mock @GenerateAuditLog annotation as following :
        // @GenerateAuditLog(application = "TMC", eventType = "application security", eventCategory = "user account",
        // eventOperation = "create")
        mockGenerateAuditLog("daikon", "security", "resource", "create", false);

        // Mock no authenticated user
        mockUser(null, null, null, null);

        // Mock minimal HTTP request & response
        mockHttpRequest("0.0.0.0", "/resource", HttpMethod.POST, null, null);
        mockHttpResponse(HttpStatus.OK, null);

        // WHEN the method is called (a rest request has been performed)
        auditLogGenerationFilterImpl.auditLogGeneration(proceedingJoinPoint);

        // THEN an audit log with minimal context must be sent
        ArgumentCaptor<Context> context = ArgumentCaptor.forClass(Context.class);
        verify(auditLogger, times(1)).sendAuditLog(context.capture());
        verifyContext(context.getValue(),
                // Basic mandatory fields must be filled
                AuditLogFieldEnum.TIMESTAMP, is(not(nullValue())), //
                AuditLogFieldEnum.REQUEST_ID, is(not(nullValue())), //
                AuditLogFieldEnum.LOG_ID, is(not(nullValue())), //
                // User information must be empty
                AuditLogFieldEnum.ACCOUNT_ID, is(nullValue()), //
                AuditLogFieldEnum.USER_ID, is(nullValue()), //
                AuditLogFieldEnum.USERNAME, is(nullValue()), //
                AuditLogFieldEnum.EMAIL, is(nullValue()), //
                // Other mandatory contextual information must be filled
                AuditLogFieldEnum.APPLICATION_ID, is("daikon"), //
                AuditLogFieldEnum.EVENT_TYPE, is("security"), //
                AuditLogFieldEnum.EVENT_CATEGORY, is("resource"), //
                AuditLogFieldEnum.EVENT_OPERATION, is("create"), //
                AuditLogFieldEnum.CLIENT_IP, is("0.0.0.0"), //
                // Request payload must be filled with minimal mandatory info
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"http://localhost/resource\"", AuditLogFieldEnum.URL.getId())),
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.METHOD.getId(), HttpMethod.POST)),
                AuditLogFieldEnum.REQUEST, not(containsString(AuditLogFieldEnum.REQUEST_BODY.getId())), AuditLogFieldEnum.REQUEST,
                not(containsString(AuditLogFieldEnum.USER_AGENT.getId())),
                // Response payload must be filled with minimal mandatory info
                AuditLogFieldEnum.RESPONSE, not(containsString(AuditLogFieldEnum.RESPONSE_BODY.getId())),
                AuditLogFieldEnum.RESPONSE,
                containsString(String.format("\"%s\":\"200\"", AuditLogFieldEnum.RESPONSE_CODE.getId())));
    }

    /**
     * GIVEN a minimal context configured
     * AND the @GenerateAuditLog well configured placed on a RestController method
     * WHEN the method is called (a rest request has been performed) and that method is annotated with @ResponseStatus
     * THEN an audit log with minimal context must be sent
     */
    @Test
    public void testGenerateAuditLogMinimalContextWithResponseCode() throws Throwable {

        // GIVEN a minimal context configured
        // AND the @GenerateAuditLog well configured placed on a RestController method

        // Mock @GenerateAuditLog annotation as following :
        // @GenerateAuditLog(application = "TMC", eventType = "application security", eventCategory = "user account",
        // eventOperation = "create")
        mockGenerateAuditLog("daikon", "security", "resource", "create", false);
        mockResponseStatus(HttpStatus.NO_CONTENT);

        // Mock no authenticated user
        mockUser(null, null, null, null);

        // Mock minimal HTTP request & response
        mockHttpRequest("0.0.0.0", "/resource", HttpMethod.POST, null, null);
        mockHttpResponse(HttpStatus.OK, null);

        // WHEN the method is called (a rest request has been performed)
        auditLogGenerationFilterImpl.auditLogGeneration(proceedingJoinPoint);

        // THEN an audit log with minimal context must be sent
        ArgumentCaptor<Context> context = ArgumentCaptor.forClass(Context.class);
        verify(auditLogger, times(1)).sendAuditLog(context.capture());
        verifyContext(context.getValue(),
                // Basic mandatory fields must be filled
                AuditLogFieldEnum.TIMESTAMP, is(not(nullValue())), //
                AuditLogFieldEnum.REQUEST_ID, is(not(nullValue())), //
                AuditLogFieldEnum.LOG_ID, is(not(nullValue())), //
                // User information must be empty
                AuditLogFieldEnum.ACCOUNT_ID, is(nullValue()), //
                AuditLogFieldEnum.USER_ID, is(nullValue()), //
                AuditLogFieldEnum.USERNAME, is(nullValue()), //
                AuditLogFieldEnum.EMAIL, is(nullValue()), //
                // Other mandatory contextual information must be filled
                AuditLogFieldEnum.APPLICATION_ID, is("daikon"), //
                AuditLogFieldEnum.EVENT_TYPE, is("security"), //
                AuditLogFieldEnum.EVENT_CATEGORY, is("resource"), //
                AuditLogFieldEnum.EVENT_OPERATION, is("create"), //
                AuditLogFieldEnum.CLIENT_IP, is("0.0.0.0"), //
                // Request payload must be filled with minimal mandatory info
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"http://localhost/resource\"", AuditLogFieldEnum.URL.getId())),
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.METHOD.getId(), HttpMethod.POST)),
                AuditLogFieldEnum.REQUEST, not(containsString(AuditLogFieldEnum.REQUEST_BODY.getId())), AuditLogFieldEnum.REQUEST,
                not(containsString(AuditLogFieldEnum.USER_AGENT.getId())),
                // Response payload must be filled with minimal mandatory info
                AuditLogFieldEnum.RESPONSE, not(containsString(AuditLogFieldEnum.RESPONSE_BODY.getId())),
                AuditLogFieldEnum.RESPONSE,
                containsString(String.format("\"%s\":\"204\"", AuditLogFieldEnum.RESPONSE_CODE.getId())));
    }

    /**
     * GIVEN a full context configured
     * AND the @GenerateAuditLog well configured placed on a RestController method
     * WHEN the method is called (a rest request has been performed)
     * THEN an audit log with full context must be sent
     */
    @Test
    public void testGenerateAuditLogFullContext() throws Throwable {

        // GIVEN a full context configured
        // AND the @GenerateAuditLog well configured placed on a RestController method

        // Mock @GenerateAuditLog annotation as following :
        // @GenerateAuditLog(application = "TMC", eventType = "application security", eventCategory = "user account",
        // eventOperation = "create")
        mockGenerateAuditLog("daikon", "security", "resource", "create", true);

        // Mock current authenticated user
        mockUser("user1", "ejarvis", "edwin.jarvis@talend.com", "account1");

        // Mock HTTP request & response
        Map<String, String> createdResource = new HashMap<>();
        createdResource.put("id", "myResourceId");
        createdResource.put("name", "myResourceName");
        mockHttpRequest("0.0.0.0", "/resource", HttpMethod.POST, objectMapper.writeValueAsString(createdResource), "myUserAgent");
        mockHttpResponse(HttpStatus.OK, createdResource);

        // WHEN the method is called (a rest request has been performed)
        auditLogGenerationFilterImpl.auditLogGeneration(proceedingJoinPoint);

        // THEN an audit log with full context must be sent
        ArgumentCaptor<Context> context = ArgumentCaptor.forClass(Context.class);
        verify(auditLogger, times(1)).sendAuditLog(context.capture());
        verifyContext(context.getValue(),
                // Basic mandatory fields must be filled
                AuditLogFieldEnum.TIMESTAMP, is(not(nullValue())), //
                AuditLogFieldEnum.REQUEST_ID, is(not(nullValue())), //
                AuditLogFieldEnum.LOG_ID, is(not(nullValue())), //
                // User information must be filled
                AuditLogFieldEnum.ACCOUNT_ID, is("account1"), //
                AuditLogFieldEnum.USER_ID, is("user1"), //
                AuditLogFieldEnum.USERNAME, is("ejarvis"), //
                AuditLogFieldEnum.EMAIL, is("edwin.jarvis@talend.com"), //
                // Other mandatory contextual information must be filled
                AuditLogFieldEnum.APPLICATION_ID, is("daikon"), //
                AuditLogFieldEnum.EVENT_TYPE, is("security"), //
                AuditLogFieldEnum.EVENT_CATEGORY, is("resource"), //
                AuditLogFieldEnum.EVENT_OPERATION, is("create"), //
                AuditLogFieldEnum.CLIENT_IP, is("0.0.0.0"), //
                // Request payload must be filled
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"http://localhost/resource\"", AuditLogFieldEnum.URL.getId())),
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.METHOD.getId(), HttpMethod.POST)),
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.REQUEST_BODY.getId(),
                        StringEscapeUtils.escapeJava(objectMapper.writeValueAsString(createdResource)))),
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"myUserAgent\"", AuditLogFieldEnum.USER_AGENT.getId())),
                // Response payload must be filled
                AuditLogFieldEnum.RESPONSE,
                containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.RESPONSE_BODY.getId(),
                        StringEscapeUtils.escapeJava(objectMapper.writeValueAsString(createdResource)))),
                AuditLogFieldEnum.RESPONSE,
                containsString(String.format("\"%s\":\"200\"", AuditLogFieldEnum.RESPONSE_CODE.getId())));
    }

    /**
     * GIVEN a full context configured
     * AND the @GenerateAuditLog well configured placed on a RestController method
     * AND a filter configured to remove sensitive information
     * WHEN the method is called (a rest request has been performed) with sensitive information
     * THEN an audit log with full context must be sent without sensitive information
     */
    @Test
    public void testGenerateAuditLogFilteredContext() throws Throwable {

        // GIVEN a full context configured
        // AND the @GenerateAuditLog well configured placed on a RestController method
        // AND a filter configured to remove sensitive information

        // Mock @GenerateAuditLog annotation as following :
        // @GenerateAuditLog(application = "TMC", eventType = "application security", eventCategory = "user account",
        // eventOperation = "create", filter = SensitiveFilter.class)
        mockGenerateAuditLog("daikon", "security", "resource", "create", true, SensitiveFilter.class);

        // Mock current authenticated user
        mockUser("user1", "ejarvis", "edwin.jarvis@talend.com", "account1");

        // Mock HTTP request & response
        Map<String, String> createdResource = new HashMap<>();
        createdResource.put("id", "myResourceId");
        createdResource.put("name", "myResourceName");
        // Request contains sensitive data
        createdResource.put("sensitiveKey", "sensitiveValue");
        mockHttpRequest("0.0.0.0", "/resource", HttpMethod.POST, objectMapper.writeValueAsString(createdResource), "myUserAgent");
        mockHttpResponse(HttpStatus.OK, createdResource);

        // WHEN the method is called (a rest request has been performed) with sensitive information
        auditLogGenerationFilterImpl.auditLogGeneration(proceedingJoinPoint);

        // THEN an audit log with full context must be sent without sensitive information
        ArgumentCaptor<Context> context = ArgumentCaptor.forClass(Context.class);
        verify(auditLogger, times(1)).sendAuditLog(context.capture());
        verifyContext(context.getValue(),
                // Basic mandatory fields must be filled
                AuditLogFieldEnum.TIMESTAMP, is(not(nullValue())), //
                AuditLogFieldEnum.REQUEST_ID, is(not(nullValue())), //
                AuditLogFieldEnum.LOG_ID, is(not(nullValue())), //
                // User information must be filled
                AuditLogFieldEnum.ACCOUNT_ID, is("account1"), //
                AuditLogFieldEnum.USER_ID, is("user1"), //
                AuditLogFieldEnum.USERNAME, is("ejarvis"), //
                AuditLogFieldEnum.EMAIL, is("edwin.jarvis@talend.com"), //
                // Other mandatory contextual information must be filled
                AuditLogFieldEnum.APPLICATION_ID, is("daikon"), //
                AuditLogFieldEnum.EVENT_TYPE, is("security"), //
                AuditLogFieldEnum.EVENT_CATEGORY, is("resource"), //
                AuditLogFieldEnum.EVENT_OPERATION, is("create"), //
                AuditLogFieldEnum.CLIENT_IP, is("0.0.0.0"), //
                // Request payload must be filled
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"http://localhost/resource\"", AuditLogFieldEnum.URL.getId())),
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.METHOD.getId(), HttpMethod.POST)),
                // Verify that request doesn't contain any sensitive value
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.REQUEST_BODY.getId(),
                        StringEscapeUtils
                                .escapeJava(objectMapper.writeValueAsString(createdResource).replace("sensitiveValue", "")))),
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"myUserAgent\"", AuditLogFieldEnum.USER_AGENT.getId())),
                // Response payload must be filled
                AuditLogFieldEnum.RESPONSE,
                containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.RESPONSE_BODY.getId(),
                        StringEscapeUtils.escapeJava(objectMapper.writeValueAsString(createdResource)))),
                AuditLogFieldEnum.RESPONSE,
                containsString(String.format("\"%s\":\"200\"", AuditLogFieldEnum.RESPONSE_CODE.getId())));
    }

    /**
     * GIVEN a full context configured
     * AND the @GenerateAuditLog well configured placed on a RestController method
     * WHEN the method is called (a rest request has been performed)
     * AND an exception occurs
     * THEN an audit log with full context must be sent with code 500
     * AND the exception is thrown
     */
    @Test
    public void testGenerateAuditLogException() throws Throwable {

        // GIVEN a full context configured
        // AND the @GenerateAuditLog well configured placed on a RestController method

        // Mock @GenerateAuditLog annotation as following :
        // @GenerateAuditLog(application = "TMC", eventType = "application security", eventCategory = "user account",
        // eventOperation = "create")
        mockGenerateAuditLog("daikon", "security", "resource", "create", true);

        // Mock current authenticated user
        mockUser("user1", "ejarvis", "edwin.jarvis@talend.com", "account1");

        // Mock HTTP request & response
        Map<String, String> createdResource = new HashMap<>();
        createdResource.put("id", "myResourceId");
        createdResource.put("name", "myResourceName");
        mockHttpRequest("0.0.0.0", "/resource", HttpMethod.POST, objectMapper.writeValueAsString(createdResource), "myUserAgent");
        mockHttpResponse(HttpStatus.OK, createdResource);

        // WHEN the method is called (a rest request has been performed)
        // AND an exception occurs
        when(proceedingJoinPoint.proceed()).thenThrow(new Exception("Ouch !"));
        exceptionRule.expect(Exception.class);
        exceptionRule.expectMessage("Ouch !");
        try {
            auditLogGenerationFilterImpl.auditLogGeneration(proceedingJoinPoint);
        } catch (Exception e) {
            // THEN an audit log with full context must be sent
            ArgumentCaptor<Context> context = ArgumentCaptor.forClass(Context.class);
            verify(auditLogger, times(1)).sendAuditLog(context.capture());
            verifyContext(context.getValue(),
                    // Basic mandatory fields must be filled
                    AuditLogFieldEnum.TIMESTAMP, is(not(nullValue())), //
                    AuditLogFieldEnum.REQUEST_ID, is(not(nullValue())), //
                    AuditLogFieldEnum.LOG_ID, is(not(nullValue())), //
                    // User information must be filled
                    AuditLogFieldEnum.ACCOUNT_ID, is("account1"), //
                    AuditLogFieldEnum.USER_ID, is("user1"), //
                    AuditLogFieldEnum.USERNAME, is("ejarvis"), //
                    AuditLogFieldEnum.EMAIL, is("edwin.jarvis@talend.com"), //
                    // Other mandatory contextual information must be filled
                    AuditLogFieldEnum.APPLICATION_ID, is("daikon"), //
                    AuditLogFieldEnum.EVENT_TYPE, is("security"), //
                    AuditLogFieldEnum.EVENT_CATEGORY, is("resource"), //
                    AuditLogFieldEnum.EVENT_OPERATION, is("create"), //
                    AuditLogFieldEnum.CLIENT_IP, is("0.0.0.0"), //
                    // Request payload must be filled
                    AuditLogFieldEnum.REQUEST,
                    containsString(String.format("\"%s\":\"http://localhost/resource\"", AuditLogFieldEnum.URL.getId())),
                    AuditLogFieldEnum.REQUEST,
                    containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.METHOD.getId(), HttpMethod.POST)),
                    AuditLogFieldEnum.REQUEST,
                    containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.REQUEST_BODY.getId(),
                            StringEscapeUtils.escapeJava(objectMapper.writeValueAsString(createdResource)))),
                    AuditLogFieldEnum.REQUEST,
                    containsString(String.format("\"%s\":\"myUserAgent\"", AuditLogFieldEnum.USER_AGENT.getId())),
                    // Response payload must be filled
                    AuditLogFieldEnum.RESPONSE, not(containsString(AuditLogFieldEnum.RESPONSE_BODY.getId())),
                    AuditLogFieldEnum.RESPONSE,
                    containsString(String.format("\"%s\":\"500\"", AuditLogFieldEnum.RESPONSE_CODE.getId())));
            // AND the exception is thrown
            throw e;
        }
    }

    /**
     * Verify that context has expected key & values
     *
     * @param context Context to check
     * @param o Succession of context keys (AuditLogFieldEnum) and matcher values (Matcher<String>)
     */
    private void verifyContext(Context context, Object... o) {
        IntStream.range(0, o.length).filter(i -> i % 2 == 0).forEach(i -> assertThat(
                String.format("Wrong expected value for key %s", ((AuditLogFieldEnum) o[i]).getId()),
                context.containsKey(((AuditLogFieldEnum) o[i]).getId()) ? context.get(((AuditLogFieldEnum) o[i]).getId()) : null,
                (Matcher<String>) o[i + 1]));
    }

    /**
     * Mock authenticated user
     *
     * @param userId User id
     * @param userName User name
     * @param userEmail User email
     * @param accountId Account id
     */
    private void mockUser(String userId, String userName, String userEmail, String accountId) {
        when(auditUserProvider.getUserId()).thenReturn(userId);
        when(auditUserProvider.getUsername()).thenReturn(userName);
        when(auditUserProvider.getUserEmail()).thenReturn(userEmail);
        when(auditUserProvider.getAccountId()).thenReturn(accountId);
    }

    /**
     * Mock HTTP request
     *
     * @param remoteAddress Caller remote address
     * @param url Request URL
     * @param method Request method
     * @param body Request body
     * @param userAgent Caller user agent
     */
    private void mockHttpRequest(String remoteAddress, String url, HttpMethod method, Object body, String userAgent) {
        // Mock HttpServletRequest
        request = new MockHttpServletRequest();
        if (remoteAddress != null) {
            request.setRemoteAddr(remoteAddress);
        }
        if (url != null) {
            request.setRequestURI(url);
        }
        if (method != null) {
            request.setMethod(method.name());
        }
        if (userAgent != null) {
            request.addHeader("User-Agent", userAgent);
        }
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));
        // Mock @RequestBody annotation & arg if needed
        Annotation[][] annotations = { {} };
        Object[] args = {};
        if (body != null) {
            Annotation requestBody = mock(Annotation.class);
            doReturn(RequestBody.class).when(requestBody).annotationType();
            annotations = new Annotation[][] { { requestBody } };
            args = new Object[] { body };
        }
        when(this.method.getParameterAnnotations()).thenReturn(annotations);
        when(proceedingJoinPoint.getArgs()).thenReturn(args);
    }

    /**
     * Mock HTTP response
     *
     * @param status Response status code
     * @param body Response body
     * @throws Throwable
     */
    private void mockHttpResponse(HttpStatus status, Object body) throws Throwable {
        // Mock HttpServletResponse
        MockHttpServletResponse response = new MockHttpServletResponse();
        if (status != null) {
            response.setStatus(status.value());
        }
        if (body != null) {
            response.getWriter().print(body);
            // Mock object returned by annotated method if needed
            when(proceedingJoinPoint.proceed()).thenReturn(body);
        }
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));
    }

    /**
     * Mock @GenerateAuditLog annotation with default NoOp filter
     *
     * @param application application param value
     * @param eventType eventType param value
     * @param eventCategory eventCategory param value
     * @param eventOperation eventOperation param value
     * @param includeBodyResponse includeBodyResponse param value
     */
    private void mockGenerateAuditLog(String application, String eventType, String eventCategory, String eventOperation,
            boolean includeBodyResponse) {
        mockGenerateAuditLog(application, eventType, eventCategory, eventOperation, includeBodyResponse,
                NoOpAuditContextFilter.class);
    }

    /**
     * Mock @GenerateAuditLog annotation with custom filter
     *
     * @param application application param value
     * @param eventType eventType param value
     * @param eventCategory eventCategory param value
     * @param eventOperation eventOperation param value
     * @param includeBodyResponse includeBodyResponse param value
     * @param filterClass custom filter class
     */
    private void mockGenerateAuditLog(String application, String eventType, String eventCategory, String eventOperation,
            boolean includeBodyResponse, Class<? extends AuditContextFilter> filterClass) {
        GenerateAuditLog annotation = mock(GenerateAuditLog.class);
        when(annotation.application()).thenReturn(application);
        when(annotation.eventType()).thenReturn(eventType);
        when(annotation.eventCategory()).thenReturn(eventCategory);
        when(annotation.eventOperation()).thenReturn(eventOperation);
        when(annotation.includeBodyResponse()).thenReturn(includeBodyResponse);
        doReturn(filterClass).when(annotation).filter();
        when(method.getAnnotation(GenerateAuditLog.class)).thenReturn(annotation);
    }

    private void mockResponseStatus(HttpStatus httpStatus) {
        ResponseStatus annotation = mock(ResponseStatus.class);
        when(annotation.value()).thenReturn(httpStatus);
        when(method.getAnnotation(ResponseStatus.class)).thenReturn(annotation);
    }
}
