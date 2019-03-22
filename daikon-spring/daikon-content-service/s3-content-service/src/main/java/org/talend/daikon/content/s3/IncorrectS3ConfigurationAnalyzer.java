package org.talend.daikon.content.s3;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.springframework.boot.diagnostics.FailureAnalyzer;

/**
 * Allow S3 code to plug into Spring Boot's startup failure analysis.
 */
public class IncorrectS3ConfigurationAnalyzer implements FailureAnalyzer {

    @Override
    public FailureAnalysis analyze(Throwable failure) {
        final Throwable[] throwables = ExceptionUtils.getThrowables(failure);
        for (Throwable throwable : throwables) {
            if (throwable instanceof S3ContentServiceConfiguration.InvalidConfigurationMissingBean) {
                final S3ContentServiceConfiguration.InvalidConfigurationMissingBean issue = (S3ContentServiceConfiguration.InvalidConfigurationMissingBean) throwable;
                return new FailureAnalysis("Incorrect S3 configuration: " + issue.getMessage(), //
                        "Add a bean of class " + issue.getMissingBeanClass().getName(), //
                        failure);
            } else if (throwable instanceof S3ContentServiceConfiguration.InvalidConfiguration) {
                return new FailureAnalysis("Incorrect S3 configuration: " + throwable.getMessage(), //
                        "Fix missing or incorrect configuration values", //
                        failure);
            }
        }
        return null;
    }
}
