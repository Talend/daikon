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
package org.talend.daikon.properties;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.talend.daikon.properties.ValidationResult.Result;

/**
 * Contains and process validation result for every property
 */
public class ValidationResults {

    /**
     * Map containing validation results where key is the property name and the value is ValidationResult for this Property.
     */
    private final Map<String, ValidationResult> validationResults;

    public ValidationResults() {
        this.validationResults = new LinkedHashMap<>();
    }

    /**
     * @return map of all properties warnings (ValidationResult equals Result.Warning and message should be specified)
     */
    public Map<String, ValidationResult> getWarnings() {
        return filterResultsByStatus(Result.WARNING);
    }

    /**
     * @return map of all properties errors (ValidationResult equals Result.Error and error message should be
     * specified)
     */
    public Map<String, ValidationResult> getErrors() {
        return filterResultsByStatus(Result.ERROR);
    }

    private Map<String, ValidationResult> filterResultsByStatus(ValidationResult.Result status) {
        Map<String, ValidationResult> filteredValues = new HashMap<>();
        for (Entry<String, ValidationResult> vr : validationResults.entrySet()) {
            if (status == vr.getValue().getStatus()) {
                filteredValues.put(vr.getKey(), vr.getValue());
            }
        }
        return filteredValues;
    }

    public void addValidationResults(ValidationResults value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        Map<String, ValidationResult> allValidationResults = value.getAll();
        for (Entry<String, ValidationResult> validationResult : allValidationResults.entrySet()) {
            addValidationResult(validationResult.getKey(), validationResult.getValue());
        }
    }

    public void addValidationResult(String propName, ValidationResult value) {
        if (value == null) {
            return;
        }
        ValidationResult containing = validationResults.get(propName);
        if (containing == null || shouldRewrite(containing, value)) {
            validationResults.put(propName, value);
        }
    }

    /**
     * Check whether newer validation result status of processed property is more critical than the containing one. If so, we
     * should update the value.
     */
    private boolean shouldRewrite(ValidationResult containing, ValidationResult newValue) {
        return ((containing.getStatus() == Result.OK && newValue.getStatus() != Result.OK)
                || (containing.getStatus() == Result.WARNING && newValue.getStatus() == Result.ERROR));
    }

    /**
     * @return unmodifiable Map where keys are property names and values are all ValidationResults
     */
    public Map<String, ValidationResult> getAll() {
        return Collections.unmodifiableMap(validationResults);
    }

    /**
     * @return actual ValidationResult.Result depending on available errors and warning
     */
    public ValidationResult.Result calculateValidationStatus() {
        if (!getErrors().isEmpty()) {
            return ValidationResult.Result.ERROR;
        } else if (!getWarnings().isEmpty()) {
            return ValidationResult.Result.WARNING;
        }
        return ValidationResult.Result.OK;
    }

    /**
     * @return empty String when no problems in maps or full errors and warnings message
     */
    private String getGeneralProblemsMessage() {
        StringBuilder message = new StringBuilder();

        addMessages(message, getErrors().values());
        addMessages(message, getWarnings().values());

        return message.toString();
    }

    private void addMessages(StringBuilder sb, Collection<ValidationResult> results) {
        for (ValidationResult vr : results) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(vr.toString());
        }
    }

    /**
     * @return "OK" when there are no problems in maps or {@link #getGeneralProblemsMessage()} when they are present
     */
    @Override
    public String toString() {
        String value = getGeneralProblemsMessage();
        return value.isEmpty() ? "OK" : value;
    }

    public boolean isEmpty() {
        return validationResults.isEmpty();
    }
}
