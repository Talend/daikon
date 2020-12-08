package org.talend.daikon.spring.mongo;

import com.mongodb.MongoClientSettings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Builder
@AllArgsConstructor
@Getter
public class ClientCacheEntry {

    private final MongoClientSettings clientSettings;

    private final String cacheKey;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ClientCacheEntry that = (ClientCacheEntry) o;
        return cacheKey.equals(that.cacheKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cacheKey);
    }
}
