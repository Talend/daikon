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
package org.talend.daikon.properties.property;

import java.util.ArrayList;
import java.util.List;

import org.talend.daikon.NamedThing;
import org.talend.daikon.exception.ExceptionContext;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;

/**
 * This property has an extra method to handle possible values of type NamedThing
 */
public class StringProperty extends Property<String> {

    private List<NamedThing> possibleValues2;

    public StringProperty(String name) {
        super(String.class, name);
    }

    /**
     * this is package protected because this constructor should only be used when copying a Property at runtime, so it
     * does not need to be typed.
     */
    StringProperty(String type, String name) {
        super(type, name);
    }

    /**
     * This will add all {@link NamedThing#getName()} as possible values for this string property
     * and use the {@link NamedThing#getDisplayName()} as display name for the associated possible value
     */
    public Property<String> setPossibleNamedThingValues(List<NamedThing> possibleValues) {
        this.possibleValues2 = possibleValues;
        // stores the real possible values which are the NameThing names
        ArrayList<String> realPossibleValues = new ArrayList<>();
        for (NamedThing nt : possibleValues2) {
            realPossibleValues.add(nt.getName());
        }
        setPossibleValues(realPossibleValues);
        return this;
    }

    /**
     * This will look if {@link NameThing} where used as possible values and use associated {@link NamedThing#getDisplayName()}.
     * If there are possible Values as NamedThing and the value is not found the an exception is thrown. If no NamedThing was set
     * as possible values it will delegate to {@link Property#getPossibleValuesDisplayName(Object)}
     * 
     * @return the associated {@link NamedThing#getDisplayName()} if found or the default i18n
     *         value from super.
     * @throws TalendRuntimeException is the possible value does not belong to the list of possible values.
     */
    @Override
    public String getPossibleValuesDisplayName(Object possibleValue) {
        String displayName = possibleValue == null ? "null" : possibleValue.toString();
        // first check that the possibleValue is part of the possible values
        if (!isAPossibleValue(possibleValue)) {
            throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_ARGUMENT,
                    ExceptionContext.build().put("argument", "possibleValues").put("value", possibleValue));
        }
        // look for the named thing named possibleValue and return it's display name if not null
        if (possibleValues2 != null && !possibleValues2.isEmpty()) {
            for (NamedThing nt : possibleValues2) {
                if (possibleValue != null && possibleValue.equals(nt.getName())) {
                    displayName = nt.getDisplayName();
                    break;
                } // else keep looking
            }
        } else {// delegate to super.
            displayName = super.getPossibleValuesDisplayName(possibleValue);
        }
        return displayName;
    }

}
