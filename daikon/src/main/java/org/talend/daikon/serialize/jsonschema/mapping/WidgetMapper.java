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
package org.talend.daikon.serialize.jsonschema.mapping;

import org.talend.daikon.serialize.jsonschema.UiSchemaConstants;

/**
 * Mapper for simple widgets.
 */
public class WidgetMapper extends Mapper {

    public WidgetMapper(String uiType) {
        super(UiSchemaConstants.TAG_WIDGET, uiType);
    }

}