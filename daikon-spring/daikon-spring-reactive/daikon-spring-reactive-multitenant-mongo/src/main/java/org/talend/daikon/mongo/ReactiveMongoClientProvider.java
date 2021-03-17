package org.talend.daikon.mongo;

import com.mongodb.reactivestreams.client.MongoClient;

import java.io.Closeable;

public interface ReactiveMongoClientProvider extends Closeable {

    MongoClient get(TenantInformation tenantInformation);

    void close(TenantInformation tenantInformation);
}
