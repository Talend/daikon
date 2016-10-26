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
package org.talend.daikon;

import org.talend.daikon.i18n.TranslatableImpl;

/**
 * Simple implementation of {@link NamedThing}
 */
public class SimpleNamedThing extends TranslatableImpl implements NamedThing {

    protected String name;

    protected String displayName;

    protected String title;

    public SimpleNamedThing() {
        this(null, null, null);
    }

    public SimpleNamedThing(String name) {
        this(name, null, null);
    }

    public SimpleNamedThing(String name, String displayName) {
        this(name, displayName, null);
    }

    public SimpleNamedThing(String name, String displayName, String title) {
        this.name = name;
        this.displayName = displayName;
        this.title = title;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return (getName() != null ? getName() : "") + "/" + (getDisplayName() != null ? getDisplayName() : "") + "/"
                + (getTitle() != null ? getTitle() : "");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SimpleNamedThing other = (SimpleNamedThing) obj;
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

}
