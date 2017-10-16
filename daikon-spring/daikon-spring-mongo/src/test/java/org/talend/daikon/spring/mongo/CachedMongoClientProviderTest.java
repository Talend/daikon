package org.talend.daikon.spring.mongo;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class CachedMongoClientProviderTest {

    private static final TenantInformationProvider TENANT1 = getTenantInformationProvider("Tenant1");

    private static final TenantInformationProvider TENANT2 = getTenantInformationProvider("Tenant2");

    private final CachedMongoClientProvider cachedMongoClientProvider = new CachedMongoClientProvider(1, TimeUnit.SECONDS);

    private static Fongo fongo;

    private static TenantInformationProvider getTenantInformationProvider(final String tenant) {
        return new TenantInformationProvider() {

            @Override
            public String getDatabaseName() {
                return tenant;
            }

            @Override
            public MongoClientURI getDatabaseURI() {
                return new MongoClientURI("mongodb://fake_host:27017/" + tenant);
            }
        };
    }

    @BeforeClass
    public static void setUp() throws Exception {
        fongo = new Fongo("CachedMongoClientProviderTest");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        fongo.getMongo().close();
    }

    @Test
    public void shouldNotEvictInstanceBeforeTimeout() throws Exception {
        // When
        final MongoClient client1 = cachedMongoClientProvider.get(TENANT1);
        final MongoClient client2 = cachedMongoClientProvider.get(TENANT1);

        // Then
        assertTrue(client1 == client2);
    }

    @Test
    public void shouldEvictInstanceAfterTimeout() throws Exception {
        // When
        final MongoClient client1 = cachedMongoClientProvider.get(TENANT1);
        TimeUnit.SECONDS.sleep(2);
        final MongoClient client2 = cachedMongoClientProvider.get(TENANT1);

        // Then
        assertTrue(client1 != client2);
    }

    @Test
    public void shouldCreateClientForTenants() throws Exception {
        // When
        final MongoClient client1 = cachedMongoClientProvider.get(TENANT1);
        final MongoClient client2 = cachedMongoClientProvider.get(TENANT2);

        // Then
        assertTrue(client1 != client2);
    }
}