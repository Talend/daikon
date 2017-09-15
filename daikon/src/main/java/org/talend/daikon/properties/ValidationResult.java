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
 * This class is immutable, you can use {@link ValidationResultMutable} for a mutable version
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

    public static final ValidationResult OK = new ValidationResult(Result.OK);

    @JsonIgnore
    protected Result status = Result.OK;

    @JsonIgnore
    protected String message;

    public int number;

    /**
     * Construct an immutable {@link ValidationResult} with a {@link #OK} status and a <code>null</code> message
     */
    public ValidationResult() {
    }

    /**
     * construct an immutable {@link ValidationResult} with a status and a <code>null</code> message
     * 
     * @param status {@link Result}
     */
    public ValidationResult(Result status) {
        this.status = status;
    }

    /**
     * Construct an immutable {@link ValidationResult} with a {@link Result} status and a message
     * 
     * @param status {@link Result}
     * @param message validation message
     */
    public ValidationResult(Result status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * Use the TalendRuntimeException to construct a Validation message. By default the status is set to Error, and the message to
     * Exception message.
     * if you need to change the status for this, please use {@link ValidationResultMutable}
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

    /**
     * @return the message previously set or null if none. If a message is returned the client will display it.
     */
    @com.fasterxml.jackson.annotation.JsonIgnore
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return getStatus() + " " + getMessage();
    }

}
