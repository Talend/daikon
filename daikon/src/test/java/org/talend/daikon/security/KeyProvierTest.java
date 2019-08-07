// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.security;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class KeyProvierTest {

	@Test
	public void testGetKey() {
		assertEquals("99ZwBDt1L9yMX2ApJx fnv94o99OeHbCGuIHTy22 V9O6cZ2i374fVjdV76VX9g49DG1r3n90hT5c1", KeyProvider.getInstance().getKeyValue(KeyProvider.SYSTEM_ENCRYPTION_KEY));
		assertEquals("Il faudrait trouver une passphrase plus originale que celle-ci!", KeyProvider.getInstance().getKeyValue(KeyProvider.PROPERTY_ENCRYPTION_KEY));
	}
}
