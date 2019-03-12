package org.talend.daikon.content.journal;

import java.util.stream.Stream;

public interface ResourceJournal {

    /**
     * Synchronize the content of this journal
     */
    void sync();

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
     * 
     * @see #ready()
     * @see #invalidate()
     */
    void validate();

    /**
     * Marks this journal as incomplete thus <b>not</b> ready for use. After method completes, {@link #ready()} must
     * return <code>false</code>.
     * 
     * @see #ready()
     * @see #validate()
     */
    void invalidate();
}
