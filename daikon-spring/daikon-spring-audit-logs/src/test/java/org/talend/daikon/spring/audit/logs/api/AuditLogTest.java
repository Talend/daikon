package org.talend.daikon.spring.audit.logs.api;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.stream.IntStream;

import org.apache.commons.text.StringEscapeUtils;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.talend.daikon.spring.audit.logs.model.AuditLogFieldEnum;
import org.talend.daikon.spring.audit.logs.service.AuditLogSender;
import org.talend.daikon.spring.audit.logs.service.AuditLogger;
import org.talend.logging.audit.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AuditLogTestApp.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = { "audit.enabled=false" })
public class AuditLogTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuditLogger auditLogger;

    @Autowired
    private MockMvc mockMvc;

    private ListAppender<ILoggingEvent> logListAppender;

    @Before
    public void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(AuditLogSender.class);
        logListAppender = new ListAppender<>();
        logListAppender.start();
        logger.addAppender(logListAppender);
    }

    @Test
    @WithAnonymousUser
    public void testGet401() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_401)).andExpect(status().isUnauthorized());

        verify(auditLogger, times(0)).sendAuditLog(any());
    }

    @Test
    @WithUserDetails
    public void testGet403() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_403)).andExpect(status().isForbidden());

        verify(auditLogger, times(0)).sendAuditLog(any());
    }

    @Test
    @WithUserDetails
    public void testGet404() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_404)).andExpect(status().isNotFound());

        verifyContext();
    }

    @Test
    @WithAnonymousUser
    public void testGet200Anonymous() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_WITH_BODY)).andExpect(status().isOk());

        verify(auditLogger, times(0)).sendAuditLog(any());
        assertThat(logListAppender.list.get(0).getLevel(), is(Level.ERROR));
    }

    @Test
    @WithUserDetails
    public void testGet200Body() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_WITH_BODY)).andExpect(status().isOk());

        verifyContext(
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
                AuditLogFieldEnum.CLIENT_IP, is("127.0.0.1"), //
                // Request payload must be filled with minimal mandatory info
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"http://localhost%s\"", AuditLogFieldEnum.URL.getId(),
                        AuditLogTestApp.GET_200_WITH_BODY)), //
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.METHOD.getId(), HttpMethod.GET)), //
                AuditLogFieldEnum.REQUEST, not(containsString(String.format("\"%s\"", AuditLogFieldEnum.REQUEST_BODY.getId()))), //
                // Response payload must be filled with minimal mandatory info
                AuditLogFieldEnum.RESPONSE,
                containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.RESPONSE_BODY.getId(),
                        StringEscapeUtils.escapeJava(objectMapper.writeValueAsString(AuditLogTestApp.BODY_RESPONSE)))), //
                AuditLogFieldEnum.RESPONSE,
                containsString(String.format("\"%s\":\"200\"", AuditLogFieldEnum.RESPONSE_CODE.getId())));
    }

    @Test
    @WithUserDetails
    public void testGet200NoBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(AuditLogTestApp.GET_200_WITHOUT_BODY)).andExpect(status().isOk());

        verifyContext(
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
                AuditLogFieldEnum.CLIENT_IP, is("127.0.0.1"), //
                // Request payload must be filled with minimal mandatory info
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"http://localhost%s\"", AuditLogFieldEnum.URL.getId(),
                        AuditLogTestApp.GET_200_WITHOUT_BODY)), //
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.METHOD.getId(), HttpMethod.GET)), //
                AuditLogFieldEnum.REQUEST, not(containsString(String.format("\"%s\"", AuditLogFieldEnum.REQUEST_BODY.getId()))), //
                // Response payload must be filled with minimal mandatory info
                AuditLogFieldEnum.RESPONSE, not(containsString(String.format("\"%s\"", AuditLogFieldEnum.RESPONSE_BODY.getId()))), //
                AuditLogFieldEnum.RESPONSE,
                containsString(String.format("\"%s\":\"200\"", AuditLogFieldEnum.RESPONSE_CODE.getId())));
    }

    @Test
    @WithUserDetails
    public void testPost200Filtered() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post(AuditLogTestApp.POST_200_FILTERED).content("Any content"))
                .andExpect(status().isOk());

        verifyContext(
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
                AuditLogFieldEnum.CLIENT_IP, is("127.0.0.1"), //
                // Request payload must be filled with minimal mandatory info
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"http://localhost%s\"", AuditLogFieldEnum.URL.getId(),
                        AuditLogTestApp.POST_200_FILTERED)), //
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.METHOD.getId(), HttpMethod.POST)), //
                AuditLogFieldEnum.REQUEST,
                containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.REQUEST_BODY.getId(),
                        AuditLogTestApp.FILTERED_BODY_REQUEST)), //
                // Response payload must be filled with minimal mandatory info
                AuditLogFieldEnum.RESPONSE,
                containsString(String.format("\"%s\":\"%s\"", AuditLogFieldEnum.RESPONSE_BODY.getId(),
                        AuditLogTestApp.FILTERED_BODY_RESPONSE)), //
                AuditLogFieldEnum.RESPONSE,
                containsString(String.format("\"%s\":\"200\"", AuditLogFieldEnum.RESPONSE_CODE.getId())));
    }

    /**
     * Verify that context has expected key & values
     *
     * @param o Succession of context keys (AuditLogFieldEnum) and matcher values (Matcher<String>)
     */
    private void verifyContext(Object... o) {
        // Verify that auditLogger has been called only once and capture the context
        ArgumentCaptor<Context> context = ArgumentCaptor.forClass(Context.class);
        verify(auditLogger, times(1)).sendAuditLog(context.capture());
        // Check if the context contains the expected information
        IntStream.range(0, o.length).filter(i -> i % 2 == 0)
                .forEach(i -> assertThat(String.format("Wrong expected value for key %s", ((AuditLogFieldEnum) o[i]).getId()),
                        context.getValue().containsKey(((AuditLogFieldEnum) o[i]).getId())
                                ? context.getValue().get(((AuditLogFieldEnum) o[i]).getId())
                                : null,
                        (Matcher<String>) o[i + 1]));
        // Rest auditLogger mock
        reset(auditLogger);
    }
}
