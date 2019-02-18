package org.talend.daikon.spring.metrics.config;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Test;

import brave.ScopedSpan;
import brave.Tracer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

public class AspectsTimeTest {

    private Aspects aspects;

    private MethodSignature methodSignature;

    private Counter counter;

    private ProceedingJoinPoint point;

    private Tracer tracer;

    private ScopedSpan span;

    @Before
    public void setUp() throws NoSuchMethodException {
        final MeterRegistry meterRegistry = mock(MeterRegistry.class);
        counter = mock(Counter.class);
        when(meterRegistry.counter(anyString(), any(String.class))).thenReturn(counter);

        tracer = mock(Tracer.class);
        span = mock(ScopedSpan.class);
        when(tracer.startScopedSpan(any())).thenReturn(span);

        aspects = new Aspects(tracer, meterRegistry);

        point = mock(ProceedingJoinPoint.class);
        methodSignature = mock(MethodSignature.class);
        when(methodSignature.getDeclaringType()).thenReturn(this.getClass());
        when(methodSignature.getMethod()).thenReturn(this.getClass().getMethod("testMethod"));
        when(methodSignature.getReturnType()).thenReturn(Void.class);
        when(point.getSignature()).thenReturn(methodSignature);
    }

    @Test
    public void shouldTimeMethodExecution() throws Throwable {
        // given
        when(methodSignature.getDeclaringType()).thenReturn(this.getClass());

        // when
        aspects.timed(point);

        // then
        verify(counter, times(1)).increment(anyDouble());
        verify(counter, times(1)).increment();
        verify(tracer, times(1)).startScopedSpan(anyString());
        verify(span, times(1)).finish();
    }

    @Test
    public void shouldTimeCallableMethodExecution() throws Throwable {
        // given
        when(methodSignature.getReturnType()).thenReturn(Callable.class);
        when(point.proceed()).thenReturn((Callable<Void>) () -> null);

        // when
        final Object callable = aspects.timed(point);

        // then
        assertTrue(callable instanceof Callable);

        // when
        ((Callable) callable).call();

        // then
        verify(counter, times(1)).increment(anyDouble());
        verify(counter, times(1)).increment();
        verify(tracer, times(1)).startScopedSpan(anyString());
        verify(span, times(1)).finish();
    }

    public void testMethod() {
        // Intentionally left empty (here to get a Method object using introspection).
    }
}