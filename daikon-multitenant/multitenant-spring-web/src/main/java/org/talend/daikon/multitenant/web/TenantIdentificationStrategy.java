package org.talend.daikon.multitenant.web;

import javax.servlet.http.HttpServletRequest;

/**
 * A strategy for identifying tenants from a {@link HttpServletRequest}.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 */
public interface TenantIdentificationStrategy {

    Object identifyTenant(HttpServletRequest request);
}
