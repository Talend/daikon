package org.talend.daikon.finders;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.collect.Streams;
import org.talend.daikon.model.JiraReleaseNoteItem;
import org.talend.daikon.model.ReleaseNoteItem;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.SearchResult;

import io.atlassian.util.concurrent.Promise;

public class JiraItemFinder implements ItemFinder {

    private final String jiraProject;

    private final String jiraVersion;

    private final String jiraServerUrl;

    private final JiraRestClient client;

    public JiraItemFinder(String jiraProject, String jiraVersion, String jiraServerUrl, JiraRestClient client) {
        this.jiraProject = jiraProject;
        this.jiraVersion = jiraVersion;
        this.jiraServerUrl = jiraServerUrl;
        this.client = client;
    }

    @Override
    public Stream<? extends ReleaseNoteItem> find() {
        final String jql = "project = '" + jiraProject + "' and fixVersion='" + jiraVersion + "' and status in (Closed, Done)";
        final Promise<SearchResult> results = client.getSearchClient().searchJql(jql);
        return Streams.stream(results.claim().getIssues()).map(i -> new JiraReleaseNoteItem(i, jiraServerUrl));
    }
}
