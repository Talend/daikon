package org.talend.daikon.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.eclipse.jgit.revwalk.RevCommit;

@Getter
@AllArgsConstructor
public class GitCommit {

    private RevCommit commit;

    private PullRequest pullRequest;

}
