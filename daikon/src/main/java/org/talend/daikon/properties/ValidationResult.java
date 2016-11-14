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

import org.talend.daikon.exception.TalendRuntimeException;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Contains the result of the validation of a components property.
 * <p/>
 * This is to be returned from the {@code validate} methods in {@link Properties}. The ValidationResult with the status
 * {@link ValidationResult.Result#OK} will be shown to the user if a message is set.
 * </p>
 * The ValidationResult with the {@link ValidationResult.Result#ERROR} must have a message set to explain the error.
 * </p>
 */
public class ValidationResult {

    public enum Result {
        OK,
        WARNING,
        ERROR
    }

    public static ValidationResult OK = new ValidationResult().setStatus(Result.OK);

    @JsonIgnore
    public Result status = Result.OK;

    public int number;

    public ValidationResult() {
    }

    /**
     * Use the TalendRuntimeException to construct a Validation message. By default the status is set to Error but this
     * may be changed by the user after creation.
     * 
     * @param tre exception used to construct the message
     */
    public ValidationResult(TalendRuntimeException tre) {
        status = Result.ERROR;
        message = tre.getMessage();
    }

    public Result getStatus() {
        return status;
    }

    public ValidationResult setStatus(Result status) {
        this.status = status;
        return this;
    }

    /**
     * @return the message previously set or null if none. If a message is returned the client will display it.
     */
    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getMessage() {
        return message;
    }

    /**
     * Set the text message related to this validation result. This method must be called with a non null value when the
     * status is {@link Result#ERROR}.
     */
    public ValidationResult setMessage(String message) {
        this.message = message;
        return this;
    }

    @JsonIgnore
    public String message;

    @Override
    public String toString() {
        return getStatus() + " " + getMessage();
    }

}
