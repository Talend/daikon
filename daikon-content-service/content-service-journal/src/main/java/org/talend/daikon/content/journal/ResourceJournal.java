package org.talend.daikon.content.journal;

import org.talend.daikon.content.ResourceResolver;

import java.util.stream.Stream;

public interface ResourceJournal {

    /**
     * <p>
     * Synchronize the content of this journal with the {@link ResourceResolver} passed as parameter.
     * </p>
     * <p>
     * Method is not expected to block callee.
     * </p>
     *
     * @param resourceResolver The {@link ResourceResolver} to use for synchronization.
     */
    void sync(ResourceResolver resourceResolver);

    Stream<String> matches(String pattern);

    void clear(String pattern);

    void add(String location);

    void remove(String location);

    void move(String source, String target);

    boolean exist(String location);

    /**
     * @return <code>true</code> if journal is ready for usage, <code>false</code> otherwise.
     */
    boolean ready();

    /**
     * Marks this journal as ready for use. After method completes, {@link #ready()} must return <code>true</code>.
     * @see #ready()
     * @see #invalidate()
     */
    void validate();

    /**
     * Marks this journal as incomplete thus <b>not</b> ready for use. After method completes, {@link #ready()} must
     * return <code>false</code>.
     * @see #ready()
     * @see #validate()
     */
    void invalidate();
}
