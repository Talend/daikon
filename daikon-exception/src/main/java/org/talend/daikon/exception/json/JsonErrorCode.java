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
package org.talend.daikon.exception.json;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;
import org.talend.daikon.exception.error.ErrorCode;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class used to ease ErrorCode json manipulation.
 * It can be used to deserialise json string encoded with {@link TalendRuntimeException#writeTo(java.io.Writer)}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonErrorCode implements ErrorCode {

    private static final long serialVersionUID = 2244078854888080512L;

    @JsonProperty("code")
    private String code;

    /** The error code context. */
    @JsonProperty("context")
    private Map<String, Object> context = Collections.emptyMap();

    private int httpStatus;

    /**
     * @return the error code product.
     */
    @Override
    public String getProduct() {
        return org.apache.commons.lang3.StringUtils.substringBefore(code, "_"); //$NON-NLS-1$
    }

    /**
     * @param product the product to set.
     */
    public void setProduct(String product) {
        // Nothing to do.
    }

    /**
     * @return the error code group.
     */
    @Override
    public String getGroup() {
        return StringUtils.substringBefore(StringUtils.substringAfter(code, "_"), "_"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @param group the error code group to set.
     */
    public void setGroup(String group) {
        // Nothing to do.
    }

    /**
     * @return the error code... code ! :-)
     */
    @Override
    public String getCode() {
        if (code == null) {
            return CommonErrorCodes.UNEXPECTED_EXCEPTION.getCode();
        }
        return code.substring((getProduct() + '_' + getGroup()).length() + 1);
    }

    /**
     * @param code the error code to set.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the error code http status.
     */
    @Override
    public int getHttpStatus() {
        return this.httpStatus;
    }

    /**
     * @param httpStatus the error code http status to set.
     */
    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    /**
     * @return the error code context entries.
     */
    public Map<String, Object> getContext() {
        return this.context;
    }

    /**
     * @param context the error code context entries.
     */
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    @Override
    public Collection<String> getExpectedContextEntries() {
        return context.keySet();
    }
}
