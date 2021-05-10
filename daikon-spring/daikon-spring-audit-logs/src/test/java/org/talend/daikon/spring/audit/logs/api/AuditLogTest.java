package org.talend.daikon.spring.audit.logs.api;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.talend.daikon.spring.audit.logs.api.AuditLogTest.TRUSTED_PROXIES;

import java.util.stream.IntStream;

import org.apache.commons.text.StringEscapeUtils;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.talend.daikon.spring.audit.logs.config.AuditLogTestConfig;
import org.talend.daikon.spring.audit.logs.model.AuditLogFieldEnum;
import org.talend.daikon.spring.audit.logs.service.AuditLogSenderImpl;
import org.talend.logging.audit.Context;
import org.talend.logging.audit.impl.AuditLoggerBase;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.micrometer.core.instrument.Counter;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuditLogTestApp.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = { //
        "audit.enabled=true", //
        "spring.application.name=daikon", //
        "audit.kafka.bootstrapServers=localhost:9092", //
        "audit.trusted-proxies=" + TRUSTED_PROXIES, //
        "audit.kafka.block-timeout-ms=10000" })
@Import(AuditLogTestConfig.class)
public class AuditLogTest {

    public static final String TRUSTED_PROXIES = "42.42.42.42";

    private static final String REMOTE_IP_HEADER = "X-Forwarded-For";

    private static final String X_FORWARDED_HOST = "X-Forwarded-Host";

    private static final String MY_IP = "35.74.154.242";

    private static final String MY_IP_WITH_INVALID_IP = "35.74.154.242, ImAWrongIp";

    private static final String MY_IP_WITH_INTERNAL_PROXIES = "35.74.154.242, 10.72.5.245, 10.80.17.172";

    private static final String MY_IP_WITH_INTERNAL_PROXIES_AND_TRUSTED_PROXIES = "35.74.154.242, 42.42.42.42, 10.72.5.245, 10.80.17.172";

    private static final String MY_IP_WITH_FORGERY_ATTEMPT = "35.74.154.242, 51.51.51.51";

    private static final String MY_IP_WITH_FORGERY_ATTEMPT_AND_INTERNAL_PROXIES = "35.74.154.242, 51.51.51.51, 10.72.5.245, 10.80.17.172";

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuditLoggerBase auditLoggerBase;

    @MockBean
    private Counter auditLogsGeneratedCounter;

    @Autowired
    private MockMvc mockMvc;

    private ListAppender<ILoggingEvent> logListAppender;

    @Before
    public void setUp() {
        // Rest auditLogger mock
        reset(auditLoggerBase);
        // Set up logger list appender
        Logger logger = (Logger) LoggerFactory.getLogger(AuditLogSenderImpl.class);
        logListAppender = new ListAppender<>();
        logListAppender.start();
        logger.setLevel(Level.DEBUG);
        logger.addAppender(logListAppender);
    }

    @Test
    @WithUserDetails
    public void testLoggerProblem() throws Exception {
        // Given Kafka is down
        Mockito //
                .doThrow(new RuntimeException("Failure when sending the audit log to Kafka")) //
                .when(auditLoggerBase) //
                .log(any(), any(), any(), any(), any());

        // When a request generating an audit logs is performed
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_WITH_BODY).header(REMOTE_IP_HEADER, MY_IP))
                // Then it must be a success anyway
                .andExpect(status().isOk());

        // And a simple log must be generated
        assertThat(lastLog().getLevel(), is(Level.WARN));
        assertThat(lastLog().getFormattedMessage(), containsString(AuditLogFieldEnum.TIMESTAMP.getId()));
        assertThat(lastLog().getFormattedMessage(), containsString(AuditLogTestApp.ACCOUNT_ID));
        assertThat(lastLog().getFormattedMessage(), containsString(AuditLogTestApp.APPLICATION));
        assertThat(lastLog().getFormattedMessage(), containsString(AuditLogTestApp.EVENT_CATEGORY));
        assertThat(lastLog().getFormattedMessage(), containsString(AuditLogTestApp.EVENT_OPERATION));
        assertThat(lastLog().getFormattedMessage(), containsString(AuditLogTestApp.EVENT_TYPE));
        assertThat(lastLog().getThrowableProxy().getMessage(), containsString("Failure when sending the audit log to Kafka"));
    }

    @Test
    @WithUserDetails
    public void testAuditLogCounter() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_WITH_BODY).header(REMOTE_IP_HEADER, MY_IP))
                .andExpect(status().isOk());
        verify(auditLogsGeneratedCounter, times(1)).increment();

    }

    @Test
    @WithUserDetails
    public void testGet400Exception() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_400_EXCEPTION).header(REMOTE_IP_HEADER, MY_IP))
                .andExpect(status().isBadRequest());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.GET_400_EXCEPTION, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.BAD_REQUEST, null));
    }

    @Test
    @WithUserDetails
    public void testGet400Annotation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_400_ANNOTATION).header(REMOTE_IP_HEADER, MY_IP))
                .andExpect(status().isBadRequest());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.GET_400_ANNOTATION, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.BAD_REQUEST, AuditLogTestApp.BODY_RESPONSE));
    }

    @Test
    @WithUserDetails
    public void testGet400ResponseEntity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_400_RESPONSE_ENTITY).header(REMOTE_IP_HEADER, MY_IP))
                .andExpect(status().isBadRequest()).andExpect(content().string(AuditLogTestApp.BODY_RESPONSE_400)).andReturn();

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.GET_400_RESPONSE_ENTITY, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.BAD_REQUEST, AuditLogTestApp.BODY_RESPONSE_400));
    }

    @Test
    @WithUserDetails
    public void testGet400ErrorOnly() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_400_ERROR_ONLY).header(REMOTE_IP_HEADER, MY_IP))
                .andExpect(status().isBadRequest());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.GET_400_ERROR_ONLY, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.BAD_REQUEST, null));
    }

    @Test
    @WithUserDetails
    public void testGet400SuccessOnly() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_400_SUCCESS_ONLY).header(REMOTE_IP_HEADER, MY_IP))
                .andExpect(status().isBadRequest());

        verify(auditLoggerBase, times(0)).log(any(), any(), any(), any(), any());
    }

    @Test
    @WithAnonymousUser
    public void testGet401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_401).header(REMOTE_IP_HEADER, MY_IP))
                .andExpect(status().isUnauthorized());

        verify(auditLoggerBase, times(0)).log(any(), any(), any(), any(), any());
        assertThat(lastLog().getLevel(), is(Level.DEBUG));
    }

    @Test
    @WithUserDetails
    public void testGet403() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_403).header(REMOTE_IP_HEADER, MY_IP))
                .andExpect(status().isForbidden());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.GET_403, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.FORBIDDEN, null));
    }

    @Test
    @WithUserDetails
    public void testGet404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_404).header(REMOTE_IP_HEADER, MY_IP))
                .andExpect(status().isNotFound());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.GET_404, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.NOT_FOUND, null));
    }

    @Test
    @WithAnonymousUser
    public void testGet200Anonymous() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_WITH_BODY).header(REMOTE_IP_HEADER, MY_IP))
                .andExpect(status().isOk());

        verify(auditLoggerBase, times(0)).log(any(), any(), any(), any(), any());
        assertThat(lastLog().getLevel(), is(Level.DEBUG));
    }

    @Test
    @WithUserDetails
    public void testGet200Body() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_WITH_BODY).header(REMOTE_IP_HEADER, MY_IP))
                .andExpect(status().isOk());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.GET_200_WITH_BODY, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.OK, AuditLogTestApp.BODY_RESPONSE));
    }

    @Test
    @WithUserDetails
    public void testGet200BodyWithXFH() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_WITH_BODY).header(REMOTE_IP_HEADER, MY_IP)
                // simulate the application is running behind the LB
                .header(X_FORWARDED_HOST, "iam.qa.cloud.talend.com")).andExpect(status().isOk());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheckWithLBHost(AuditLogTestApp.GET_200_WITH_BODY, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.OK, AuditLogTestApp.BODY_RESPONSE));
    }

    @Test
    @WithUserDetails
    public void testGet200BodyWithXfhAndPort() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_WITH_BODY).header(REMOTE_IP_HEADER, MY_IP)
                // simulate the application is running behind the LB that adds port to X-Forwarded-Host header
                .header(X_FORWARDED_HOST, "iam.qa.cloud.talend.com:443")).andExpect(status().isOk());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheckWithLBHost(AuditLogTestApp.GET_200_WITH_BODY, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.OK, AuditLogTestApp.BODY_RESPONSE));
    }

    @Test
    @WithUserDetails
    public void testGet200NoBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_WITHOUT_BODY).header(REMOTE_IP_HEADER, MY_IP))
                .andExpect(status().isOk());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.GET_200_WITHOUT_BODY, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.OK, null));
    }

    @Test
    @WithUserDetails
    public void testGet200ErrorOnly() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_ERROR_ONLY).header(REMOTE_IP_HEADER, MY_IP))
                .andExpect(status().isOk());

        verify(auditLoggerBase, times(0)).log(any(), any(), any(), any(), any());
    }

    @Test
    @WithUserDetails
    public void testGet200SuccessOnly() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_SUCCESS_ONLY).header(REMOTE_IP_HEADER, MY_IP))
                .andExpect(status().isOk());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.GET_200_SUCCESS_ONLY, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.OK, AuditLogTestApp.BODY_RESPONSE));
    }

    @Test
    @WithUserDetails
    public void testGet200InvalidIp() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_WITH_BODY).header(REMOTE_IP_HEADER, MY_IP_WITH_INVALID_IP))
                .andExpect(status().isOk());

        verifyContext(basicContextCheck());
        verifyContext(AuditLogFieldEnum.CLIENT_IP, is(MY_IP));
    }

    @Test
    @WithUserDetails
    public void testGet200InternalProxies() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_WITH_BODY).header(REMOTE_IP_HEADER,
                MY_IP_WITH_INTERNAL_PROXIES)).andExpect(status().isOk());

        verifyContext(basicContextCheck());
        verifyContext(AuditLogFieldEnum.CLIENT_IP, is(MY_IP));
    }

    @Test
    @WithUserDetails
    public void testGet200InternalAndTrustedProxies() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_WITH_BODY).header(REMOTE_IP_HEADER,
                MY_IP_WITH_INTERNAL_PROXIES_AND_TRUSTED_PROXIES)).andExpect(status().isOk());

        verifyContext(basicContextCheck());
        verifyContext(AuditLogFieldEnum.CLIENT_IP, is(MY_IP));
    }

    @Test
    @WithUserDetails
    public void testGet200ForgeryAttempt() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_WITH_BODY).header(REMOTE_IP_HEADER,
                MY_IP_WITH_FORGERY_ATTEMPT_AND_INTERNAL_PROXIES)).andExpect(status().isOk());

        verifyContext(basicContextCheck());
        verifyContext(AuditLogFieldEnum.CLIENT_IP, is(MY_IP_WITH_FORGERY_ATTEMPT));
    }

    @Test
    @WithUserDetails
    public void testGet200EmptyXForwardedForHeader() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders //
                .get(AuditLogTestApp.GET_200_WITH_BODY) //
                .with(req -> {
                    req.setRemoteAddr(MY_IP);
                    return req;
                })).andExpect(status().isOk());

        verifyContext(basicContextCheck());
        verifyContext(AuditLogFieldEnum.CLIENT_IP, is(MY_IP));
    }

    @Test
    @WithUserDetails
    public void testPost200Filtered() throws Exception {
        AuditLogTestApp.RequestObject request = new AuditLogTestApp.RequestObject("val1", "val2");

        mockMvc.perform(MockMvcRequestBuilders.post(AuditLogTestApp.POST_200_FILTERED) //
                .header(REMOTE_IP_HEADER, MY_IP) //
                .content(objectMapper.writeValueAsString(request)) //
                .contentType(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
        ).andExpect(status().isOk());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.POST_200_FILTERED, HttpMethod.POST,
                objectMapper.writeValueAsString(request.setUnsafeProp(null))));
        verifyContext(httpResponseContextCheck(HttpStatus.OK, objectMapper.writeValueAsString(request.setUnsafeProp(null))));
    }

    @Test
    @WithUserDetails
    public void testGet302SuccessOnly() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_302_SUCCESS_ONLY).header(REMOTE_IP_HEADER, MY_IP))
                .andExpect(status().isFound());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.GET_302_SUCCESS_ONLY, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.FOUND, null));
    }

    @Test
    @WithUserDetails
    public void testPost204() throws Exception {
        final String content = "myContent";
        mockMvc.perform(MockMvcRequestBuilders.post(AuditLogTestApp.POST_204).header(REMOTE_IP_HEADER, MY_IP).content(content))
                .andExpect(status().isNoContent());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.POST_204, HttpMethod.POST, content));
        verifyContext(httpResponseContextCheck(HttpStatus.NO_CONTENT, null));
    }

    @Test
    @WithUserDetails
    public void testPost500() throws Exception {
        final String content = "myContent";
        mockMvc.perform(MockMvcRequestBuilders.post(AuditLogTestApp.POST_500).header(REMOTE_IP_HEADER, MY_IP).content(content))
                .andExpect(status().isInternalServerError());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.POST_500, HttpMethod.POST, content));
        verifyContext(httpResponseContextCheck(HttpStatus.INTERNAL_SERVER_ERROR, null));
    }

    @Test
    @WithUserDetails
    public void testPost500Filtered() throws Exception {
        AuditLogTestApp.RequestObject request = new AuditLogTestApp.RequestObject("val1", "val2");

        mockMvc.perform(MockMvcRequestBuilders.post(AuditLogTestApp.POST_500_FILTERED).header(REMOTE_IP_HEADER, MY_IP) //
                .content(objectMapper.writeValueAsString(request)) //
                .contentType(APPLICATION_JSON) //
                .accept(APPLICATION_JSON) //
        ).andExpect(status().isInternalServerError());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.POST_500_FILTERED, HttpMethod.POST,
                objectMapper.writeValueAsString(request.setUnsafeProp(null))));
        verifyContext(httpResponseContextCheck(HttpStatus.INTERNAL_SERVER_ERROR, null));
    }

    private Object[] basicContextCheck() {
        return new Object[] {
                // Basic mandatory fields must be filled
                AuditLogFieldEnum.TIMESTAMP, is(not(nullValue())), //
                AuditLogFieldEnum.REQUEST_ID, is(not(nullValue())), //
                AuditLogFieldEnum.LOG_ID, is(not(nullValue())), //
                // User information must be filled
                AuditLogFieldEnum.ACCOUNT_ID, is(AuditLogTestApp.ACCOUNT_ID), //
                AuditLogFieldEnum.USER_ID, is(AuditLogTestApp.USER_ID), //
                AuditLogFieldEnum.USERNAME, is(AuditLogTestApp.USERNAME), //
                AuditLogFieldEnum.EMAIL, is(AuditLogTestApp.USER_EMAIL), //
                // Other mandatory contextual information must be filled
                AuditLogFieldEnum.APPLICATION_ID, is(AuditLogTestApp.APPLICATION), //
                AuditLogFieldEnum.EVENT_TYPE, is(AuditLogTestApp.EVENT_TYPE), //
                AuditLogFieldEnum.EVENT_CATEGORY, is(AuditLogTestApp.EVENT_CATEGORY), //
                AuditLogFieldEnum.EVENT_OPERATION, is(AuditLogTestApp.EVENT_OPERATION), //
                AuditLogFieldEnum.CLIENT_IP, containsString(MY_IP) //
        };
    }

    private Object[] httpRequestContextCheck(String url, HttpMethod method, String body) {
        return httpRequestContextCheck("http", "localhost", url, method, body);
    }

    private Object[] httpRequestContextCheckWithLBHost(String url, HttpMethod method, String body) {
        return httpRequestContextCheck("https", "iam.qa.cloud.talend.com", url, method, body);
    }

    private Object[] httpRequestContextCheck(String expectedProto, String expectedHost, String url, HttpMethod method,
            String body) {
        return new Object[] { AuditLogFieldEnum.REQUEST, //
                containsString(
                        String.format("\"%s\":\"%s://%s%s\"", AuditLogFieldEnum.URL.getId(), expectedProto, expectedHost, url)), //
                AuditLogFieldEnum.REQUEST, //
                containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.METHOD.getId(), method)), //
                AuditLogFieldEnum.REQUEST, //
                body == null ? not(containsString(String.format("\"%s\"", AuditLogFieldEnum.REQUEST_BODY.getId())))
                        : containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.REQUEST_BODY.getId(),
                                StringEscapeUtils.escapeJson(body))) };
    }

    private Object[] httpResponseContextCheck(HttpStatus status, String body) {
        return new Object[] { AuditLogFieldEnum.RESPONSE, //
                containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.RESPONSE_CODE.getId(), status.value())), //
                AuditLogFieldEnum.RESPONSE, //
                body == null ? not(containsString(String.format("\"%s\"", AuditLogFieldEnum.RESPONSE_BODY.getId())))
                        : containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.RESPONSE_BODY.getId(),
                                StringEscapeUtils.escapeJson(body))), //
        };
    }

    private ILoggingEvent lastLog() {
        return logListAppender.list.get(logListAppender.list.size() - 1);
    }

    /**
     * Verify that context has expected key & values
     *
     * @param o Succession of context keys (AuditLogFieldEnum) and matcher values (Matcher<String>)
     */
    private void verifyContext(Object... o) {
        // Verify that auditLogger has been called only once and capture the context
        ArgumentCaptor<Context> context = ArgumentCaptor.forClass(Context.class);
        verify(auditLoggerBase, times(1)).log(any(), any(), context.capture(), any(), any());
        // Check if the context contains the expected information
        IntStream.range(0, o.length).filter(i -> i % 2 == 0)
                .forEach(i -> assertThat(String.format("Wrong expected value for key %s", ((AuditLogFieldEnum) o[i]).getId()),
                        context.getValue().containsKey(((AuditLogFieldEnum) o[i]).getId())
                                ? context.getValue().get(((AuditLogFieldEnum) o[i]).getId())
                                : null,
                        (Matcher<String>) o[i + 1]));
    }
}
