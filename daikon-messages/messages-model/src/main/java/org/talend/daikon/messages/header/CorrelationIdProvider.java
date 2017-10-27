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
package org.talend.daikon.messages.header;

/**
 * Accesses the current request identifier and provides the current correlation id.
 *
 * This implementation should generate a new correlation id if the current request has no correlation id.
 */
public interface CorrelationIdProvider {

    /**
     * @return the current correlation id or generates a new one.
     */
    String getCorrelationId();

}
