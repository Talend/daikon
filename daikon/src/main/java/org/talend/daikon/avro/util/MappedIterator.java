package org.talend.daikon.avro.util;

import java.util.Iterator;

import org.talend.daikon.java8.Function;

/**
 * Provides an {@link Iterator} that wraps another, transparently applying a {@link Function} to all of its values.
 * 
 * @param <InT> The (hidden) type of the values in the wrapped iterator.
 * @param <OutT> The (visible) type of the values in this iterator.
 */
public class MappedIterator<InT, OutT> implements Iterator<OutT> {

    private final Iterator<InT> mWrapped;

    private final Function<InT, OutT> mInFunction;

    MappedIterator(Iterator<InT> wrapped, Function<InT, OutT> inFunction) {
        this.mWrapped = wrapped;
        this.mInFunction = inFunction;
    }

    @Override
    public boolean hasNext() {
        return mWrapped.hasNext();
    }

    @Override
    public OutT next() {
        return mInFunction.apply(mWrapped.next());
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
