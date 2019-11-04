package org.talend.daikon.finders;

import java.util.regex.Matcher;
import java.util.stream.Stream;

import org.eclipse.jgit.revwalk.RevCommit;
import org.talend.daikon.model.MiscReleaseNoteItem;
import org.talend.daikon.model.ReleaseNoteItem;

public class MiscGitItemFinder extends AbstractGitItemFinder {

    public MiscGitItemFinder() {
        this(null);
    }

    public MiscGitItemFinder(String pathname) {
        super(pathname);
    }

    @Override
    public Stream<ReleaseNoteItem> find() {
        try {
            return getGitCommits().map(c -> new Tuple(JIRA_DETECTION_PATTERN.matcher(c.getShortMessage()), c))
                    .filter(t -> !t.matcher.matches()) //
                    .map(t -> new MiscReleaseNoteItem(t.commit));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class Tuple {

        private final Matcher matcher;

        private final RevCommit commit;

        private Tuple(Matcher matcher, RevCommit commit) {
            this.matcher = matcher;
            this.commit = commit;
        }
    }
}
