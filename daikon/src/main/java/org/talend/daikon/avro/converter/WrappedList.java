package org.talend.daikon.avro.converter;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.talend.daikon.java8.Function;

/**
 * Provides a {@link List} that wraps another, transparently applying a {@link Function} to all of its values.
 * 
 * @param <InT> The (hidden) type of the values in the wrapped list.
 * @param <OutT> The (visible) type of the values in this list.
 */
public class WrappedList<InT, OutT> extends AbstractList<OutT> {

    private final List<InT> mWrapped;

    private final Function<InT, OutT> mInFunction;

    private final Function<OutT, InT> mOutFunction;

    WrappedList(List<InT> wrapped, Function<InT, OutT> inFunction, Function<OutT, InT> outFunction) {
        this.mWrapped = wrapped == null ? new ArrayList<InT>(0) : wrapped;
        this.mInFunction = inFunction;
        this.mOutFunction = outFunction;
    }

    @Override
    public OutT get(int index) {
        return mInFunction.apply(mWrapped.get(index));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        return mWrapped.contains(mOutFunction.apply((OutT) o));
    }

    @Override
    public int size() {
        return mWrapped.size();
    }
}
