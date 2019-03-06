// ============================================================================
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

import static org.talend.daikon.spring.metrics.io.Metered.Type.OUT;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.output.ProxyOutputStream;

public class MeteredOutputStream extends ProxyOutputStream implements Metered {

    private final OutputStream delegate;

    private long volume;

    public MeteredOutputStream(OutputStream delegate) {
        super(delegate);
        this.delegate = delegate;
    }

    @Override
    public void write(int b) throws IOException {
        try {
            delegate.write(b);
        } finally {
            volume++;
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        try {
            delegate.write(b);
        } finally {
            volume += b.length;
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        try {
            delegate.write(b, off, len);
        } finally {
            volume += len;
        }
    }

    @Override
    public long getVolume() {
        return volume;
    }

    @Override
    public Type getType() {
        return OUT;
    }
}
