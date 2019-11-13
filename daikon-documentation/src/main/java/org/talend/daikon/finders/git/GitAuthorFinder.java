package org.talend.daikon.finders.git;

import java.util.stream.Stream;

import org.talend.daikon.finders.AuthorFinder;
import org.talend.daikon.model.Author;

public class GitAuthorFinder extends AbstractGitItemFinder implements AuthorFinder {

    public GitAuthorFinder(String version, String gitRepositoryPath, String gitHubRepositoryUrl) {
        super(version, gitRepositoryPath, gitHubRepositoryUrl);
    }

    @Override
    public Stream<Author> findAuthors() {
        return getGitCommits() //
                .filter(c -> !c.getCommit().getShortMessage().contains("release")) //
                .map(c -> new Author(c.getCommit().getAuthorIdent().getName())) //
                .distinct();
    }

}
