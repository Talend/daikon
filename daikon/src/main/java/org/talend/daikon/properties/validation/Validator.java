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
package org.talend.daikon.properties.validation;

import org.talend.daikon.properties.ValidationResult;

/**
 * Interface representing validators for single property field. Validator can be set to instance of {@link Property} class, and it
 * will be used to validate value set to this Property object. Generic should be of the same class as Property generic type.
 */
public interface Validator<T> {

    /**
     * Method to validate Property value. This method should validate Property value and return {@link ValidationResult}.
     * 
     * @param value set to the Property field
     * @return ValidationResult
     */
    public ValidationResult validate(T value);

}
