package org.talend.daikon.spring.mongo;

import com.mongodb.MongoClientURI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class TestMultiTenantConfiguration {

    private static final ThreadLocal<String> dataBaseName = ThreadLocal.withInitial(() -> "default");

    private static final ThreadLocal<String> hostName = ThreadLocal.withInitial(() -> "local");

    private static final Map<String, Fongo> fongoInstances = new HashMap<>();

    public static void changeTenant(String tenant) {
        dataBaseName.set(tenant);
    }

    public static void changeHost(String host) {
        hostName.set(host);
    }

    public static Map<String, Fongo> getFongoInstances() {
        return fongoInstances;
    }

    @Bean
    public MongoDbFactory defaultMongoDbFactory() {
        // Applications are expected to have one MongoDbFactory available
        return new SimpleMongoDbFactory(new Fongo("Standard Mongo").getMongo(), "standard");
    }

    @Bean
    public MongoTemplate mongoTemplate(final MongoDbFactory factory) {
        // Used in tests
        return new MongoTemplate(factory);
    }

    // A fake mongo for tests
    @Bean
    public Fongo fongo() {
        return new Fongo("MongoDB");
    }

    /**
     * @return A {@link TenantInformationProvider} that gets the database name from {@link #dataBaseName}.
     */
    @Bean
    public TenantInformationProvider tenantProvider() {
        return new TenantInformationProvider() {
            @Override
            public String getDatabaseName() {
                if("failure".equals(dataBaseName.get())) {
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
            public MongoClient get(TenantInformationProvider provider) {
                final String name = provider.getDatabaseURI().getURI();
                fongoInstances.computeIfAbsent(name, Fongo::new);
                return fongoInstances.get(name).getMongo();
            }

            @Override
            public void close(TenantInformationProvider provider) {
                for (Map.Entry<String, Fongo> entry : fongoInstances.entrySet()) {
                    entry.getValue().getMongo().close();
                }
                fongoInstances.clear();
            }
        };
    }

}
