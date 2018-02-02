package org.talend.daikon.spring.mongo;

import com.mongodb.MongoClientURI;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class SynchronizedMongoClientProviderTest {

    @Test
    public void shouldNotCloseInCaseOfConcurrentUsage() {
        // given
        final MongoClientProvider mongoClientProvider = mock(MongoClientProvider.class);
        SynchronizedMongoClientProvider provider = new SynchronizedMongoClientProvider(mongoClientProvider);
        final TenantInformationProvider tenantInformationProvider = mock(TenantInformationProvider.class);
        when(tenantInformationProvider.getDatabaseURI()).thenReturn(new MongoClientURI("mongodb://no_host"));

        // when
        provider.get(tenantInformationProvider);
        provider.get(tenantInformationProvider);
        provider.close(tenantInformationProvider);
        provider.close(tenantInformationProvider);

        // then
        verify(mongoClientProvider, times(1)).close(any());
    }

    @Test
    public void shouldCloseInCaseOfSequentialUsage() {
        // given
        final MongoClientProvider mongoClientProvider = mock(MongoClientProvider.class);
        SynchronizedMongoClientProvider provider = new SynchronizedMongoClientProvider(mongoClientProvider);
        final TenantInformationProvider tenantInformationProvider = mock(TenantInformationProvider.class);
        when(tenantInformationProvider.getDatabaseURI()).thenReturn(new MongoClientURI("mongodb://no_host"));

        // when
        provider.get(tenantInformationProvider);
        provider.close(tenantInformationProvider);
        provider.get(tenantInformationProvider);
        provider.close(tenantInformationProvider);

        // then
        verify(mongoClientProvider, times(2)).close(any());
    }

    @Test
    public void shouldCloseInCaseOfDifferentDatabases() {
        // given
        final MongoClientProvider mongoClientProvider = mock(MongoClientProvider.class);
        SynchronizedMongoClientProvider provider = new SynchronizedMongoClientProvider(mongoClientProvider);
        final TenantInformationProvider tenantInformationProvider1 = mock(TenantInformationProvider.class);
        final TenantInformationProvider tenantInformationProvider2 = mock(TenantInformationProvider.class);
        when(tenantInformationProvider1.getDatabaseURI()).thenReturn(new MongoClientURI("mongodb://no_host_1"));
        when(tenantInformationProvider2.getDatabaseURI()).thenReturn(new MongoClientURI("mongodb://no_host_2"));

        // when
        provider.get(tenantInformationProvider1);
        provider.get(tenantInformationProvider2);
        provider.close(tenantInformationProvider1);
        provider.close(tenantInformationProvider2);

        // then
        verify(mongoClientProvider, times(2)).close(any());
    }

}