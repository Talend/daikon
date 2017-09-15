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

/**
 * Mutable version of {@link ValidationResult}
 */
public class ValidationResultMutable extends ValidationResult {

    /**
     * Construct an immutable {@link ValidationResultMutable} with a {@link #OK} status and a <code>null</code> message
     */
    public ValidationResultMutable() {
        super();
    }

    /**
     * construct an immutable {@link ValidationResultMutable} with a status and a <code>null</code> message
     * 
     * @param status {@link Result}
     */
    public ValidationResultMutable(Result status) {
        super(status);
    }

    /**
     * Construct an immutable {@link ValidationResultMutable} with a {@link Result} status and a message
     * 
     * @param status {@link Result}
     * @param message validation message
     */
    public ValidationResultMutable(Result status, String message) {
        super(status, message);
    }

    /**
     * Construct a mutable copie of validation result passed in parameter
     * 
     * @param vr {@link ValidationResult}
     */
    public ValidationResultMutable(ValidationResult vr) {
        if (vr == null) {
            return;
        }

        this.status = vr.getStatus();
        this.message = vr.getMessage();
    }

    /**
     * Use the TalendRuntimeException to construct a Validation message. By default the status is set to Error, and the message to
     * Exception message.
     * if you need to change the status for this, please use {@link ValidationResultMutable}
     * 
     * @param tre exception used to construct the message
     */
    public ValidationResultMutable(TalendRuntimeException tre) {
        super(tre);
    }

    public ValidationResultMutable setStatus(Result status) {
        this.status = status;
        return this;
    }

    /**
     * Set the text message related to this validation result. This method must be called with a non null value when the
     * status is {@link Result#ERROR}.
     */
    public ValidationResultMutable setMessage(String message) {
        this.message = message;
        return this;
    }

}
