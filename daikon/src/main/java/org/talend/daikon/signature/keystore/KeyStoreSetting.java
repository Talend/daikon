// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.signature.keystore;

import java.net.URL;

public class KeyStoreSetting {

    public static final String JKS = "JKS";

    private String storeType = JKS;

    private URL storeUrl;

    private char[] storePassword;

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public URL getStoreUrl() {
        return storeUrl;
    }

    public void setStoreUrl(URL storeUrl) {
        this.storeUrl = storeUrl;
    }

    public char[] getStorePassword() {
        return storePassword;
    }

    public void setStorePassword(char[] storePassword) {
        this.storePassword = storePassword;
    }
}
