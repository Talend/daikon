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
package org.talend.daikon.properties.property;

import org.talend.daikon.properties.AnyPropertyVisitor;
import org.talend.daikon.properties.Properties;

/**
 * Provides a default Properties visitor implementation for visiting only Property typed values
 */
public abstract class PropertyVisitor implements AnyPropertyVisitor {

    @Override
    public void visit(Properties properties, Properties parent) {
        // nothing to done here
    }
}
