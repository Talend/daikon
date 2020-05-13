package org.talend.daikon.spring.audit.logs.api;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.stream.IntStream;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuditLogTestApp.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = { "audit.enabled=true", "spring.application.name=daikon",
        "audit.kafka.bootstrapServers=localhost:9092" })
@Import(AuditLogTestConfig.class)
public class AuditLogTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuditLoggerBase auditLoggerBase;

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
        logger.addAppender(logListAppender);
    }

    @Test
    @WithUserDetails
    public void testGet400Exception() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_400_EXCEPTION)).andExpect(status().isBadRequest());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.GET_400_EXCEPTION, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.BAD_REQUEST, null));
    }

    @Test
    @WithUserDetails
    public void testGet400Annotation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_400_ANNOTATION)).andExpect(status().isBadRequest());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.GET_400_ANNOTATION, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.BAD_REQUEST, AuditLogTestApp.BODY_RESPONSE));
    }

    @Test
    @WithUserDetails
    public void testGet400ResponseEntity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_400_RESPONSE_ENTITY)).andExpect(status().isBadRequest())
                .andExpect(content().string(AuditLogTestApp.BODY_RESPONSE_400)).andReturn();

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.GET_400_RESPONSE_ENTITY, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.BAD_REQUEST, AuditLogTestApp.BODY_RESPONSE_400));
    }

    @Test
    @WithAnonymousUser
    public void testGet401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_401)).andExpect(status().isUnauthorized());

        verify(auditLoggerBase, times(0)).log(any(), any(), any(), any(), any());
        assertThat(logListAppender.list.get(0).getLevel(), is(Level.ERROR));
    }

    @Test
    @WithUserDetails
    public void testGet403() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_403)).andExpect(status().isForbidden());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.GET_403, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.FORBIDDEN, null));
    }

    @Test
    @WithUserDetails
    public void testGet404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_404)).andExpect(status().isNotFound());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.GET_404, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.NOT_FOUND, null));
    }

    @Test
    @WithAnonymousUser
    public void testGet200Anonymous() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_WITH_BODY)).andExpect(status().isOk());

        verify(auditLoggerBase, times(0)).log(any(), any(), any(), any(), any());
        assertThat(logListAppender.list.get(0).getLevel(), is(Level.ERROR));
    }

    @Test
    @WithUserDetails
    public void testGet200Body() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_WITH_BODY)).andExpect(status().isOk());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.GET_200_WITH_BODY, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.OK, AuditLogTestApp.BODY_RESPONSE));
    }

    @Test
    @WithUserDetails
    public void testGet200NoBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_WITHOUT_BODY)).andExpect(status().isOk());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.GET_200_WITHOUT_BODY, HttpMethod.GET, null));
        verifyContext(httpResponseContextCheck(HttpStatus.OK, null));
    }

    @Test
    @WithUserDetails
    public void testPost200Filtered() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(AuditLogTestApp.POST_200_FILTERED).content("Any content"))
                .andExpect(status().isOk());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.POST_200_FILTERED, HttpMethod.POST,
                AuditLogTestApp.FILTERED_BODY_REQUEST));
        verifyContext(httpResponseContextCheck(HttpStatus.OK, AuditLogTestApp.FILTERED_BODY_RESPONSE));
    }

    @Test
    @WithUserDetails
    public void testPost204() throws Exception {
        final String content = "myContent";
        mockMvc.perform(MockMvcRequestBuilders.post(AuditLogTestApp.POST_204).content(content)).andExpect(status().isNoContent());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.POST_204, HttpMethod.POST, content));
        verifyContext(httpResponseContextCheck(HttpStatus.NO_CONTENT, null));
    }

    @Test
    @WithUserDetails
    public void testPost500() throws Exception {
        final String content = "myContent";
        mockMvc.perform(MockMvcRequestBuilders.post(AuditLogTestApp.POST_500).content(content))
                .andExpect(status().isInternalServerError());

        verifyContext(basicContextCheck());
        verifyContext(httpRequestContextCheck(AuditLogTestApp.POST_500, HttpMethod.POST, content));
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
                AuditLogFieldEnum.CLIENT_IP, is("127.0.0.1") //
        };
    }

    private Object[] httpRequestContextCheck(String url, HttpMethod method, String body) {
        return new Object[] { AuditLogFieldEnum.REQUEST, //
                containsString(String.format("\"%s\":\"http://localhost%s\"", AuditLogFieldEnum.URL.getId(), url)), //
                AuditLogFieldEnum.REQUEST, //
                containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.METHOD.getId(), method)), //
                AuditLogFieldEnum.REQUEST, //
                body == null ? not(containsString(String.format("\"%s\"", AuditLogFieldEnum.REQUEST_BODY.getId())))
                        : containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.REQUEST_BODY.getId(), body)) };
    }

    private Object[] httpResponseContextCheck(HttpStatus status, String body) {
        return new Object[] { AuditLogFieldEnum.RESPONSE, //
                containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.RESPONSE_CODE.getId(), status.value())), //
                AuditLogFieldEnum.RESPONSE, //
                body == null ? not(containsString(String.format("\"%s\"", AuditLogFieldEnum.RESPONSE_BODY.getId())))
                        : containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.RESPONSE_BODY.getId(), body)), //
        };
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
