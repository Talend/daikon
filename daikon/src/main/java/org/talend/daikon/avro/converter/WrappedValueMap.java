package org.talend.daikon.avro.converter;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

import org.talend.daikon.java8.Function;
import org.talend.daikon.java8.SerializableFunction;

/**
 * Provides a {@link Map} that wraps another, transparently applying a {@link Function} to all of its values.
 * 
 * @param <KeyT> The type of the key in the map.
 * @param <InT> The (hidden) type of the values in the wrapped map.
 * @param <OutT> The (visible) type of the values in this map.
 */
public class WrappedValueMap<KeyT, InT, OutT> extends AbstractMap<KeyT, OutT> {

    private final Map<KeyT, InT> mWrapped;

    private final Function<InT, OutT> mInFunction;

    private final Function<OutT, InT> mOutFunction;

    WrappedValueMap(Map<KeyT, InT> wrapped, Function<InT, OutT> inFunction, Function<OutT, InT> outFunction) {
        this.mWrapped = wrapped;
        this.mInFunction = inFunction;
        this.mOutFunction = outFunction;
    }

    @Override
    public boolean containsKey(Object key) {
        return mWrapped.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean containsValue(Object value) {
        return mWrapped.containsValue(mOutFunction.apply((OutT) value));
    }

    @Override
    public Set<KeyT> keySet() {
        return mWrapped.keySet();
    }

    @Override
    public OutT get(Object key) {
        return mInFunction.apply(mWrapped.get(key));
    }

    @Override
    public Set<Map.Entry<KeyT, OutT>> entrySet() {
        Set<Map.Entry<KeyT, InT>> in = mWrapped.entrySet();
        return new WrappedSet<>(in, new LambdaConvertToMappedValueEntry(), new LambdaUnwrapMappedValueEntry());
    }

    private class LambdaConvertToMappedValueEntry implements SerializableFunction<Map.Entry<KeyT, InT>, Map.Entry<KeyT, OutT>> {

        /** Default serial version UID. */
        private static final long serialVersionUID = 1L;

        @Override
        public java.util.Map.Entry<KeyT, OutT> apply(java.util.Map.Entry<KeyT, InT> t) {
            return new MappedValueEntry(t);
        }

    }

    private class LambdaUnwrapMappedValueEntry implements SerializableFunction<Map.Entry<KeyT, OutT>, Map.Entry<KeyT, InT>> {

        /** Default serial version UID. */
        private static final long serialVersionUID = 1L;

        @Override
        public java.util.Map.Entry<KeyT, InT> apply(java.util.Map.Entry<KeyT, OutT> t) {
            if (t instanceof WrappedValueMap.MappedValueEntry)
                return ((MappedValueEntry) t).getWrapped();
            else
                return new AbstractMap.SimpleEntry<>(t.getKey(), mOutFunction.apply(t.getValue()));
        }

    }

    private class MappedValueEntry implements Map.Entry<KeyT, OutT> {

        private final Map.Entry<KeyT, InT> mWrappedEntry;

        public MappedValueEntry(Map.Entry<KeyT, InT> wrappedEntry) {
            mWrappedEntry = wrappedEntry;
        }

        Map.Entry<KeyT, InT> getWrapped() {
            return mWrappedEntry;
        }

        @Override
        public KeyT getKey() {
            return mWrappedEntry.getKey();
        }

        @Override
        public OutT getValue() {
            return mInFunction.apply(mWrappedEntry.getValue());
        }

        @Override
        public OutT setValue(OutT value) {
            return mInFunction.apply(mWrappedEntry.setValue(mOutFunction.apply(value)));
        }

        @Override
        public int hashCode() {
            // The hashCode() for Map.Entry is defined as this:
            return (getKey() == null ? 0 : getKey().hashCode()) ^ (getValue() == null ? 0 : getValue().hashCode());
        }

        @Override
        public boolean equals(Object other) {
            if (other == null || !(other instanceof Map.Entry<?, ?>))
                return false;
            // The hashCode() for Map.Entry is defined as this:
            Map.Entry<?, ?> e2 = (Map.Entry<?, ?>) other;
            return (getKey() == null ? e2.getKey() == null : getKey().equals(e2.getKey()))
                    && (getValue() == null ? e2.getValue() == null : getValue().equals(e2.getValue()));
        }
    }
}