package org.talend.daikon.spring.mongo;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

import java.util.concurrent.TimeUnit;

/**
 * An implementation of {@link MongoClientProvider} that has automatic client clean up after a time period.
 */
public class CachedMongoClientProvider implements MongoClientProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedMongoClientProvider.class);

    private final LoadingCache<MongoClientURI, MongoClient> cache;

    /**
     * Creates an instance with <code>concurrencyLevel = 100</code> and <code>maximumSize = 100</code>.
     * 
     * @param duration The time after which a cache entry is removed if cache entry wasn't accessed for this time. For
     * example, 10 minutes means any cached mongo client not used within the last 10 minutes will be removed.
     * @param unit The time unit for <code>duration</code>.
     */
    public CachedMongoClientProvider(int duration, TimeUnit unit) {
        this(duration, unit, 100, 100);
    }

    /**
     *
     * @param duration The time after which a cache entry is removed if cache entry wasn't accessed for this time. For
     * example, 10 minutes means any cached mongo client not used within the last 10 minutes will be removed.
     * @param unit The time unit for <code>duration</code>.
     * @param concurrencyLevel Max number of concurrent access to a cached mongo client (see
     * {@link CacheBuilder#concurrencyLevel}).
     * @param maximumSize Max size for the cache (see {@link CacheBuilder#maximumSize}).
     */
    public CachedMongoClientProvider(int duration, TimeUnit unit, int concurrencyLevel, int maximumSize) {
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
                } catch (Exception e) {
                    // 3.x client throws UnknownHostException, keep catch block for compatibility with 3.x version
                    throw new InvalidDataAccessResourceUsageException("Unable to retrieve host information.", e);
                }
            }
        };

        cache = CacheBuilder.newBuilder() //
                .concurrencyLevel(concurrencyLevel) //
                .maximumSize(maximumSize) //
                .expireAfterAccess(duration, unit) //
                .removalListener(removalListener).build(factory);
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
    public void close(TenantInformationProvider provider) {
        try {
            final MongoClientURI uri = provider.getDatabaseURI();
            final MongoClient mongoClient = cache.get(uri);
            try {
                mongoClient.close();
            } finally {
                cache.asMap().remove(uri);
            }
        } catch (Exception e) {
            throw new InvalidDataAccessResourceUsageException("Unable to close client.", e);
        }
    }

    @Override
    public void close() {
        for (MongoClient client : cache.asMap().values()) {
            client.close();
        }
    }
}
