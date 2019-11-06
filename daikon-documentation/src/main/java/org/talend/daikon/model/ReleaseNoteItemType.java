package org.talend.daikon.model;

import com.atlassian.jira.rest.client.api.domain.IssueType;

public enum ReleaseNoteItemType {

    BUG("Fix"),
    FEATURE("Feature"),
    WORK_ITEM("Work Item"),
    MISC("Other");

    private final String displayName;

    ReleaseNoteItemType(String displayName) {
        this.displayName = displayName;
    }

    public static ReleaseNoteItemType fromJiraIssueType(IssueType issueType) {
        switch (issueType.getName().toLowerCase()) {
        case "bug":
            return BUG;
        case "work item":
            return WORK_ITEM;
        case "new feature":
        case "epic":
            return FEATURE;
        default:
            return MISC;
        }
    }

    public String getDisplayName() {
        return displayName;
    }
}
