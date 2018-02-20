package org.talend.daikon.spring.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class SynchronizedMongoClientProviderTest {

    @Test
    public void shouldNotCloseInCaseOfConcurrentUsage() {
        // given
        final MongoClientProvider mongoClientProvider = mock(MongoClientProvider.class);
        SynchronizedMongoClientProvider provider = new SynchronizedMongoClientProvider(mongoClientProvider);
        final TenantInformationProvider tenantInformationProvider = mock(TenantInformationProvider.class);
        when(tenantInformationProvider.getDatabaseURI()).thenReturn(new MongoClientURI("mongodb://no_host"));
        final MongoClient mongoClient = mock(MongoClient.class);
        when(mongoClientProvider.get(eq(tenantInformationProvider))).thenReturn(mongoClient);

        // when
        provider.get(tenantInformationProvider);
        provider.get(tenantInformationProvider);
        provider.close(tenantInformationProvider);
        provider.close(tenantInformationProvider);

        // then
        verify(mongoClient, times(1)).close();
    }

    @Test
    public void shouldCloseInCaseOfSequentialUsage() {
        // given
        final MongoClientProvider mongoClientProvider = mock(MongoClientProvider.class);
        SynchronizedMongoClientProvider provider = new SynchronizedMongoClientProvider(mongoClientProvider);
        final TenantInformationProvider tenantInformationProvider = mock(TenantInformationProvider.class);
        when(tenantInformationProvider.getDatabaseURI()).thenReturn(new MongoClientURI("mongodb://no_host"));
        final MongoClient mongoClient = mock(MongoClient.class);
        when(mongoClientProvider.get(eq(tenantInformationProvider))).thenReturn(mongoClient);

        // when
        provider.get(tenantInformationProvider);
        provider.close(tenantInformationProvider);
        provider.get(tenantInformationProvider);
        provider.close(tenantInformationProvider);

        // then
        verify(mongoClient, times(2)).close();
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
        final MongoClient mongoClient1 = mock(MongoClient.class);
        final MongoClient mongoClient2 = mock(MongoClient.class);
        when(mongoClientProvider.get(eq(tenantInformationProvider1))).thenReturn(mongoClient1);
        when(mongoClientProvider.get(eq(tenantInformationProvider2))).thenReturn(mongoClient2);

        // when
        provider.get(tenantInformationProvider1);
        provider.get(tenantInformationProvider2);
        provider.close(tenantInformationProvider1);
        provider.close(tenantInformationProvider2);

        // then
        verify(mongoClient1, times(1)).close();
        verify(mongoClient2, times(1)).close();
    }

    @Test
    public void shouldCloseInCaseOfMissingConfiguration() {
        // given
        final MongoClientProvider mongoClientProvider = mock(MongoClientProvider.class);
        SynchronizedMongoClientProvider provider = new SynchronizedMongoClientProvider(mongoClientProvider);
        final TenantInformationProvider tenantInformationProvider = mock(TenantInformationProvider.class);
        when(tenantInformationProvider.getDatabaseURI()).thenThrow(new RuntimeException("On purpose thrown exception"));
        final MongoClient mongoClient = mock(MongoClient.class);
        when(mongoClientProvider.get(eq(tenantInformationProvider))).thenReturn(mongoClient);

        // when
        provider.close(tenantInformationProvider);

        // then
        verify(mongoClient, times(1)).close();
    }

}