package org.talend.daikon.spring.mongo;

import com.mongodb.ClientSessionOptions;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.util.Assert;

/**
 * A {@link MongoDatabaseFactory} that allows external code to choose which MongoDB database should be accessed.
 *
 * @see TenantInformationProvider
 */
class MultiTenancyMongoDbFactory implements MongoDatabaseFactory, DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiTenancyMongoDbFactory.class);

    private final MongoDatabaseFactory delegate;

    private final TenantInformationProvider tenantProvider;

    private final MongoClientProvider mongoClientProvider;

    MultiTenancyMongoDbFactory(final MongoDatabaseFactory delegate, //
            final TenantInformationProvider tenantProvider, //
            final MongoClientProvider mongoClientProvider) {
        this.delegate = delegate;
        this.tenantProvider = tenantProvider;
        this.mongoClientProvider = mongoClientProvider;
    }

    @Override
    public MongoDatabase getMongoDatabase() throws DataAccessException {
        final String databaseName = getDatabaseName();
        LOGGER.debug("Using '{}' as Mongo database.", databaseName);
        // Get MongoDB database using tenant information
        return mongoClientProvider.get(tenantProvider).getDatabase(databaseName);
    }

    private String getDatabaseName() {
        // Multi tenancy database name selection
        final String databaseName;
        try {
            databaseName = tenantProvider.getTenantInformation().getDatabaseName();
        } catch (Exception e) {
            throw new InvalidDataAccessResourceUsageException("Unable to retrieve database name.", e);
        }
        Assert.hasText(databaseName, "Database name must not be empty.");
        return databaseName;
    }

    @Override
    public MongoDatabase getMongoDatabase(String databaseName) throws DataAccessException {
        // There's no reason the database name parameter should be considered here (information belongs to the tenant).
        return getMongoDatabase();
    }

    @Override
    public PersistenceExceptionTranslator getExceptionTranslator() {
        return delegate.getExceptionTranslator();
    }

    @Override
    public ClientSession getSession(ClientSessionOptions options) {
        return null;
    }

    @Override
    public MongoDatabaseFactory withSession(ClientSession session) {
        return this;
    }

    @Override
    public void destroy() {
        mongoClientProvider.close(tenantProvider);
    }

}
