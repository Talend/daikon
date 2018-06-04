package org.talend.daikon.content.journal;

import org.springframework.data.annotation.Id;

public class ResourceJournalEntry {

    @Id
    private String id;

    private String name;

    public ResourceJournalEntry(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}