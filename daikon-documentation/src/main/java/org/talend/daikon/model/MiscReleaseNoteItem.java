package org.talend.daikon.model;

import java.io.PrintWriter;

import org.eclipse.jgit.revwalk.RevCommit;

public class MiscReleaseNoteItem implements ReleaseNoteItem {

    private final RevCommit commit;

    public MiscReleaseNoteItem(RevCommit commit) {
        this.commit = commit;
    }

    @Override
    public ReleaseNoteItemType getIssueType() {
        return ReleaseNoteItemType.MISC;
    }

    @Override
    public void writeTo(PrintWriter writer) {
        writer.println("- " + commit.getShortMessage());
    }
}
