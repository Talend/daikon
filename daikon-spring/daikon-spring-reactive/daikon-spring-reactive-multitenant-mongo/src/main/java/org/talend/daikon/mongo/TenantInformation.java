package org.talend.daikon.mongo;

import com.mongodb.MongoClientSettings;
import lombok.Getter;

import java.util.Objects;

@Getter
public class TenantInformation {

    private final String databaseName;

    private final MongoClientSettings settings;

    public TenantInformation(String databaseName, MongoClientSettings settings) {
        Objects.requireNonNull(databaseName, "Database name must be present");

        this.settings = settings;
        this.databaseName = databaseName;
    }

}
