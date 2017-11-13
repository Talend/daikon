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
