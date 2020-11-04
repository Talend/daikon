package org.talend.daikon.spring.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A {@link MongoClientProvider} implementation that provides thread safety around the
 * {@link MongoClientProvider#close(TenantInformationProvider)} method.
 */
public class SynchronizedMongoClientProvider implements MongoClientProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizedMongoClientProvider.class);

    private final MongoClientProvider delegate;

    private final Map<ConnectionString, AtomicInteger> concurrentOpens = Collections.synchronizedMap(new HashMap<>());

    public SynchronizedMongoClientProvider(MongoClientProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public MongoClient get(TenantInformationProvider tenantInformationProvider) {
        final ConnectionString databaseURI = tenantInformationProvider.getDatabaseURI();
        concurrentOpens.putIfAbsent(databaseURI, new AtomicInteger(0));
        concurrentOpens.get(databaseURI).incrementAndGet();

        return delegate.get(tenantInformationProvider);
    }

    @Override
    public synchronized void close(TenantInformationProvider tenantInformationProvider) {
        ConnectionString databaseURI = null;
        int openCount = 0;
        try {
            databaseURI = tenantInformationProvider.getDatabaseURI();
            openCount = concurrentOpens.getOrDefault(databaseURI, new AtomicInteger(0)).decrementAndGet();
        } catch (Exception e) {
            LOGGER.debug("Unable to obtain database URI (configuration might be missing for tenant).", e);
        }
        if (openCount <= 0) {
            try {
                delegate.close(tenantInformationProvider);
            } finally {
                concurrentOpens.remove(databaseURI);
            }
        } else {
            LOGGER.trace("Not closing mongo clients ({} remain in use for database '{}')", openCount,
                    databaseURI == null ? "N/A" : databaseURI);
        }
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
