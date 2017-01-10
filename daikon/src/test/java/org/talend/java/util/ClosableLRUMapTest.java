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
package org.talend.java.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class ClosableLRUMapTest {

    private class ClosableItem implements AutoCloseable {

        private boolean isClosed = false;

        @Override
        public void close() throws IOException {
            isClosed = true;
        }

        public boolean isClosed() {
            return isClosed;
        }
    }

    @Test
    public void testInsert() {
        ClosableItem closable1 = new ClosableItem();
        ClosableItem closable2 = new ClosableItem();
        ClosableItem closable3 = new ClosableItem();
        ClosableItem closable4 = new ClosableItem();
        ClosableItem closable5 = new ClosableItem();
        ClosableLRUMap<String, ClosableItem> map = new ClosableLRUMap<>(1, 3);
        map.put("closable1", closable1);
        assertFalse(closable1.isClosed);
        map.put("closable2", closable2);
        assertFalse(closable1.isClosed);
        assertFalse(closable2.isClosed);
        map.put("closable3", closable3);
        assertFalse(closable1.isClosed);
        assertFalse(closable2.isClosed);
        assertFalse(closable3.isClosed);

        // add to many element => close the oldest
        map.put("closable4", closable4);
        assertTrue(closable1.isClosed);
        assertFalse(closable2.isClosed);
        assertFalse(closable3.isClosed);
        assertFalse(closable4.isClosed);

        // add an existing element => no change
        map.put("closable4", closable4);
        assertTrue(closable1.isClosed);
        assertFalse(closable2.isClosed);
        assertFalse(closable3.isClosed);
        assertFalse(closable4.isClosed);

        // add to many element => close the oldest
        map.put("closable5", closable5);
        assertTrue(closable1.isClosed);
        assertTrue(closable2.isClosed);
        assertFalse(closable3.isClosed);
        assertFalse(closable4.isClosed);
        assertFalse(closable5.isClosed);
    }

    @Test
    public void testDelete() {
        ClosableItem closable1 = new ClosableItem();
        ClosableLRUMap<String, ClosableItem> map = new ClosableLRUMap<>(1, 3);
        map.put("closable1", closable1);
        assertFalse(closable1.isClosed);
        map.remove("closable1");
        assertTrue(closable1.isClosed);
    }

    @Test
    public void testDelete2() {
        ClosableItem closable1 = new ClosableItem();
        ClosableLRUMap<String, ClosableItem> map = new ClosableLRUMap<>(1, 3);
        map.put("closable1", closable1);
        assertFalse(closable1.isClosed);
        map.remove("closable1", closable1);
        assertTrue(closable1.isClosed);
    }
}
