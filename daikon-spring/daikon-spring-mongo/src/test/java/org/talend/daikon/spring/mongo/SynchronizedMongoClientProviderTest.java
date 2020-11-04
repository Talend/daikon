package org.talend.daikon.spring.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SynchronizedMongoClientProviderTest {

    @Test
    public void shouldNotCloseInCaseOfConcurrentUsage() {
        // given
        final MongoClientProvider mongoClientProvider = mock(MongoClientProvider.class);
        SynchronizedMongoClientProvider provider = new SynchronizedMongoClientProvider(mongoClientProvider);
        final TenantInformationProvider tenantInformationProvider = mock(TenantInformationProvider.class);
        when(tenantInformationProvider.getDatabaseURI()).thenReturn(new ConnectionString("mongodb://no_host"));
        final MongoClient mongoClient = mock(MongoClient.class);
        when(mongoClientProvider.get(eq(tenantInformationProvider))).thenReturn(mongoClient);
        doAnswer(invocation -> {
            try {
                mongoClient.close();
            } catch (Exception e) {
                // Ignored
            }
            return null;
        }).when(mongoClientProvider).close(eq(tenantInformationProvider));

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
        when(tenantInformationProvider.getDatabaseURI()).thenReturn(new ConnectionString("mongodb://no_host"));
        final MongoClient mongoClient = mock(MongoClient.class);
        when(mongoClientProvider.get(eq(tenantInformationProvider))).thenReturn(mongoClient);
        doAnswer(invocation -> {
            try {
                mongoClient.close();
            } catch (Exception e) {
                // Ignored
            }
            return null;
        }).when(mongoClientProvider).close(eq(tenantInformationProvider));

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
        when(tenantInformationProvider1.getDatabaseURI()).thenReturn(new ConnectionString("mongodb://no_host_1"));
        when(tenantInformationProvider2.getDatabaseURI()).thenReturn(new ConnectionString("mongodb://no_host_2"));
        final MongoClient mongoClient1 = mock(MongoClient.class);
        final MongoClient mongoClient2 = mock(MongoClient.class);
        when(mongoClientProvider.get(eq(tenantInformationProvider1))).thenReturn(mongoClient1);
        when(mongoClientProvider.get(eq(tenantInformationProvider2))).thenReturn(mongoClient2);
        doAnswer(invocation -> {
            try {
                mongoClient1.close();
            } catch (Exception e) {
                // Ignored
            }
            return null;
        }).when(mongoClientProvider).close(eq(tenantInformationProvider1));
        doAnswer(invocation -> {
            try {
                mongoClient2.close();
            } catch (Exception e) {
                // Ignored
            }
            return null;
        }).when(mongoClientProvider).close(eq(tenantInformationProvider2));

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
        doAnswer(invocation -> {
            try {
                mongoClient.close();
            } catch (Exception e) {
                // Ignored
            }
            return null;
        }).when(mongoClientProvider).close(eq(tenantInformationProvider));

        // when
        provider.close(tenantInformationProvider);

        // then
        verify(mongoClient, times(1)).close();
    }

}