package org.talend.daikon.finders;

import java.util.regex.Matcher;
import java.util.stream.Stream;

import org.talend.daikon.model.JiraReleaseNoteItem;
import org.talend.daikon.model.ReleaseNoteItem;

import com.atlassian.jira.rest.client.api.JiraRestClient;

public class GitItemFinder extends AbstractGitItemFinder {

    private final String jiraServerUrl;

    private final JiraRestClient client;

    public GitItemFinder(String jiraServerUrl, JiraRestClient client) {
        this(null, jiraServerUrl, client);
    }

    public GitItemFinder(String pathname, String jiraServerUrl, JiraRestClient client) {
        super(pathname);
        this.jiraServerUrl = jiraServerUrl;
        this.client = client;
    }

    @Override
    public Stream<ReleaseNoteItem> find() {
        try {
            return getGitCommits().map(c -> JIRA_DETECTION_PATTERN.matcher(c.getShortMessage())).filter(Matcher::matches) //
                    .map(m -> m.group(1)) //
                    .map(jiraId -> client.getIssueClient().getIssue(jiraId).claim()) //
                    .map(i -> new JiraReleaseNoteItem(i, jiraServerUrl));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
