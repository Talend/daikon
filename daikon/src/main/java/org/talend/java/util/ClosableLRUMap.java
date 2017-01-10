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

import java.util.LinkedHashMap;
import java.util.Map;

import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.exception.error.CommonErrorCodes;

public class ClosableLRUMap<K, V> extends LinkedHashMap<K, V> {

    protected final int _maxEntries;

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        boolean removeOldest = size() > _maxEntries;
        if (removeOldest) {
            if (eldest.getValue() instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) eldest.getValue()).close();
                } catch (Exception e) {
                    new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
                }
            }
        }
        return removeOldest;
    }

    public ClosableLRUMap(int initialEntries, int maxEntries) {
        super(initialEntries, 0.8f, true);
        _maxEntries = maxEntries;
    }

    @Override
    public V remove(Object key) {
        V value = get(key);
        if (value instanceof AutoCloseable) {
            try {
                ((AutoCloseable) value).close();
            } catch (Exception e) {
                new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
            }
        }
        return super.remove(key);
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (value instanceof AutoCloseable) {
            try {
                ((AutoCloseable) value).close();
            } catch (Exception e) {
                new TalendRuntimeException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
            }
        }
        return super.remove(key, value);
    }

}