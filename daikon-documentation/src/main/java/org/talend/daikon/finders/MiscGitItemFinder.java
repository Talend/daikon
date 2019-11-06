package org.talend.daikon.finders;

import java.util.regex.Matcher;
import java.util.stream.Stream;

import org.eclipse.jgit.revwalk.RevCommit;
import org.talend.daikon.model.MiscReleaseNoteItem;
import org.talend.daikon.model.ReleaseNoteItem;

public class MiscGitItemFinder extends AbstractGitItemFinder {

    private final String version;

    public MiscGitItemFinder(String version) {
        this(null, version);
    }

    public MiscGitItemFinder(String pathname, String version) {
        super(pathname);
        this.version = version;
    }

    @Override
    public Stream<ReleaseNoteItem> find() {
        try {
            return getGitCommits(version) //
                    .filter(c -> !c.getShortMessage().contains("release")) //
                    .map(c -> new Tuple(JIRA_DETECTION_PATTERN.matcher(c.getShortMessage()), c)) //
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
