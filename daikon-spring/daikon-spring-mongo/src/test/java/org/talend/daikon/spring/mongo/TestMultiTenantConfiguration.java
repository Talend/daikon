package org.talend.daikon.spring.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TestMultiTenantConfiguration {

    private static final ThreadLocal<String> dataBaseName = ThreadLocal.withInitial(() -> "default");

    private static final ThreadLocal<String> hostName = ThreadLocal.withInitial(() -> "local");

    private static final Map<String, MongoServer> mongoInstances = new HashMap<>();

    public static void changeTenant(String tenant) {
        dataBaseName.set(tenant);
    }

    public static void changeHost(String host) {
        hostName.set(host);
    }

    public static Map<String, MongoServer> getMongoInstances() {
        return mongoInstances;
    }

    @Bean
    public MongoDbFactory defaultMongoDbFactory() {
        MongoServer server = mongoServer();

        return new SimpleMongoDbFactory(new MongoClient(new ServerAddress(server.getLocalAddress())), "standard");
    }

    @Bean
    public MongoServer mongoServer() {
        return initNewServer();
    }

    private MongoServer initNewServer() {
        // Applications are expected to have one MongoDbFactory available
        MongoServer server = new MongoServer(new MemoryBackend());

        System.out.println("Bind");
        // bind on a random local port
        server.bind();

        return server;
    }

    @Bean
    public MongoTemplate mongoTemplate(final MongoDbFactory factory) {
        // Used in tests
        return new MongoTemplate(factory);
    }

    /**
     * @return A {@link TenantInformationProvider} that gets the database name from {@link #dataBaseName}.
     */
    @Bean
    public TenantInformationProvider tenantProvider() {
        return new TenantInformationProvider() {

            @Override
            public String getDatabaseName() {
                if ("failure".equals(dataBaseName.get())) {
                    throw new RuntimeException("On purpose thrown exception.");
                }
                return dataBaseName.get();
            }

            @Override
            public MongoClientURI getDatabaseURI() {
                String uri = "mongodb://fake_host:27017/" + dataBaseName.get();
                return new MongoClientURI(uri);
            }
        };
    }

    @Bean
    public MongoClientProvider mongoClientProvider() {
        return new MongoClientProvider() {

            @Override
            public void close() {
                for (Map.Entry<String, MongoServer> entry : mongoInstances.entrySet()) {
                    entry.getValue().shutdown();
                }
                mongoInstances.clear();
            }

            @Override
            public MongoClient get(TenantInformationProvider provider) {
                final String name = provider.getDatabaseURI().getURI();
                if (!mongoInstances.containsKey(name)) {
                    System.out.println("Create server " + name);
                    mongoInstances.put(name, initNewServer());
                    System.out.println("Created server " + mongoInstances.get(name).getLocalAddress());
                }
                return new MongoClient(new ServerAddress(mongoInstances.get(name).getLocalAddress()));
            }

            @Override
            public void close(TenantInformationProvider provider) {
                final String uri = provider.getDatabaseURI().getURI();
                final MongoServer server = mongoInstances.get(uri);
                if (server != null) {
                    server.shutdown();
                }
                mongoInstances.remove(uri);
            }
        };
    }

}
