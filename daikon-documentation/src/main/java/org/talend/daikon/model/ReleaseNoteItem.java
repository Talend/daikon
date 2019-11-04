package org.talend.daikon.model;

import java.io.PrintWriter;

public interface ReleaseNoteItem {

    ReleaseNoteItemType getIssueType();

    void writeTo(PrintWriter writer);

}
