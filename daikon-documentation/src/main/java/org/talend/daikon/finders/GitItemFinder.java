package org.talend.daikon.finders;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.talend.daikon.model.JiraReleaseNoteItem;
import org.talend.daikon.model.ReleaseNoteItem;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.google.common.collect.Streams;

public class GitItemFinder extends AbstractGitItemFinder {

    private final String jiraServerUrl;

    private final JiraRestClient client;

    private final String version;

    public GitItemFinder(String jiraServerUrl, JiraRestClient client, String version) {
        this(null, jiraServerUrl, client, version);
    }

    public GitItemFinder(String gitRepositoryPath, String jiraServerUrl, JiraRestClient client, String version) {
        super(gitRepositoryPath);
        this.jiraServerUrl = jiraServerUrl;
        this.client = client;
        this.version = version;
    }

    @Override
    public Stream<? extends ReleaseNoteItem> find() {
        try {
            return supplyAsync(() -> { // Get all Jira id from commits
                return getGitCommits(version) //
                        .filter(c -> !c.getShortMessage().contains("release")) //
                        .map(c -> JIRA_DETECTION_PATTERN.matcher(c.getShortMessage())) //
                        .filter(Matcher::matches) //
                        .map(matcher -> matcher.group(1)) //
                        .collect(Collectors.toList());
            }) //
                    .thenApply(jiraIds -> { // Get all Jira issues in one call
                        final String idList = jiraIds.stream() //
                                .map(s -> "\"" + s + "\"") //
                                .collect(Collectors.joining(", "));
                        final String jql = "id IN (" + idList + ")";
                        final SearchResult results = client.getSearchClient().searchJql(jql).claim();
                        return Streams.stream(results.getIssues());
                    }) //
                    .thenApply(jiraIssues -> { // Generate JiraReleaseNoteItem for Jira issues
                        return jiraIssues.map(issue -> new JiraReleaseNoteItem(issue, jiraServerUrl));
                    }) //
                    .get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
