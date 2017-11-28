package org.talend.daikon.spring.mongo;

import java.io.IOException;

import com.mongodb.MongoClient;

/**
 * A {@link MongoClientProvider} implementation that provides thread safety around the
 * {@link MongoClientProvider#close()} method.
 */
public class SynchronizedMongoClientProvider implements MongoClientProvider {

    private final MongoClientProvider delegate;

    public SynchronizedMongoClientProvider(MongoClientProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public MongoClient get(TenantInformationProvider tenantInformationProvider) {
        return delegate.get(tenantInformationProvider);
    }

    @Override
    public synchronized void close() throws IOException {
        delegate.close();
    }
}
