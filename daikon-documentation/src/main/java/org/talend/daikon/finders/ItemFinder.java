package org.talend.daikon.finders;

import java.util.stream.Stream;

import org.talend.daikon.model.ReleaseNoteItem;

public interface ItemFinder {

    /**
     * @return A <code>Stream</code> of {@link ReleaseNoteItem} to be used to generate release notes.
     */
    Stream<? extends ReleaseNoteItem> find();
}
