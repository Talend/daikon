package org.talend.daikon.content.journal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.talend.daikon.content.DeletableResource;
import org.talend.daikon.content.ResourceResolver;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JournalizedResourceResolverTest {

    @InjectMocks
    private JournalizedResourceResolver journalizedResourceResolver;

    @Mock
    private ResourceJournal resourceJournal;

    @Mock
    private ResourceResolver delegate;

    @Test
    public void shouldAddToRepository() {
        // when
        journalizedResourceResolver.getResource("myResource.txt");

        // then
        verify(delegate, times(1)).getResource(eq("myResource.txt"));
        verify(resourceJournal, times(1)).add(eq("myResource.txt"));
    }

    @Test
    public void shouldListUsingRepository() throws IOException {
        // given
        final DeletableResource resource1 = mock(DeletableResource.class);
        final DeletableResource resource2 = mock(DeletableResource.class);
        when(resourceJournal.ready()).thenReturn(true);
        when(resourceJournal.matches(eq("/**"))).thenReturn(Stream.of("resource1.txt", "resource2.txt"));
        when(resource1.getFilename()).thenReturn("resource1.txt");
        when(resource2.getFilename()).thenReturn("resource2.txt");
        when(delegate.getResource("resource1.txt")).thenReturn(resource1);
        when(delegate.getResource("resource2.txt")).thenReturn(resource2);

        // when
        final DeletableResource[] resources = journalizedResourceResolver.getResources("/**");

        // then
        assertEquals(2, resources.length);
        assertEquals("resource1.txt", resources[0].getFilename());
        assertEquals("resource2.txt", resources[1].getFilename());
        verify(resourceJournal, times(1)).matches(eq("/**"));
    }

    @Test
    public void shouldClearRepository() throws IOException {
        // given
        when(resourceJournal.ready()).thenReturn(true);
        when(resourceJournal.matches("/**")).thenReturn(Stream.of("/location1", "/location2"));
        final DeletableResource location1 = mock(DeletableResource.class);
        final DeletableResource location2 = mock(DeletableResource.class);
        when(delegate.getResource(eq("/location1"))).thenReturn(location1);
        when(delegate.getResource(eq("/location2"))).thenReturn(location2);

        // when
        journalizedResourceResolver.clear("/**");

        // then
        verify(resourceJournal, times(1)).clear(eq("/**"));
        verify(resourceJournal, times(1)).remove(eq("/location1"));
        verify(resourceJournal, times(1)).remove(eq("/location2"));
        verify(location1, times(1)).delete();
        verify(location2, times(1)).delete();
    }
}