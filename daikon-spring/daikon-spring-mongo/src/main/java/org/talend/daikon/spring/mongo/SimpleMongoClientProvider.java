package org.talend.daikon.spring.mongo;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.InvalidDataAccessResourceUsageException;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * A very simple implementation of {@link MongoClientProvider}.
 * This provider does not allow selected eviction of cached
 * instances.
 *
 * This class should be instantiate only once.
 */
public class SimpleMongoClientProvider implements MongoClientProvider {

    // ensure the map is synchronized
    private final Map<MongoClientURI, MongoClient> clients = Collections.synchronizedMap(new HashMap<>(100));

    protected MongoClient createMongoClient(MongoClientURI uri) {
        try {
            return new MongoClient(uri);
        } catch (Exception e) {
            // 3.x client throws UnknownHostException, keep catch block for compatibility with 3.x version
            throw new InvalidDataAccessResourceUsageException("Unable to retrieve host information.", e);
        }
    }

    @Override
    public MongoClient get(TenantInformationProvider provider) {
        final MongoClientURI databaseURI = provider.getDatabaseURI();
        clients.computeIfAbsent(databaseURI, this::createMongoClient);
        return clients.get(databaseURI);
    }

    @Override
    public void close() throws IOException {
        for (Map.Entry<MongoClientURI, MongoClient> entry : clients.entrySet()) {
            entry.getValue().close();
        }
        clients.clear();
    }
}
