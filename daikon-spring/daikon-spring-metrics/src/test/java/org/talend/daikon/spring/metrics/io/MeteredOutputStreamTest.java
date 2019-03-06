package org.talend.daikon.spring.metrics.io;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;

public class MeteredOutputStreamTest {

    private MeteredOutputStream outputStream;

    private OutputStream delegate;

    @Before
    public void setUp() {
        delegate = mock(OutputStream.class);
        outputStream = new MeteredOutputStream(delegate);
    }

    @Test
    public void shouldMeterWriteByte() throws IOException {
        // when
        outputStream.write(0);

        // then
        assertEquals(outputStream.getVolume(), 1);
        verify(delegate, times(1)).write(eq(0));
    }

    @Test
    public void shouldMeterWriteBytes() throws IOException {
        // when
        outputStream.write(new byte[10]);

        // then
        assertEquals(outputStream.getVolume(), 10);
        verify(delegate, times(1)).write(any());
    }

    @Test
    public void shouldMeterReadBytesWithOffset() throws IOException {
        // when
        outputStream.write(new byte[10], 0, 10);

        // then
        assertEquals(outputStream.getVolume(), 10);
        verify(delegate, times(1)).write(any(), eq(0), eq(10));
    }

    @Test
    public void shouldHaveOutType() {
        // when
        final Metered.Type type = outputStream.getType();

        // then
        assertEquals(Metered.Type.OUT, type);
        assertEquals("out", type.getMeterTag());
    }

}