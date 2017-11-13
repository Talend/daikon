// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.multitenant.web;

import javax.servlet.http.HttpServletRequest;

/**
 * A {@link TenantIdentificationStrategy strategy} which looks for tenant identity from a given request header.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 * 
 */
public class HeaderTenantIdentificationStrategy implements TenantIdentificationStrategy {

    private String headerName;

    @Override
    public Object identifyTenant(HttpServletRequest request) {
        return request.getHeader(headerName);
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

}
