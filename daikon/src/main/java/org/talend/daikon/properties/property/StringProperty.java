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
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.talend.daikon.NamedThing;
import org.talend.daikon.crypto.Encryption;
import org.talend.daikon.exception.ExceptionContext;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.daikon.security.CryptoHelper;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This property has an extra method to handle possible values of type NamedThing
 */
public class StringProperty extends Property<String> {

    private static final long serialVersionUID = -7464790471464008148L;

    private static Logger log = Logger.getLogger(StringProperty.class.getCanonicalName());

    private List<NamedThing> possibleValues2;

    private Encryption encryption;

    public StringProperty(String name) {
        super(String.class, name);
    }

    StringProperty(String type, String name) {
        super(type, name);
    }

    /**
     * Adds all {@link NamedThing#getName()} as possible values for this string property and use the
     * {@link NamedThing#getDisplayName()} as display name for the associated possible value
     */
    public Property<String> setPossibleNamedThingValues(List<NamedThing> possibleValues) {
        this.possibleValues2 = possibleValues;
        // stores the real possible values which are the NameThing names
        ArrayList<String> realPossibleValues = new ArrayList<>();
        for (NamedThing nt : possibleValues2) {
            realPossibleValues.add(nt.getName());
        }
        super.setPossibleValues(realPossibleValues);
        return this;
    }

    @JsonIgnore
    // to avoid swagger failure because of the 2 similar following methods.
    public Property<String> setPossibleValues(List<?> possibleValues) {
        // check is the type is a NamedThing
        if (possibleValues != null && !possibleValues.isEmpty() && possibleValues.get(0) instanceof NamedThing) {
            setPossibleNamedThingValues((List<NamedThing>) possibleValues);
        } else {
            super.setPossibleValues(possibleValues);
        }
        return this;
    }

    /**
     * This will look if {@link NamedThing} where used as possible values and use associated
     * {@link NamedThing#getDisplayName()}. If there are possible Values as NamedThing and the value is not found the an
     * exception is thrown. If no NamedThing was set as possible values it will delegate to
     * {@link Property#getPossibleValuesDisplayName(Object)}
     * 
     * @return the associated {@link NamedThing#getDisplayName()} if found or the default i18n value from super.
     * @throws TalendRuntimeException is the possible value does not belong to the list of possible values.
     */
    @Override
    public String getPossibleValuesDisplayName(Object possibleValue) {
        String possibleValueDisplayName = possibleValue == null ? "null" : possibleValue.toString();
        // first check that the possibleValue is part of the possible values
        if (!isAPossibleValue(possibleValue)) {
            throw new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_ARGUMENT,
                    ExceptionContext.build().put("argument", "possibleValues").put("value", possibleValue));
        }
        // look for the named thing named possibleValue and return it's display name if not null
        if (possibleValues2 != null && !possibleValues2.isEmpty()) {
            for (NamedThing nt : possibleValues2) {
                if (possibleValue != null && possibleValue.equals(nt.getName())) {
                    possibleValueDisplayName = (nt.getDisplayName() != null ? nt.getDisplayName() : nt.getName());
                    break;
                } // else keep looking
            }
        } else {// delegate to super.
            possibleValueDisplayName = super.getPossibleValuesDisplayName(possibleValue);
        }
        return possibleValueDisplayName;
    }

    @Override
    public void encryptStoredValue(boolean encrypt) {
        if (isFlag(Property.Flags.ENCRYPT)) {
            String value = (String) getStoredValue();
            if (encrypt) {
                setStoredValue(encryptFunc().apply(value));
            } else {
                setStoredValue(decryptFunc().apply(value));
            }
        }
    }

    @Override
    public void setEncryption(Encryption e) {
        this.encryption = e;
    }

    private Function<String,String> encryptFunc(){
        if (this.encryption != null) {
            return (src) -> {
                if (src == null) {
                    return null;
                }
                try {
                    return encryption.encrypt(src);
                } catch (Exception e) {
                    // for backward compatibility
                    log.log(Level.FINE, e.getMessage(), e);
                }
                return null;
            };
        }
        return (src) -> new CryptoHelper(CryptoHelper.PASSPHRASE).encrypt(src);
    }

    private Function<String, String> decryptFunc() {
        if (this.encryption != null) {
            return (src) -> {
                // backward compatibility
                if (src == null) {
                    return null;
                }
                if (src.isEmpty()) {
                    return "";
                }
                try {
                    return encryption.decrypt(src);
                } catch (Exception e) {
                    // for backward compatibility
                    log.log(Level.FINE, e.getMessage(), e);
                }
                return null;
            };
        }
        return (src) -> new CryptoHelper(CryptoHelper.PASSPHRASE).decrypt(src);
    }

}
