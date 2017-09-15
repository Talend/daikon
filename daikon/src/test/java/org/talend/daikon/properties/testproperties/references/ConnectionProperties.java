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
package org.talend.daikon.properties.testproperties.references;

import org.talend.daikon.properties.PresentationItem;
import org.talend.daikon.properties.PropertiesImpl;
import org.talend.daikon.properties.presentation.Form;

public class ConnectionProperties extends PropertiesImpl {

    public PresentationItem presItem = new PresentationItem("presItem");

    public ConnectionProperties(String name) {
        super(name);
    }

    @Override
    public void setupLayout() {
        super.setupLayout();
        Form form = Form.create(this, Form.MAIN);
        form.addRow(presItem);
    }

}