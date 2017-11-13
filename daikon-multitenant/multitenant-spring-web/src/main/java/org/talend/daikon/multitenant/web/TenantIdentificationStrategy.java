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
 * A strategy for identifying tenants from a {@link HttpServletRequest}.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 */
public interface TenantIdentificationStrategy {

    Object identifyTenant(HttpServletRequest request);
}
