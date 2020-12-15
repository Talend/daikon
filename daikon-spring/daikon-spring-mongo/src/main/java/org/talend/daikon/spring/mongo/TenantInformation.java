package org.talend.daikon.spring.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public final class TenantInformation {

    /**
     * Describes how to connect to db: host, port, replicaSet, credentials, etc.
     */
    private final MongoClientSettings clientSettings;

    /**
     * The database name to use to execute operation (write or read). Database name must <b>NOT</b> be empty.
     */
    private final String databaseName;

    @EqualsAndHashCode.Include
    private final String cacheKey;

    @Builder
    public TenantInformation(String databaseName, MongoClientSettings clientSettings) {
        Objects.requireNonNull(databaseName, "Database name must be present");

        this.clientSettings = clientSettings;
        this.databaseName = databaseName;
        this.cacheKey = getKey(clientSettings);
    }

    private String getKey(MongoClientSettings clientSettings) {
        String hosts = clientSettings.getClusterSettings().getHosts().stream().map(h -> h.getHost() + ":" + h.getPort())
                .collect(Collectors.joining(","));
        String replicaSet = clientSettings.getClusterSettings().getRequiredReplicaSetName();
        String userName = Optional.ofNullable(clientSettings.getCredential()).map(MongoCredential::getUserName).orElse("");

        return String.join(";", Arrays.asList(hosts, replicaSet, userName, databaseName));
    }

}
