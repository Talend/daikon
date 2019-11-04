package org.talend.daikon.model;

import com.atlassian.jira.rest.client.api.domain.IssueType;

public enum ReleaseNoteItemType {

    BUG("Bug"),
    FEATURE("Feature"),
    WORK_ITEM("Work item"),
    MISC("Others");

    private final String displayName;

    ReleaseNoteItemType(String displayName) {
        this.displayName = displayName;
    }

    public static ReleaseNoteItemType fromJiraIssueType(IssueType issueType) {
        System.out.println(issueType);
        switch (issueType.getName().toLowerCase()) {
        case "bug":
            return BUG;
        case "work item":
            return WORK_ITEM;
        case "new feature":
            return FEATURE;
        default:
            return MISC;
        }
    }

    public String getDisplayName() {
        return displayName;
    }
}
