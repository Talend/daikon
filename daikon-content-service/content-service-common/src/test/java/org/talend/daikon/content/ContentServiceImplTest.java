package org.talend.daikon.content;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.talend.daikon.exception.TalendRuntimeException;

/**
 * Unit test for the ContentServiceImpl class.
 * @see ContentServiceImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class ContentServiceImplTest {

    @Mock
    private ResourceResolver resourceResolver;

    @InjectMocks
    private ContentServiceImpl service = new ContentServiceImpl();


    @Test
    public void shouldGetResource() throws Exception {
        // given
        final DeletableResource resource = mock(DeletableResource.class);
        final OutputStream output = mock(OutputStream.class);
        when(resource.getOutputStream()).thenReturn(output);
        final String location = "toto";
        when(resourceResolver.getResource(location)).thenReturn(resource);

        // when
        final OutputStream actual = service.get(location);

        // then
        assertNotNull(actual);
        verify(resourceResolver).getResource(location);
    }


    @Test(expected = TalendRuntimeException.class)
    public void getResourceShouldDealWithIOException() throws Exception {
        // given
        final String location = "toto";
        final DeletableResource deletableResource= mock(DeletableResource.class);
        when(resourceResolver.getResource(location)).thenReturn(deletableResource);
        when(deletableResource.getOutputStream()).thenThrow(new IOException("error"));

        // when
        service.get(location);
    }


    @Test
    public void shouldPutResource() throws Exception {
        // given
        final DeletableResource resource = mock(DeletableResource.class);
        final InputStream input = mock(InputStream.class);
        when(resource.getInputStream()).thenReturn(input);
        final String location = "titi";
        when(resourceResolver.getResource(location)).thenReturn(resource);

        // when
        final InputStream actual = service.put(location);

        // then
        assertNotNull(actual);
        verify(resourceResolver).getResource(location);
    }

    @Test(expected = TalendRuntimeException.class)
    public void putResourceShouldDealWithIOException() throws Exception {
        // given
        final String location = "tutu";
        final DeletableResource deletableResource= mock(DeletableResource.class);
        when(resourceResolver.getResource(location)).thenReturn(deletableResource);
        when(deletableResource.getInputStream()).thenThrow(new IOException("error"));

        // when
        service.put(location);
    }

    @Test
    public void shouldDeleteResource() throws Exception {
        // given
        final DeletableResource resource = mock(DeletableResource.class);
        final String location = "tete";
        when(resourceResolver.getResource(location)).thenReturn(resource);

        // when
        service.delete(location);

        // then
        verify(resourceResolver).getResource(location);
        verify(resource).delete();
    }

    @Test(expected = TalendRuntimeException.class)
    public void deleteResourceShouldDealWithException() throws Exception {
        // given
        final DeletableResource resource = mock(DeletableResource.class);
        final String location = "tete";
        when(resourceResolver.getResource(location)).thenReturn(resource);
        doThrow(new IOException("error")).when(resource).delete();

        // when
        service.delete(location);
    }

    @Test
    public void shouldClear() throws Exception {
        // when
        service.clear();

        // then
        verify(resourceResolver).clear(anyString());
    }

    @Test(expected = TalendRuntimeException.class)
    public void clearShouldDealWithErrors() throws Exception {
        //given
        doThrow(new IOException("error")).when(resourceResolver).clear(anyString());

        // when
        service.clear();
    }
}