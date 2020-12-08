package org.talend.daikon.spring.mongo;

import com.mongodb.MongoClientSettings;
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

    private final Map<MongoClientSettings, AtomicInteger> concurrentOpens = Collections.synchronizedMap(new HashMap<>());

    public SynchronizedMongoClientProvider(MongoClientProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public MongoClient get(TenantInformationProvider tenantInformationProvider) {
        final MongoClientSettings clientSettings = tenantInformationProvider.getClientSettings();
        concurrentOpens.putIfAbsent(clientSettings, new AtomicInteger(0));
        concurrentOpens.get(clientSettings).incrementAndGet();

        return delegate.get(tenantInformationProvider);
    }

    @Override
    public synchronized void close(TenantInformationProvider tenantInformationProvider) {
        MongoClientSettings clientSettings = null;
        int openCount = 0;
        try {
            clientSettings = tenantInformationProvider.getClientSettings();
            openCount = concurrentOpens.getOrDefault(clientSettings, new AtomicInteger(0)).decrementAndGet();
        } catch (Exception e) {
            LOGGER.debug("Unable to obtain database URI (configuration might be missing for tenant).", e);
        }
        if (openCount <= 0) {
            try {
                delegate.close(tenantInformationProvider);
            } finally {
                concurrentOpens.remove(clientSettings);
            }
        } else {
            LOGGER.trace("Not closing mongo clients ({} remain in use for database '{}')", openCount,
                    clientSettings == null ? "N/A" : clientSettings);
        }
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }
}
