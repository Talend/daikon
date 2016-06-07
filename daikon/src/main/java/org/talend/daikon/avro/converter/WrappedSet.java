package org.talend.daikon.avro.converter;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

import org.talend.daikon.java8.Function;

/**
 * Provides a {@link Set} that wraps another, transparently applying a {@link Function} to all of its values.
 * 
 * @param <InT> The (hidden) type of the values in the wrapped set.
 * @param <OutT> The (visible) type of the values in this set.
 */
public class WrappedSet<InT, OutT> extends AbstractSet<OutT> {

    private final Set<InT> mWrapped;

    private final Function<InT, OutT> mInFunction;

    private final Function<OutT, InT> mOutFunction;

    WrappedSet(Set<InT> wrapped, Function<InT, OutT> inFunction, Function<OutT, InT> outFunction) {
        this.mWrapped = wrapped;
        this.mInFunction = inFunction;
        this.mOutFunction = outFunction;
    }

    @Override
    public Iterator<OutT> iterator() {
        return new WrappedIterator<>(mWrapped.iterator(), mInFunction);
    }

    @Override
    public int size() {
        return mWrapped.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object o) {
        return mWrapped.contains(mOutFunction.apply((OutT) o));
    }

}