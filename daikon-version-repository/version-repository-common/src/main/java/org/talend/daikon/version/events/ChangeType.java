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

package org.talend.daikon.version.events;

/**
 * Type of change that can occur on a resource
 * or a property of a resource.
 */
public enum ChangeType {

    /**
     * The resource or its property was created
     */
    CREATE,
    /**
     * The resource or its property was updated
     */
    UPDATE,
    /**
     * The resource or its property was deleted
     */
    DELETE

}
