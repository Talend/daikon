package org.talend.daikon.content.journal.configuration;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalyzer;
import org.talend.daikon.content.journal.ResourceJournal;

/**
 * Allow journalized resource resolver code to plug into Spring Boot's startup failure analysis.
 */
public class IncorrectJournalConfigurationAnalyzer implements FailureAnalyzer {

    @Override
    public FailureAnalysis analyze(Throwable failure) {
        final Throwable[] throwables = ExceptionUtils.getThrowables(failure);
        for (Throwable throwable : throwables) {
            if (throwable instanceof JournalizedConfiguration.MissingJournalBean) {
                final JournalizedConfiguration.MissingJournalBean issue = (JournalizedConfiguration.MissingJournalBean) throwable;
                return new FailureAnalysis("Incorrect S3 configuration: " + issue.getMessage(), //
                        "Add a bean of class " + ResourceJournal.class.getName(), //
                        failure);
            }
        }
        return null;
    }
}
