package org.talend.daikon.spring.audit.logs.service;

import static org.mockito.Mockito.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.talend.daikon.spring.audit.logs.api.AuditContextFilter;
import org.talend.daikon.spring.audit.logs.api.GenerateAuditLog;
import org.talend.daikon.spring.audit.logs.api.NoOpAuditContextFilter;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = AuditLogGenerationFilterConfiguration.class)
@TestPropertySource(properties = { "spring.application.name=daikon", "audit.enabled=true",
        "audit.kafka.bootstrapServers=localhost:9092" })
public class AuditLogGenerationFilterTest {

    @Autowired
    private AuditLogGenerationFilter auditLogGenerationFilter;

    @Autowired
    private AuditLogger auditLogger;

    private ProceedingJoinPoint proceedingJoinPoint;

    private Method method;

    @Before
    public void setUp() {
        proceedingJoinPoint = mock(ProceedingJoinPoint.class);
        method = mock(Method.class);
        MethodSignature methodSignature = mock(MethodSignature.class);
        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
    }

    @Test
    public void testGenerateAuditLog() throws Throwable {

        mockGenerateAuditLog("TMC", "application security", "user account", "read");
        mockHttpRequest("0.0.0.0", "/users", HttpMethod.GET, null, "my user agent");

        auditLogGenerationFilter.auditLogGeneration(proceedingJoinPoint);

        verify(auditLogger, times(1)).sendAuditLog(any());
    }

    private void mockHttpRequest(String remoteAddress, String url, HttpMethod method, Object body, String userAgent) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(remoteAddress);
        request.setRequestURI(url);
        request.setMethod(method.name());
        request.addHeader("User-Agent", userAgent);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

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

    private void mockGenerateAuditLog(String application, String eventType, String eventCategory, String eventOperation) {
        when(method.getAnnotation(any())).thenReturn(new GenerateAuditLog() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String application() {
                return application;
            }

            @Override
            public String eventType() {
                return eventType;
            }

            @Override
            public String eventCategory() {
                return eventCategory;
            }

            @Override
            public String eventOperation() {
                return eventOperation;
            }

            @Override
            public boolean includeBodyResponse() {
                return false;
            }

            @Override
            public Class<? extends AuditContextFilter> filter() {
                return NoOpAuditContextFilter.class;
            }
        });
    }
}
