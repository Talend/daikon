package org.talend.daikon.spring.mongo;

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
     * @return A {@link TenantInformation} object that describes database name and where the mongo db host is.
     */
    TenantInformation getTenantInformation();
}
