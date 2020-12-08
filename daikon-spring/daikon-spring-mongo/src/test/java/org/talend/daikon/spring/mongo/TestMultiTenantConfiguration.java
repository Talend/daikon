package org.talend.daikon.spring.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TestMultiTenantConfiguration {

    private static final ThreadLocal<String> dataBaseName = ThreadLocal.withInitial(() -> "default");

    private static final ThreadLocal<String> hostName = ThreadLocal.withInitial(() -> "local");

    private static final Map<ClientCacheEntry, MongoServer> mongoInstances = new HashMap<>();

    public static void changeTenant(String tenant) {
        dataBaseName.set(tenant);
    }

    public static void changeHost(String host) {
        hostName.set(host);
    }

    public static Map<ClientCacheEntry, MongoServer> getMongoInstances() {
        return mongoInstances;
    }

    @Bean
    public MongoDatabaseFactory defaultMongoDbFactory() {
        MongoServer server = mongoServer();
        return new SimpleMongoClientDatabaseFactory(
                new ConnectionString("mongodb:/" + server.getLocalAddress().toString() + "/default"));
    }

    @Bean
    public MongoServer mongoServer() {
        return initNewServer();
    }

    private MongoServer initNewServer() {
        // Applications are expected to have one MongoDbFactory available
        MongoServer server = new MongoServer(new MemoryBackend());

        // bind on a random local port
        server.bind();

        return server;
    }

    @Bean
    public MongoTemplate mongoTemplate(final MongoDatabaseFactory factory) {
        // Used in tests
        return new MongoTemplate(factory);
    }

    /**
     * @return A {@link TenantInformationProvider} that gets the database name from {@link #dataBaseName}.
     */
    @Bean
    public TenantInformationProvider tenantProvider(MongoServer mongoServer) {
        return new TenantInformationProvider() {

            @Override
            public String getDatabaseName() {
                if ("failure".equals(dataBaseName.get())) {
                    throw new RuntimeException("On purpose thrown exception.");
                }
                return dataBaseName.get();
            }

            @Override
            public ClientCacheEntry getCacheEntry() {
                String uri = "mongodb://127.0.0.1:" + mongoServer.getLocalAddress().getPort() + "/" + dataBaseName.get();
                return ClientCacheEntry.builder()
                        .clientSettings(MongoClientSettings.builder().applyConnectionString(new ConnectionString(uri)).build())
                        .cacheKey("127.0.0.1/" + dataBaseName.get()).build();
            }
        };
    }

    @Bean
    public MongoClientProvider mongoClientProvider() {
        return new MongoClientProvider() {

            @Override
            public void close() {
                for (Map.Entry<ClientCacheEntry, MongoServer> entry : mongoInstances.entrySet()) {
                    entry.getValue().shutdown();
                }
                mongoInstances.clear();
            }

            @Override
            public MongoClient get(TenantInformationProvider provider) {
                final ClientCacheEntry cacheEntry = provider.getCacheEntry();
                if (!mongoInstances.containsKey(cacheEntry)) {
                    mongoInstances.put(cacheEntry, initNewServer());
                }
                return MongoClients.create(provider.getCacheEntry().getClientSettings());
            }

            @Override
            public void close(TenantInformationProvider provider) {
                final ClientCacheEntry cacheEntry = provider.getCacheEntry();
                final MongoServer server = mongoInstances.get(cacheEntry);
                if (server != null) {
                    server.shutdown();
                }
                mongoInstances.remove(cacheEntry);
            }
        };
    }

}
