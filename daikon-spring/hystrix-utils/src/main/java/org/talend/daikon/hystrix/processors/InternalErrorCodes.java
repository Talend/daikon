package org.talend.daikon.hystrix.processors;

import java.util.Collection;
import java.util.List;

import org.talend.daikon.exception.error.ErrorCode;

public enum InternalErrorCodes implements ErrorCode {

    UNEXPECTED_EXCEPTION;

    /** The http status to use. */
    private int httpStatus;

    /** Expected entries to be in the context. */
    private List<String> expectedContextEntries;

    @Override
    public String getProduct() {
        return "DAIKON";
    }

    @Override
    public String getGroup() {
        return "HYSTRIX";
    }

    @Override
    public int getHttpStatus() {
        return httpStatus;
    }

    @Override
    public Collection<String> getExpectedContextEntries() {
        return this.expectedContextEntries;
    }

    @Override
    public String getCode() {
        return this.toString();
    }
}
