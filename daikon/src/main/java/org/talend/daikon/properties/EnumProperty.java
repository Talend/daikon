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
package org.talend.daikon.properties;

/**
 * Specifc property for enums
 */
public class EnumProperty<T extends Enum<?>> extends Property<T> implements AnyProperty {

    public EnumProperty(Class<T> zeEnumType, String name) {
        super(zeEnumType, name, null);
        // set the possible values accoording with all the enum types.
        Enum<?>[] enumConstants = zeEnumType.getEnumConstants();
        this.setPossibleValues(enumConstants);

    }

    // this is used for deserialization
    EnumProperty(String zeEnumTypeStr, String name) {
        super(zeEnumTypeStr, name);
    }

}
