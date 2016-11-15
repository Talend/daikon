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
package org.talend.daikon.exception.error;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

/**
 * Common error code for a backend service application that also implents a REST API.
 */
public enum CommonErrorCodes implements ErrorCode {
    UNEXPECTED_EXCEPTION(HttpServletResponse.SC_INTERNAL_SERVER_ERROR), // 500
    MISSING_I18N_TRANSLATOR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "key", "baseName"), //$NON-NLS-1$ //$NON-NLS-2$
    /** uses <b>definitionName<b> context key */
    UNREGISTERED_DEFINITION(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "definitionName"),
    UNABLE_TO_PARSE_JSON(HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
    UNABLE_TO_WRITE_JSON(HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
    UNABLE_TO_SERIALIZE_TO_JSON(HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
    UNABLE_TO_READ_CONTENT(HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
    UNABLE_TO_PARSE_REQUEST(HttpServletResponse.SC_BAD_REQUEST), // 400,
    /** uses <b>argument</b> and <b>value</b> context keys (in this order) */
    UNEXPECTED_ARGUMENT(HttpServletResponse.SC_BAD_REQUEST, "argument", "value"); // 400,
    // e.g
    // IllegalArgumentException

    private DefaultErrorCode errorCodeDelegate;

    /**
     * default constructor.
     * 
     * @param httpStatus the http status to use.
     */
    CommonErrorCodes(int httpStatus) {
        this.errorCodeDelegate = new DefaultErrorCode(httpStatus);
    }

    /**
     * default constructor.
     *
     * @param httpStatus the http status to use.
     */
    CommonErrorCodes(int httpStatus, String... contextEntries) {
        this.errorCodeDelegate = new DefaultErrorCode(httpStatus, contextEntries);
    }

    /**
     * @return the product. Default value is Talend.
     */
    @Override
    public String getProduct() {
        return errorCodeDelegate.getProduct();
    }

    /**
     * @return the group. Default Value is ALL
     */
    @Override
    public String getGroup() {
        return errorCodeDelegate.getGroup();
    }

    /**
     * @return the http status.
     */
    @Override
    public int getHttpStatus() {
        return errorCodeDelegate.getHttpStatus();
    }

    /**
     * @return the expected context entries.
     */
    @Override
    public Collection<String> getExpectedContextEntries() {
        return errorCodeDelegate.getExpectedContextEntries();
    }

    @Override
    public String getCode() {
        return toString();
    }
}
