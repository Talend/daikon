package org.talend.daikon.mongo;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

@RunWith(MockitoJUnitRunner.class)
public class CachedMongoClientProviderTest {

    @Test
    public void contextLoads() {
    }

    ReactiveMongoClientProvider mongoClientProvider = new CachedMongoClientProvider(1, TimeUnit.SECONDS);

    @Mock
    TenantInformation tenant1;

    @Mock
    TenantInformation tenant2;

    @Test
    public void shouldNotEvictInstanceBeforeTimeout() {
        // given
        when(tenant1.getSettings()).thenReturn(MongoClientSettings.builder().build());
        // When
        final MongoClient client1 = mongoClientProvider.get(tenant1);
        final MongoClient client2 = mongoClientProvider.get(tenant1);

        // Then
        assertSame(client1, client2);
    }

    @Test
    public void shouldEvictInstanceAfterTimeout() throws Exception {
        // given
        when(tenant1.getSettings()).thenReturn(MongoClientSettings.builder().build());
        // When
        final MongoClient client1 = mongoClientProvider.get(tenant1);
        TimeUnit.SECONDS.sleep(2);
        final MongoClient client2 = mongoClientProvider.get(tenant1);

        // Then
        assertNotSame(client1, client2);
    }

    @Test
    public void shouldCreateClientForTenants() {
        // given
        when(tenant1.getSettings()).thenReturn(MongoClientSettings.builder().build());
        when(tenant2.getSettings()).thenReturn(MongoClientSettings.builder().build());
        // When
        final MongoClient client1 = mongoClientProvider.get(tenant1);
        final MongoClient client2 = mongoClientProvider.get(tenant2);

        // Then
        assertNotSame(client1, client2);
    }

    @Test
    public void shouldCloseClient() {
        // Given
        when(tenant1.getSettings()).thenReturn(MongoClientSettings.builder().build());

        // When
        final MongoClient client1 = mongoClientProvider.get(tenant1);
        mongoClientProvider.close(tenant1);
        final MongoClient client2 = mongoClientProvider.get(tenant1);

        // Then
        assertNotSame(client1, client2);
    }

}