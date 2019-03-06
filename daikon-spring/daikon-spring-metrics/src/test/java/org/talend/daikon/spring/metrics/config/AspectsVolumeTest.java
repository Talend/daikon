package org.talend.daikon.spring.metrics.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import javax.servlet.http.Part;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Test;
import org.talend.daikon.spring.metrics.io.Metered;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

public class AspectsVolumeTest {

    private Aspects aspects;

    private Counter counter;

    private ProceedingJoinPoint point;

    @Before
    public void setUp() throws Throwable {
        final MeterRegistry meterRegistry = mock(MeterRegistry.class);
        counter = mock(Counter.class);
        when(meterRegistry.counter(anyString(), any(String.class))).thenReturn(counter);

        aspects = new Aspects(null, meterRegistry);

        point = mock(ProceedingJoinPoint.class);
        MethodSignature methodSignature = mock(MethodSignature.class);
        final Method testMethod = this.getClass().getMethod("testMethod", OutputStream.class, InputStream.class, Part.class);
        when(methodSignature.getDeclaringType()).thenReturn(this.getClass());
        when(methodSignature.getMethod()).thenReturn(testMethod);
        when(methodSignature.getReturnType()).thenReturn(OutputStream.class);
        when(methodSignature.getDeclaringType()).thenReturn(this.getClass());
        when(point.getArgs()).thenReturn(new Object[] { mock(OutputStream.class), mock(InputStream.class), mock(Part.class) });
        when(point.getSignature()).thenReturn(methodSignature);
        when(point.proceed()).thenReturn(mock(OutputStream.class));
    }

    @Test
    public void shouldTimeMethodExecution() throws Throwable {
        // when
        aspects.volumeMetered(point);

        // then
        verify(counter, times(3)).increment(anyDouble());
        verify(point, times(1)).proceed(argThat(args -> Stream.of(args).allMatch(arg -> arg instanceof Metered)));
    }

    public OutputStream testMethod(OutputStream out, InputStream in, Part part) {
        return new ByteArrayOutputStream();
    }
}