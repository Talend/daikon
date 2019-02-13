package org.talend.daikon.spring.metrics.io;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

public class MeteredInputStreamTest {

    private MeteredInputStream inputStream;

    private InputStream delegate;

    @Before
    public void setUp() throws IOException {
        delegate = mock(InputStream.class);
        when(delegate.read(any())).thenReturn(10);
        when(delegate.read(any(), anyInt(), anyInt())).thenReturn(10);
        inputStream = new MeteredInputStream(delegate);
    }

    @Test
    public void shouldMeterReadByte() throws IOException {
        // when
        inputStream.read();

        // then
        assertEquals(inputStream.getVolume(), 1);
        verify(delegate, times(1)).read();
    }

    @Test
    public void shouldMeterReadBytes() throws IOException {
        // when
        inputStream.read(new byte[10]);

        // then
        assertEquals(inputStream.getVolume(), 10);
        verify(delegate, times(1)).read(any());
    }

    @Test
    public void shouldMeterReadBytesWithOffset() throws IOException {
        // when
        inputStream.read(new byte[10], 0, 10);

        // then
        assertEquals(inputStream.getVolume(), 10);
        verify(delegate, times(1)).read(any(), eq(0), eq(10));
    }

    @Test
    public void shouldHaveInType() {
        // when
        final Metered.Type type = inputStream.getType();

        // then
        assertEquals(Metered.Type.IN, type);
        assertEquals("in", type.getMeterTag());
    }
}