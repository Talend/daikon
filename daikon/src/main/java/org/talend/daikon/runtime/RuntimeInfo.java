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

import java.net.URL;
import java.util.List;

public interface RuntimeInfo {

    /**
     * list all the depencencies required for this component to be executed at runtime
     * 
     * @param componentName name of the component to get the dependencies of.
     * @return a set of maven uri following the pax-maven uri scheme @see
     * <a href="https://ops4j1.jira.com/wiki/display/paxurl/Mvn+Protocol">https://ops4j1.jira.com/wiki/display/paxurl/
     * Mvn+Protocol</a>
     */
    List<URL> getMavenUrlDependencies();

    String getRuntimeClassName();
}
