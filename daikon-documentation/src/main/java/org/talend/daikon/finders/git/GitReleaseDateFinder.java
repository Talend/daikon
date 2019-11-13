package org.talend.daikon.finders.git;

import static java.util.Comparator.comparing;

import java.util.Date;
import java.util.Optional;

import org.talend.daikon.finders.ReleaseDateFinder;

public class GitReleaseDateFinder extends AbstractGitItemFinder implements ReleaseDateFinder {

    public GitReleaseDateFinder(String version, String gitRepositoryPath, String gitHubRepositoryUrl) {
        super(version, gitRepositoryPath, gitHubRepositoryUrl);
    }

    @Override
    public Date find() {
        try {
            final Optional<Date> optionalDate = getGitCommits() //
                    .max(comparing(c -> c.getCommit().getCommitTime())) //
                    .map(c -> c.getCommit().getAuthorIdent().getWhen());
            return optionalDate.orElseGet(() -> new Date(0));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
