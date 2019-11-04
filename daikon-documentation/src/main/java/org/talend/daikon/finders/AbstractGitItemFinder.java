package org.talend.daikon.finders;

import static org.eclipse.jgit.lib.Constants.R_TAGS;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.RefDatabase;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

public abstract class AbstractGitItemFinder implements ItemFinder {

    public static final Pattern JIRA_DETECTION_PATTERN = Pattern.compile(".*((?<!([A-Z]{1,10})-?)[A-Z]+-\\d+).*");

    private String pathname;

    public AbstractGitItemFinder(String pathname) {
        this.pathname = pathname;
    }

    protected Stream<RevCommit> getGitCommits() {
        try {
            // Init git client
            InitCommand gitInit = Git.init();
            if (StringUtils.isNotBlank(pathname)) {
                gitInit = gitInit.setDirectory(new File(pathname));
            }
            final Git git = gitInit.call();

            final Repository repository = git.getRepository();
            final RefDatabase refDatabase = repository.getRefDatabase();
            final RevWalk walk = new RevWalk(repository);
            if (refDatabase.hasRefs()) {
                final Optional<Ref> refsByPrefix = refDatabase.getRefsByPrefix(R_TAGS) //
                        .stream() //
                        .max(Comparator.comparing(r -> getDate(walk, r)));

                if (refsByPrefix.isPresent()) {
                    final AnyObjectId start = walk.parseTag(refsByPrefix.get().getObjectId()).getObject();
                    final ObjectId head = repository.getRefDatabase().findRef("HEAD").getObjectId();
                    final Iterable<RevCommit> commits = git.log().addRange(start, head).call();
                    return StreamSupport.stream(commits.spliterator(), false);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Stream.empty();
    }

    private static Date getDate(RevWalk walk, Ref ref) {
        try {
            return walk.parseTag(ref.getObjectId()).getTaggerIdent().getWhen();
        } catch (IOException e) {
            return new Date(0);
        }
    }
}
