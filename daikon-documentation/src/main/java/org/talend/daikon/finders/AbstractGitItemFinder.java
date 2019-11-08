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

abstract class AbstractGitItemFinder implements ItemFinder {

    static final Pattern JIRA_DETECTION_PATTERN = Pattern.compile(".*((?<!([A-Z]{1,10})-?)[A-Z]+-\\d+).*");

    private String gitRepositoryPath;

    AbstractGitItemFinder(String gitRepositoryPath) {
        this.gitRepositoryPath = gitRepositoryPath;
    }

    private static Date getDate(RevWalk walk, Ref ref) {
        try {
            return walk.parseTag(ref.getObjectId()).getTaggerIdent().getWhen();
        } catch (IOException e) {
            return new Date(0);
        }
    }

    private static ObjectId getTagCommitId(RevWalk walk, Ref t) {
        try {
            return walk.parseTag(t.getObjectId()).getObject().getId();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Stream<RevCommit> getGitCommits(String version) {
        try {
            // Init git client
            final File dir;
            if (StringUtils.isNotBlank(gitRepositoryPath)) {
                dir = new File(gitRepositoryPath);
            } else {
                dir = new File(".");
            }
            final Git git = Git.open(dir);

            final Repository repository = git.getRepository();
            final RefDatabase refDatabase = repository.getRefDatabase();
            final RevWalk walk = new RevWalk(repository);
            if (refDatabase.hasRefs()) {
                final GitRange refsByPrefix = findRange(refDatabase, walk, version);
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

    private GitRange findRange(RefDatabase refDatabase, RevWalk walk, String version) throws IOException {
        final Stream<Ref> base = refDatabase.getRefsByPrefix(R_TAGS).stream();
        final ObjectId head = refDatabase.findRef("HEAD").getObjectId();

        if (StringUtils.isBlank(version)) {
            return base.max(comparing(r -> getDate(walk, r))) // Get latest tag from history
                    .map(start -> new GitRange(start.getObjectId(), head)) // If found, return range of latest tag to HEAD
                    .orElseGet(() -> { // Or else return range from HEAD to root
                        final RevCommit headCommit = walk.lookupCommit(head);
                        return new GitRange(headCommit.getParent(headCommit.getParentCount() - 1), head);
                    });
        } else {
            final AtomicBoolean hasMetVersion = new AtomicBoolean(false);
            final ThreadLocal<ObjectId> start = new ThreadLocal<>();

            final Optional<ObjectId> end = base //
                    .sorted((r1, r2) -> getDate(walk, r2).compareTo(getDate(walk, r1))) //
                    .filter(t -> {
                        if (!hasMetVersion.get()) {
                            if (t.getName().contains(version)) {
                                hasMetVersion.set(true);
                                start.set(getTagCommitId(walk, t));
                                return false; // So jump to next element after this one.
                            }
                        }
                        return hasMetVersion.get();
                    }) //
                    .findFirst() //
                    .map(ref -> getTagCommitId(walk, ref));

            return new GitRange(end.map(ObjectId::toObjectId).orElse(head), start.get());
        }
    }

    static class GitRange {

        ObjectId start;

        ObjectId end;

        private GitRange(ObjectId start, ObjectId end) {
            this.start = start;
            this.end = end;
        }
    }
}
