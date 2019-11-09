package org.talend.daikon.finders;

import java.util.regex.Matcher;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.talend.daikon.model.GitCommit;
import org.talend.daikon.model.MiscReleaseNoteItem;
import org.talend.daikon.model.ReleaseNoteItem;

/**
 * Finds Git commit for release notes <b>NOT</b> linked to any Jira.
 */
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
    public Stream<? extends ReleaseNoteItem> find() {
        try {
            return getGitCommits(version) //
                    .filter(c -> !c.getCommit().getShortMessage().contains("release")) //
                    .map(c -> new Tuple(JIRA_DETECTION_PATTERN.matcher(c.getCommit().getShortMessage()), c)) //
                    .filter(t -> !t.getMatcher().matches()) //
                    .map(t -> new MiscReleaseNoteItem(t.getGitCommit()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    @AllArgsConstructor
    private static class Tuple {

        private Matcher matcher;

        private GitCommit gitCommit;

    }
}
