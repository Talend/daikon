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
 * Similar to InheritableThreadLocalTenancyContextHolderStrategy
 *
 * @author agonzalez
 */
final class InheritableThreadLocalTenancyContextHolderStrategy implements TenancyContextHolderStrategy {

    private static final ThreadLocal<TenancyContext> CONTEXT_HOLDER = new InheritableThreadLocal<TenancyContext>();

    public void clearContext() {
        CONTEXT_HOLDER.remove();
    }

    public TenancyContext getContext() {
        TenancyContext ctx = CONTEXT_HOLDER.get();

        if (ctx == null) {
            ctx = createEmptyContext();
            CONTEXT_HOLDER.set(ctx);
        }

        return ctx;
    }

    public void setContext(TenancyContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Only non-null TenancyContext instances are permitted");
        }
        CONTEXT_HOLDER.set(context);
    }

    public TenancyContext createEmptyContext() {
        return new DefaultTenancyContext();
    }
}
