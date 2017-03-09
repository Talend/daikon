/*
 * ============================================================================
 *
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 *
 * ============================================================================
 */

package org.talend.daikon.version.api;

import java.util.List;

import org.talend.daikon.version.events.FieldModification;

/**
 * Contract of a provider of "diff" between two instances of the same class.
 */
@FunctionalInterface
public interface ModificationsProvider {

    /**
     * Returns the list of modifications that occurred on instance v1
     * to have become instance v2.
     *
     * @param v1 instance that represents the initial state
     * @param v2 instance that represents the final state
     * @param <T> the type of v1 and v2.
     * @return the list of modifications.
     */
    <T> List<FieldModification> getModifications(T v1, T v2);

}
