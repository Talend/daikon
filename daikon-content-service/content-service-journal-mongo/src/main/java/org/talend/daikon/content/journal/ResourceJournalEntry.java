package org.talend.daikon.content.journal;

import org.springframework.data.annotation.Id;

/**
 * A journal entry for {@link MongoResourceJournalResolver}.
 */
public class ResourceJournalEntry {

    @Id
    private String name;

    public ResourceJournalEntry(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}