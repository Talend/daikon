package org.talend.daikon.model;

import java.io.PrintWriter;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "MiscReleaseNoteItem{" + getIssueType() + ", " + "commit=" + commit.getShortMessage() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        MiscReleaseNoteItem that = (MiscReleaseNoteItem) o;
        return Objects.equals(commit, that.commit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commit);
    }
}
