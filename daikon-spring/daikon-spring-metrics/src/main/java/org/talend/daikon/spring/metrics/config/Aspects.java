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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.concurrent.Callable;

import javax.servlet.http.Part;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.talend.daikon.spring.metrics.io.Metered;
import org.talend.daikon.spring.metrics.io.MeteredInputStream;
import org.talend.daikon.spring.metrics.io.MeteredOutputStream;
import org.talend.daikon.spring.metrics.io.PartWrapper;
import org.talend.daikon.spring.metrics.io.ReleasableInputStream;
import org.talend.daikon.spring.metrics.io.ReleasableOutputStream;
import org.talend.daikon.spring.metrics.io.SpanOutputStream;

import brave.ScopedSpan;
import brave.Tracer;
import io.micrometer.core.instrument.MeterRegistry;

@Aspect
public class Aspects {

    private final MeterRegistry repository;

    private final Optional<Tracer> tracer;

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

}
