package org.talend.daikon.spring.mongo;

import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.util.Assert;

import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * A {@link MongoDbFactory} that allows external code to choose which MongoDB database should be accessed.
 *
 * @see TenantInformationProvider
 */
class MultiTenancyMongoDbFactory implements MongoDbFactory, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiTenancyMongoDbFactory.class);

    private final MongoDbFactory delegate;

    private final TenantInformationProvider tenantProvider;

    private final MongoClientProvider mongoClientProvider;

    MultiTenancyMongoDbFactory(final MongoDbFactory delegate, //
            final TenantInformationProvider tenantProvider, //
            final MongoClientProvider mongoClientProvider) {
        this.delegate = delegate;
        this.tenantProvider = tenantProvider;
        this.mongoClientProvider = mongoClientProvider;
    }

    @Override
    public MongoDatabase getDb() {
        final String databaseName = getDatabaseName();
        LOGGER.debug("Using '{}' as Mongo database.", databaseName);
        // Get MongoDB database using tenant information
        return mongoClientProvider.get(tenantProvider).getDatabase(databaseName);
    }

    private String getDatabaseName() {
        // Multi tenancy database name selection
        final String databaseName;
        try {
            databaseName = tenantProvider.getDatabaseName();
        } catch (Exception e) {
            throw new InvalidDataAccessResourceUsageException("Unable to retrieve database name.", e);
        }
        Assert.hasText(databaseName, "Database name must not be empty.");
        return databaseName;
    }

    @Override
    public MongoDatabase getDb(String dbName) {
        // There's no reason the database name parameter should be considered here (information belongs to the tenant).
        return getDb();
    }

    @Override
    public PersistenceExceptionTranslator getExceptionTranslator() {
        return delegate.getExceptionTranslator();
    }

    @Override
    public DB getLegacyDb() {
        final String databaseName = getDatabaseName();
        LOGGER.debug("Using '{}' as Mongo database.", databaseName);
        return mongoClientProvider.get(tenantProvider).getDB(databaseName);
    }

    @Override
    public ClientSession getSession(ClientSessionOptions options) {
        return null;
    }

    @Override
    public MongoDbFactory withSession(ClientSession session) {
        return this;
    }

    @Override
    public void destroy() {
        mongoClientProvider.close(tenantProvider);
    }

}
