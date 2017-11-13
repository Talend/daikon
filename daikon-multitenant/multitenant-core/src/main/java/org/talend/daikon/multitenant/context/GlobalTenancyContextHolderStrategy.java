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
package org.talend.daikon.multitenant.context;

/**
 * Similar to GlobalSecurityContextHolderStrategy
 */
public class GlobalTenancyContextHolderStrategy implements TenancyContextHolderStrategy {

    private static TenancyContext contextHolder;

    public void clearContext() {
        contextHolder = null;
    }

    public TenancyContext getContext() {
        if (contextHolder == null) {
            contextHolder = new DefaultTenancyContext();
        }

        return contextHolder;
    }

    public void setContext(TenancyContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Only non-null TenancyContext instances are permitted");
        }
        contextHolder = context;
    }

    public TenancyContext createEmptyContext() {
        return new DefaultTenancyContext();
    }
}
