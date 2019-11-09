package org.talend.daikon.model;

import java.io.PrintWriter;

import org.eclipse.jgit.revwalk.RevCommit;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public class MiscReleaseNoteItem implements ReleaseNoteItem {

    private final GitCommit commit;

    @Override
    public ReleaseNoteItemType getIssueType() {
        return ReleaseNoteItemType.MISC;
    }

    @Override
    public void writeTo(PrintWriter writer) {
        final PullRequest pullRequest = commit.getPullRequest();
        final RevCommit commit = this.commit.getCommit();
        if (pullRequest != null) {
            final String processedShortMessage = commit.getShortMessage().replace("(#" + pullRequest.getDisplay() + ")", "");
            writer.println(
                    "- " + processedShortMessage + " (link:" + pullRequest.getUrl() + "[#" + pullRequest.getDisplay() + "])");
        } else {
            writer.println("- " + commit.getShortMessage());
        }
    }

    @Override
    public String toString() {
        return "MiscReleaseNoteItem{" + getIssueType() + ", " + "commit=" + commit.getCommit().getShortMessage() + '}';
    }

}
