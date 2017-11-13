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
 * A strategy for storing tenancy context information against an execution.
 * 
 * @author Clint Morgan (Tasktop Technologies Inc.)
 */
public interface TenancyContextHolderStrategy {

    /**
     * Clears the current context.
     */
    void clearContext();

    /**
     * Obtains the current context.
     * 
     * @return a context (never <code>null</code> - create a default implementation if necessary)
     */
    TenancyContext getContext();

    /**
     * Sets the current context.
     * 
     * @param context
     * to the new argument (should never be <code>null</code>, although implementations must check if
     * <code>null</code> has been passed and throw an <code>IllegalArgumentException</code> in such cases)
     */
    void setContext(TenancyContext context);

    /**
     * Creates a new, empty context implementation, for use when creating a new context for the first time.
     * 
     * @return the empty context.
     */
    TenancyContext createEmptyContext();

}
