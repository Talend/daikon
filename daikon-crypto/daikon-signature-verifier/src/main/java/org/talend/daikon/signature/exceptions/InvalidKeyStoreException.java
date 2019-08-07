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
package org.talend.daikon.signature.exceptions;

public class InvalidKeyStoreException extends VerifyException {

    private static final long serialVersionUID = 1L;

    public InvalidKeyStoreException(String message) {
        super(message);
    }

    public InvalidKeyStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
