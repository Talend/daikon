// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.runtime;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import org.ops4j.pax.url.mvn.Handler;
import org.ops4j.pax.url.mvn.ServiceConstants;

public class MvnHandlerConfigForSpring {

    static {// install the mvn protocol handler if not already installed.
        try {
            new URL("mvn:foo/bar");
        } catch (MalformedURLException e) {
            // handles mvn local repository
            String mvnLocalRepo = System.getProperty("maven.repo.local");
            if (mvnLocalRepo != null) {
                System.setProperty("org.ops4j.pax.url.mvn.localRepository", mvnLocalRepo);
            }
            URL.setURLStreamHandlerFactory(new URLStreamHandlerFactory() {

                @Override
                public URLStreamHandler createURLStreamHandler(String protocol) {
                    if (ServiceConstants.PROTOCOL.equals(protocol)) {
                        return new Handler();
                    } else {
                        return null;
                    }
                }
            });
        }
    }
}
