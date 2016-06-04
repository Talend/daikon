// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.serialize;

/**
 * Returns information about a migration that was done when converting an object from its serialized form into an
 * object. If the serialized form was created from a previous version of the implementation, the {@link #isMigrated()}
 * property will be set, which can be used to trigger actions on the part of the client.
 */
public interface MigrationInformation {

    /**
     * Returns true if this object was created from a serialized form used by a previous version of the implementation
     * and was modified because of that.
     * 
     * This can be used by the caller to notify the user that the object has been migrated and also, if desired, it can
     * be re-saved in its current serialized form (which would be different than the serialized form provided
     * initially).
     * 
     * @return true if the object was migrated
     */
    public boolean isMigrated();

}
