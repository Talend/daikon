/*
 * Copyright (C) 2006-2016 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package org.talend.daikon.serialize.jsonschema;

/**
 * List possible callbacks on a property. As it is destined only to be serialized, in lower case, even if it would be preferable
 * to have an
 * enum serializer on jackson and/or spring to serialize in lower case.
 */
public enum PropertyTrigger {
    VALIDATE,
    BEFORE_ACTIVE,
    BEFORE_PRESENT,
    AFTER,
    /** Custom tag. Represent the widget(normally a button) which can open a new form */
    SHOW_FORM
}
