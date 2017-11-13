// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.multitenant.async;

/**
 * Allows to interact with {@link ContextAwareCallable} to propagate any context specific information from a thread to another
 */
public interface ContextPropagator {

    /**
     * Called before switch thread
     */
    void captureContext();

    /**
     * Called on the new thread
     */
    void setupContext();

    /**
     * Called at the end of the execution.
     */
    void restoreContext();
}
