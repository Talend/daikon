package org.talend.daikon.content.journal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.talend.daikon.content.DeletableResource;
import org.talend.daikon.content.ResourceResolver;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResourceResolverJournalTest {

    @InjectMocks
    private ResourceResolverJournal resourceResolverRepository;

    @Mock
    private ResourceResolver delegate;

    @Test
    public void matches() throws IOException {
        // given
        when(delegate.getResources("/**")).thenReturn(new DeletableResource[0]);

        // when
        resourceResolverRepository.matches("/**");

        // then
        verify(delegate, times(1)).getResources(eq("/**"));
    }
}