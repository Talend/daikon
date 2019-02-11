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

import java.io.Serializable;
import java.util.Collection;

/**
 * An error code uniquely describes an error condition (for reference, documentation, UI, etc).
 */
public interface ErrorCode extends Serializable {

    /**
     * @return the product used for the error message... Hum... TDP ? ;-)
     */
    String getProduct();

    /**
     * @return the group this message belongs to (API, DATASET, PREPARATION...)
     */
    String getGroup();

    /**
     * @return the http status to return.
     */
    int getHttpStatus();

    /**
     * @return the expected context entries if any.
     */
    Collection<String> getExpectedContextEntries();

    /**
     * @return the full code for this message.
     */
    String getCode();

}
