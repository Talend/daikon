package org.talend.daikon.mongo;

import com.mongodb.reactivestreams.client.MongoDatabase;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.talend.daikon.multitenant.context.TenancyContext;
import org.talend.daikon.security.tenant.ReactiveTenancyContextHolder;
import reactor.core.publisher.Mono;

/**
 * Override the ReactiveMongoTemplate to use the ReactiveTenancyContextHolder to create a reactive MongoDatabase.
 */
public class TenancyReactiveMongoTemplate extends ReactiveMongoTemplate {

    /**
     * Use to get the tenant configuration.
     */
    private final ReactiveTenantInformationProvider reactiveTenantInformationProvider;

    /**
     * Use to create a reactive MongoClient (use cache on the current implmentation of this provider).
     */
    private final ReactiveMongoClientProvider reactiveMongoClientProvider;

    public TenancyReactiveMongoTemplate(ReactiveMongoDatabaseFactory factory,
            ReactiveTenantInformationProvider reactiveTenantInformationProvider,
            ReactiveMongoClientProvider reactiveMongoClientProvider) {
        super(factory);
        this.reactiveTenantInformationProvider = reactiveTenantInformationProvider;
        this.reactiveMongoClientProvider = reactiveMongoClientProvider;
    }

    @Override
    protected Mono<MongoDatabase> doGetDatabase() {
        return ReactiveTenancyContextHolder.getContext().map(TenancyContext::getOptionalTenant)
                .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty))
                .flatMap(tenant -> reactiveTenantInformationProvider.getTenantInformation(tenant.getIdentity().toString()))
                .map(tenantInformation -> reactiveMongoClientProvider.get(tenantInformation)
                        .getDatabase(tenantInformation.getDatabaseName()));
    }

}
