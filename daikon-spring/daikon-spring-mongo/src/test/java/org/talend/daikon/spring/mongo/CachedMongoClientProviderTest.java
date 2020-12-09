package org.talend.daikon.spring.mongo;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class CachedMongoClientProviderTest {

    private static final TenantInformationProvider TENANT1 = getTenantInformationProvider("Tenant1");

    private static final TenantInformationProvider TENANT2 = getTenantInformationProvider("Tenant2");

    private static MongoServer server;

    private final CachedMongoClientProvider cachedMongoClientProvider = new CachedMongoClientProvider(1, TimeUnit.SECONDS);

    private static InetSocketAddress serverAddress;

    private static TenantInformationProvider getTenantInformationProvider(final String tenant) {
        return () -> {
            ConnectionString connectionString = new ConnectionString(
                    "mongodb://" + serverAddress.getHostName() + ":" + serverAddress.getPort() + "/" + tenant);
            return TenantInformation.builder()
                    .clientSettings(MongoClientSettings.builder().applyConnectionString(connectionString).build())
                    .databaseName(tenant).build();
        };
    }

    @BeforeClass
    public static void setUp() {
        server = new MongoServer(new MemoryBackend());

        // bind on a random local port
        serverAddress = server.bind();
    }

    @AfterClass
    public static void tearDown() {
        server.shutdown();
    }

    @Test
    public void shouldNotEvictInstanceBeforeTimeout() {
        // When
        final MongoClient client1 = cachedMongoClientProvider.get(TENANT1);
        final MongoClient client2 = cachedMongoClientProvider.get(TENANT1);

        // Then
        assertSame(client1, client2);
    }

    @Test
    public void shouldEvictInstanceAfterTimeout() throws Exception {
        // When
        final MongoClient client1 = cachedMongoClientProvider.get(TENANT1);
        TimeUnit.SECONDS.sleep(2);
        final MongoClient client2 = cachedMongoClientProvider.get(TENANT1);

        // Then
        assertNotSame(client1, client2);
    }

    @Test
    public void shouldCreateClientForTenants() {
        // When
        final MongoClient client1 = cachedMongoClientProvider.get(TENANT1);
        final MongoClient client2 = cachedMongoClientProvider.get(TENANT2);

        // Then
        assertNotSame(client1, client2);
    }

    @Test
    public void shouldCloseClient() {
        // Given
        final TenantInformationProvider tenantInformationProvider = getTenantInformationProvider("Tenant1");

        // When
        final MongoClient client1 = cachedMongoClientProvider.get(tenantInformationProvider);
        cachedMongoClientProvider.close(tenantInformationProvider);
        final MongoClient client2 = cachedMongoClientProvider.get(tenantInformationProvider);

        // Then
        assertNotSame(client1, client2);
    }
}
