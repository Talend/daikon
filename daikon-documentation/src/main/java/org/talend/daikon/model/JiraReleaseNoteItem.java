package org.talend.daikon.model;

import java.io.PrintWriter;
import java.util.Objects;

import com.atlassian.jira.rest.client.api.domain.Issue;

public class JiraReleaseNoteItem implements ReleaseNoteItem {

    private final Issue issue;

    private final String jiraServerUrl;

    public JiraReleaseNoteItem(Issue issue, String jiraServerUrl) {
        this.issue = issue;
        this.jiraServerUrl = jiraServerUrl;
    }

    @Override
    public ReleaseNoteItemType getIssueType() {
        return ReleaseNoteItemType.fromJiraIssueType(issue.getIssueType());
    }

    @Override
    public void writeTo(PrintWriter writer) {
        writer.println(
                "- link:" + jiraServerUrl + "/browse/" + issue.getKey() + "[" + issue.getKey() + "]: " + issue.getSummary());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JiraReleaseNoteItem that = (JiraReleaseNoteItem) o;
        return Objects.equals(issue, that.issue) && Objects.equals(jiraServerUrl, that.jiraServerUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issue, jiraServerUrl);
    }
}
