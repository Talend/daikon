package org.talend.daikon.finders;

import java.util.regex.Matcher;
import java.util.stream.Stream;

import org.talend.daikon.model.JiraReleaseNoteItem;
import org.talend.daikon.model.ReleaseNoteItem;

import com.atlassian.jira.rest.client.api.JiraRestClient;

public class GitItemFinder extends AbstractGitItemFinder {

    private final String jiraServerUrl;

    private final JiraRestClient client;

    private final String version;

    public GitItemFinder(String jiraServerUrl, JiraRestClient client, String version) {
        this(null, jiraServerUrl, client, version);
    }

    public GitItemFinder(String pathname, String jiraServerUrl, JiraRestClient client, String version) {
        super(pathname);
        this.jiraServerUrl = jiraServerUrl;
        this.client = client;
        this.version = version;
    }

    @Override
    public Stream<ReleaseNoteItem> find() {
        try {
            return getGitCommits(version) //
                    .filter(c -> !c.getShortMessage().contains("release")) //
                    .map(c -> JIRA_DETECTION_PATTERN.matcher(c.getShortMessage())) //
                    .filter(Matcher::matches) //
                    .map(matcher -> matcher.group(1)) //
                    .map(jiraId -> client.getIssueClient().getIssue(jiraId).claim()) //
                    .map(issue -> new JiraReleaseNoteItem(issue, jiraServerUrl));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
