package org.talend.daikon.finders;

import static java.util.Comparator.comparing;
import static org.eclipse.jgit.lib.Constants.R_TAGS;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
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

    private static Date getDate(RevWalk walk, Ref ref) {
        try {
            return walk.parseTag(ref.getObjectId()).getTaggerIdent().getWhen();
        } catch (IOException e) {
            return new Date(0);
        }
    }

    protected Stream<RevCommit> getGitCommits(String version) {
        try {
            // Init git client
            final File dir;
            if (StringUtils.isNotBlank(pathname)) {
                dir = new File(pathname);
            } else {
                dir = new File(".");
            }
            final Git git = Git.open(dir);

            final Repository repository = git.getRepository();
            final RefDatabase refDatabase = repository.getRefDatabase();
            final RevWalk walk = new RevWalk(repository);
            if (refDatabase.hasRefs()) {
                final GitRange refsByPrefix = findRange(refDatabase, walk, repository, version);
                final ObjectId start = refsByPrefix.start;
                final ObjectId head = refsByPrefix.end;

                final Iterable<RevCommit> commits = git.log().addRange(start, head).call();
                return StreamSupport.stream(commits.spliterator(), false);

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Stream.empty();
    }

    private GitRange findRange(RefDatabase refDatabase, RevWalk walk, Repository repository, String version) throws IOException {
        final Stream<Ref> base = refDatabase.getRefsByPrefix(R_TAGS).stream();

        if (StringUtils.isBlank(version)) {
            final Optional<Ref> start = base.max(comparing(r -> getDate(walk, r)));
            if (!start.isPresent()) {
                // TODO Start from initial commit?
                throw new IllegalStateException("Unable to find the latest tag in repository.");
            } else {
                return new GitRange(start.get().getObjectId(), repository.getRefDatabase().findRef("HEAD").getObjectId());
            }
        } else {
            final AtomicBoolean hasMetVersion = new AtomicBoolean(false);
            final ObjectId[] start = new ObjectId[1];

            final Optional<ObjectId> end = base //
                    .sorted((r1, r2) -> getDate(walk, r2).compareTo(getDate(walk, r1))) //
                    .filter(t -> {
                        if (!hasMetVersion.get()) {
                            if (t.getName().contains(version)) {
                                hasMetVersion.set(true);
                                start[0] = getTagCommitId(walk, t);
                                return false; // So jump to next element after this one.
                            }
                        }
                        return hasMetVersion.get();
                    }) //
                    .findFirst() //
                    .map(ref -> getTagCommitId(walk, ref));

            return new GitRange(end.get(), start[0]);
        }
    }

    private ObjectId getTagCommitId(RevWalk walk, Ref t) {
        try {
            return walk.parseTag(t.getObjectId()).getObject().getId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class GitRange {

        ObjectId start;

        ObjectId end;

        public GitRange(ObjectId start, ObjectId end) {
            this.start = start;
            this.end = end;
        }
    }
}
