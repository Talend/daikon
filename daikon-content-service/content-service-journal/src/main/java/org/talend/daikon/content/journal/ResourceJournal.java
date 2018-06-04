package org.talend.daikon.content.journal;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface ResourceJournal {

    /**
     * Synchronize the content of this journal with the underlying {@link org.talend.daikon.content.ResourceResolver}.
     */
    void sync();

    Stream<String> matches(String pattern) throws IOException;

    void clear(String location);

    void add(String location);

    void remove(String location);

    void move(String source, String target);

    boolean exist(String location);
}
