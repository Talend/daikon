package org.talend.daikon.spring.mongo;

import com.mongodb.ConnectionString;

/**
 * <p>
 * Implementation of this interface returns the current MongoDB database settings that should be used. Interface is very simple on
 * purpose and allow any kind of implementation (e.g. always same name, use name based on user name, ask an external service...).
 * </p>
 * <p>
 * Performance note: the result of all methods are <b>not</b> cached. Consider using cache in implementation if:
 * <ul>
 * <li>Implementation code is slow.</li>
 * <li>Limit calls to external components.</li>
 * </ul>
 * </p>
 */
public interface TenantInformationProvider {

    /**
     * @return The database name to use to execute operation (write or read). Database name must <b>NOT</b> be empty.
     */
    String getDatabaseName();

    /**
     * @return A {@link ConnectionString} object that describes where the mongo db host is.
     */
    ConnectionString getDatabaseURI();
}
