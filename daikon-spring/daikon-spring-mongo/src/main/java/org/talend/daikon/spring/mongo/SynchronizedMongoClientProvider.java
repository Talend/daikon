package org.talend.daikon.spring.mongo;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link MongoClientProvider} implementation that provides thread safety around the
 * {@link MongoClientProvider#close(TenantInformationProvider)} method.
 */
public class SynchronizedMongoClientProvider implements MongoClientProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizedMongoClientProvider.class);

    private final MongoClientProvider delegate;

    private final Map<MongoClientURI, AtomicInteger> concurrentOpens = Collections.synchronizedMap(new HashMap<>());

    public SynchronizedMongoClientProvider(MongoClientProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public MongoClient get(TenantInformationProvider tenantInformationProvider) {
        final MongoClientURI databaseURI = tenantInformationProvider.getDatabaseURI();
        concurrentOpens.putIfAbsent(databaseURI, new AtomicInteger(0));
        concurrentOpens.get(databaseURI).incrementAndGet();

        return delegate.get(tenantInformationProvider);
    }

    @Override
    public synchronized void close(TenantInformationProvider tenantInformationProvider) {
        final MongoClientURI databaseURI = tenantInformationProvider.getDatabaseURI();
        final int openCount = concurrentOpens.getOrDefault(databaseURI, new AtomicInteger(0)).decrementAndGet();
        if(openCount <= 0) {
            delegate.close(tenantInformationProvider);
        } else {
            LOGGER.trace("Not closing mongo clients ({} remain in use for database '{}')", openCount, databaseURI);
        }
    }
}
