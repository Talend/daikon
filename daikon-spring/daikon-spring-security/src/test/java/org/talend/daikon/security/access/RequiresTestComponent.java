// ============================================================================
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.daikon.security.access;

import org.springframework.stereotype.Component;

@Component
public class RequiresTestComponent {

    @RequiresAuthority(authority = { "TestComponentExec", "Test 0" }, value = { "Test 1", "Test 2" })
    public String authoritiesValuesPriority() {
        return "secret string";
    }

    @RequiresAuthority(authority = "TestComponentExec", value = { "Test 1", "Test 2" })
    public String authorityValuesPriority() {
        return "secret string";
    }

    @RequiresAuthority(authority = "", value = { "TestComponentExec", "Test" })
    public String emptyAuthorityValuesPriority() {
        return "secret string";
    }

    @RequiresAuthority({})
    public String emptyValuesPriority() {
        return "secret string";
    }

    @RequiresAuthority({ "TestComponentExec", "Test" })
    public String valuesPriority() {
        return "secret string";
    }

    @RequiresAuthority(authority = "TestComponentExec", value = "Test")
    public String authorityValuePriority() {
        return "secret string";
    }

    @RequiresAuthority(authority = "", value = "TestComponentExec")
    public String emptyAuthorityValuePriority() {
        return "secret string";
    }

    @RequiresAuthority("TestComponentExec")
    public String execute() {
        return "secret string";
    }

    @RequiresAuthority(authority = "TestComponentExec", onDeny = AccessDeniedDefaults.EmptyString.class)
    public String executeWithFallback() {
        return "secret string";
    }

    public String getDetails() {
        return "My details";
    }
}
