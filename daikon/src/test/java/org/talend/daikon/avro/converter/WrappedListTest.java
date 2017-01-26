package org.talend.daikon.avro.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.talend.daikon.java8.Function;

public class WrappedListTest {

    @Test
    public void testEmpty() {
        List<Object> actual = null;
        WrappedList<Object, Object> wrappedList = new WrappedList<>(actual, new UnconvertFunction(), new UnconvertFunction());
        assertEquals(0, wrappedList.size());
    }

    @Test
    public void testBasic() {
        List<String> actual = Arrays.asList("1", "2", "3");
        WrappedList<String, String> wrappedList = new WrappedList<>(actual, new UnconvertFunction(), new UnconvertFunction());
        assertEquals(actual.size(), wrappedList.size());
        for (int i = 0; i < actual.size(); i++) {
            assertEquals(actual.get(i), wrappedList.get(i));
            assertTrue(wrappedList.contains(actual.get(i)));
        }
    }

    public static class UnconvertFunction<T> implements Function<T, T> {

        @Override
        public T apply(T o) {
            return o;
        }
    }

}
