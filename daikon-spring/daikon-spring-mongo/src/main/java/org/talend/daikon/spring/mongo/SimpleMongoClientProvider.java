package org.talend.daikon.spring.mongo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

/**
 * A very simple implementation of {@link MongoClientProvider}.
 * This provider does not allow selected eviction of cached
 * instances.
 *
 * This class should be instantiate only once.
 */
public class SimpleMongoClientProvider implements MongoClientProvider {

    // ensure the map is synchronized
    private final Map<ConnectionString, MongoClient> clients = Collections.synchronizedMap(new HashMap<>(100));

    protected MongoClient createMongoClient(ConnectionString uri) {
        try {
            return MongoClients.create(uri);
        } catch (Exception e) {
            // 3.x client throws UnknownHostException, keep catch block for compatibility with 3.x version
            throw new InvalidDataAccessResourceUsageException("Unable to retrieve host information.", e);
        }
    }

    @Override
    public MongoClient get(TenantInformationProvider provider) {
        final ConnectionString databaseURI = provider.getDatabaseURI();
        clients.computeIfAbsent(databaseURI, this::createMongoClient);
        return clients.get(databaseURI);
    }

    @Override
    public void close(TenantInformationProvider provider) {
        final ConnectionString uri = provider.getDatabaseURI();
        final MongoClient mongoClient = clients.get(uri);
        if (mongoClient != null) {
            mongoClient.close();
        }
        clients.remove(uri);
    }

    @Override
    public void close() {
        for (Map.Entry<ConnectionString, MongoClient> entry : clients.entrySet()) {
            entry.getValue().close();
        }
        clients.clear();
    }
}
