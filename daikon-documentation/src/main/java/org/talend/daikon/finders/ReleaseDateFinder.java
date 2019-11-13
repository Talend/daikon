package org.talend.daikon.finders;

import java.util.Date;

public interface ReleaseDateFinder {

    /**
     * @return The {@link Date} of the release.
     */
    Date find();
}
