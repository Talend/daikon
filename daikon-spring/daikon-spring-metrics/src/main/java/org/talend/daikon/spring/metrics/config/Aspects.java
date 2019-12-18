// ============================================================================
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.daikon.spring.metrics.config;

import brave.ScopedSpan;
import brave.Tracer;
import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.talend.daikon.multitenant.context.TenancyContext;
import org.talend.daikon.multitenant.context.TenancyContextHolder;
import org.talend.daikon.multitenant.core.Tenant;
import org.talend.daikon.spring.metrics.LogTimed;
import org.talend.daikon.spring.metrics.io.Metered;
import org.talend.daikon.spring.metrics.io.MeteredInputStream;
import org.talend.daikon.spring.metrics.io.MeteredOutputStream;
import org.talend.daikon.spring.metrics.io.PartWrapper;
import org.talend.daikon.spring.metrics.io.ReleasableInputStream;
import org.talend.daikon.spring.metrics.io.ReleasableOutputStream;
import org.talend.daikon.spring.metrics.io.SpanOutputStream;
import org.talend.daikon.spring.metrics.util.VariableLevelLog;

import javax.servlet.http.Part;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Callable;

@Aspect
public class Aspects {

    private final MeterRegistry repository;

    private final Optional<Tracer> tracer;

    private static final Logger LOGGER = LoggerFactory.getLogger(Aspects.class);

    /** Tenant ID returned when not in multi-tenancy context. */
    private String NONE_TENANT_ID = "none";

    private static final String ANONYMOUS_USER_NAME = "anonymous";

    public Aspects(Tracer tracer, MeterRegistry repository) {
        this.tracer = Optional.ofNullable(tracer);
        this.repository = repository;
    }

    private static String getCategory(Signature signature) {
        final Class clazz = signature.getDeclaringType();
        final String method = signature.getName();
        return clazz.getTypeName() + '.' + method;
    }

    @Around("@annotation(io.micrometer.core.annotation.Timed)")
    public Object timed(ProceedingJoinPoint pjp) throws Throwable {
        final MethodSignature signature = (MethodSignature) pjp.getSignature();
        final String spanName = getCategory(signature);
        if (Callable.class.isAssignableFrom(signature.getReturnType())) {
            Callable callable = (Callable) pjp.proceed();
            return (Callable) () -> {
                long start = System.currentTimeMillis();
                final ScopedSpan span = startSpan(spanName);
                try {
                    return callable.call();
                } finally {
                    long time = System.currentTimeMillis() - start;
                    repository.counter(metricName(pjp), "type", "sum").increment(time);
                    repository.counter(metricName(pjp), "type", "count").increment();
                    span.finish();
                }
            };
        } else {
            long start = System.currentTimeMillis();
            final ScopedSpan span = startSpan(spanName);
            try {
                return pjp.proceed();
            } finally {
                long time = System.currentTimeMillis() - start;
                repository.counter(metricName(pjp), "type", "sum").increment(time);
                repository.counter(metricName(pjp), "type", "count").increment();
                span.finish();
            }
        }
    }

    private String metricName(ProceedingJoinPoint pjp) {
        return getCategory(pjp.getSignature());
    }

    private ScopedSpan startSpan(String spanName) {
        return tracer.map(t -> t.startScopedSpan(spanName)).orElse(null);
    }

    @Around("@annotation(org.talend.daikon.spring.metrics.VolumeMetered)")
    public Object volumeMetered(ProceedingJoinPoint pjp) throws Throwable {
        // Find first InputStream available in arguments
        Object[] wrappedArgs = new Object[pjp.getArgs().length];
        final Object[] args = pjp.getArgs();
        for (int i = 0; i < pjp.getArgs().length; i++) {
            Object o = args[i];
            if (o instanceof InputStream) {
                wrappedArgs[i] = new MeteredInputStream((InputStream) o);
            } else if (o instanceof OutputStream) {
                wrappedArgs[i] = new MeteredOutputStream(processOutputStream((OutputStream) o));
            } else if (o instanceof Part) {
                final Part delegate = (Part) o;
                wrappedArgs[i] = new PartWrapper(delegate);
            } else {
                wrappedArgs[i] = o;
            }
        }
        try {
            final Object proceed = pjp.proceed(wrappedArgs);
            if (proceed instanceof InputStream) {
                final MeteredInputStream inputStream = new MeteredInputStream((InputStream) proceed);
                return new ReleasableInputStream(inputStream,
                        () -> repository.counter(metricName(pjp) + ".volume", "type", "in").increment(inputStream.getVolume()));
            } else if (proceed instanceof OutputStream) {
                final MeteredOutputStream outputStream = new MeteredOutputStream(processOutputStream((OutputStream) proceed));
                return new ReleasableOutputStream(outputStream,
                        () -> repository.counter(metricName(pjp) + ".volume", "type", "out").increment(outputStream.getVolume()));
            } else {
                return proceed;
            }
        } finally {
            for (Object wrappedArg : wrappedArgs) {
                if (wrappedArg instanceof Metered) {
                    final Metered metered = (Metered) wrappedArg;
                    repository.counter(metricName(pjp), "type", metered.getType().getTag()).increment(metered.getVolume());
                }
            }
        }
    }

    private OutputStream processOutputStream(OutputStream o) {
        OutputStream outputStream = o;
        if (tracer.isPresent()) {
            outputStream = new SpanOutputStream(outputStream);
        }
        return outputStream;
    }

    @Around("@annotation(org.talend.dataprep.metrics.LogTimed)")
    public Object logTimed(ProceedingJoinPoint pjp) throws Throwable {

        MethodSignature signature = (MethodSignature) pjp.getSignature();

        LogTimed logTimed = signature.getMethod().getAnnotation(LogTimed.class);
        Level logLevel = logTimed.logLevel();

        if (!VariableLevelLog.isEnabledFor(LOGGER, logLevel)) {
            return pjp.proceed();
        }

        Logger loggerExecution = LoggerFactory.getLogger(pjp.getSignature().getDeclaringType());

        final String methodName = signature.getName();

        String args = Arrays.toString(pjp.getArgs());

        String tenantId = getTenantId();
        String userId = getUserId();

        String startMessage = logTimed.startMessage().isEmpty() ? "Call" : logTimed.startMessage();

        if (!logTimed.additionalMessage().isEmpty()) {
            VariableLevelLog.log(loggerExecution, logLevel, logTimed.additionalMessage());
        }

        if (logTimed.displayStartingMessage()) {
            VariableLevelLog.log(loggerExecution, logLevel, "{}: [{}] with args {}. [tenantId = {}, userId = {}]",
                    new Object[] { startMessage, methodName, args, tenantId, userId });
        }

        Instant start = Instant.now();
        Object output;
        try {
            output = pjp.proceed();
        } finally {

            Instant finish = Instant.now();

            long elapsedTime = Duration.between(start, finish).toMillis();

            String stopMessage = logTimed.endMessage().isEmpty() ? "End call" : logTimed.endMessage();

            VariableLevelLog.log(loggerExecution, logLevel,
                    "{}: Elapsed time {} ms for [{}] with args {}: . [tenantId = {}, userId = {}]",
                    new Object[] { stopMessage, elapsedTime, methodName, args, tenantId, userId });
        }
        return output;
    }

    private String getTenantId() {
        final TenancyContext context = TenancyContextHolder.getContext();
        Optional<Tenant> tenant = context.getOptionalTenant();
        if (!tenant.isPresent()) {
            return NONE_TENANT_ID;
        }
        try {
            return String.valueOf(tenant.get().getIdentity());
        } catch (RuntimeException e) {
            LOGGER.debug("Unable to find tenancy information.", e);
            return "<missing>";
        }
    }

    private static String getUserId() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            Authentication authentication = context.getAuthentication();
            if (authentication != null) {
                return authentication.getName();
            }
        }
        LOGGER.debug("Unable to find user information.");
        return ANONYMOUS_USER_NAME;
    }
}