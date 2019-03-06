// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.daikon.spring.metrics.io;

import static org.talend.daikon.spring.metrics.io.Metered.Type.IN;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.input.ProxyInputStream;

public class MeteredInputStream extends ProxyInputStream implements Metered {

    private final InputStream delegate;

    private long volume;

    public MeteredInputStream(InputStream delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    private void updateVolume(int read) {
        if (read > 0) {
            volume += read;
        }
    }

    @Override
    public int read() throws IOException {
        try {
            return delegate.read();
        } finally {
            volume++;
        }
    }

    @Override
    public int read(byte[] bytes) throws IOException {
        int read = delegate.read(bytes);
        updateVolume(read);
        return read;
    }

    @Override
    public int read(byte[] bytes, int i, int i1) throws IOException {
        int read = delegate.read(bytes, i, i1);
        updateVolume(read);
        return read;
    }

    @Override
    public long getVolume() {
        return volume;
    }

    @Override
    public Type getType() {
        return IN;
    }
}
