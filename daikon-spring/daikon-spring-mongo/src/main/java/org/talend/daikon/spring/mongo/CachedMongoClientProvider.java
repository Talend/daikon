package org.talend.daikon.spring.mongo;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * An implementation of {@link MongoClientProvider} that has automatic client clean up after a time period.
 */
public class CachedMongoClientProvider implements MongoClientProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedMongoClientProvider.class);

    private final LoadingCache<MongoClientURI, MongoClient> cache;

    public CachedMongoClientProvider(int duration, TimeUnit unit) {
        final RemovalListener<MongoClientURI, MongoClient> removalListener = notification -> {
            final MongoClient client = notification.getValue();
            try {
                LOGGER.debug("Closing '{}' due to '{}'.", client, notification.getCause());
                client.close();
            } catch (Exception e) {
                LOGGER.error("Unable to properly close '{}'.", client, e);
            }
        };
        final CacheLoader<MongoClientURI, MongoClient> factory = new CacheLoader<MongoClientURI, MongoClient>() {

            public MongoClient load(MongoClientURI uri) throws Exception {
                try {
                    LOGGER.debug("Adding new mongo client for '{}'.", uri);
                    return new MongoClient(uri);
                } catch (UnknownHostException e) {
                    throw new InvalidDataAccessResourceUsageException("Unable to retrieve host information.", e);
                }
            }
        };

        cache = CacheBuilder.newBuilder() //
                .concurrencyLevel(100) //
                .maximumSize(100) //
                .expireAfterAccess(duration, unit) //
                .removalListener(removalListener)
                .build(factory);
    }

    @Override
    public MongoClient get(TenantInformationProvider provider) {
        try {
            return cache.get(provider.getDatabaseURI());
        } catch (Exception e) {
            throw new InvalidDataAccessResourceUsageException("Unable to retrieve client.", e);
        }
    }

    @Override
    public void close() throws IOException {
        for (MongoClient client : cache.asMap().values()) {
            client.close();
        }
    }
}
