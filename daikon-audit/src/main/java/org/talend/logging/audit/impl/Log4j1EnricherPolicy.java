package org.talend.logging.audit.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.rewrite.RewritePolicy;
import org.apache.log4j.spi.LoggingEvent;

/**
 *
 */
public class Log4j1EnricherPolicy implements RewritePolicy {

    private LogEnricher logEnricher = new LogEnricher();

    public void setLogEnricher(LogEnricher logEnricher) {
        this.logEnricher = logEnricher;
    }

    @Override
    @SuppressWarnings({ "unchecked" })
    public LoggingEvent rewrite(LoggingEvent source) {
        final String categoryName = Utils.getCategoryFromLoggerName(source.getLoggerName());
        final Map<?, ?> props = logEnricher.enrich(categoryName, source.getProperties());

        return new LoggingEvent(source.getFQNOfLoggerClass(),
                source.getLogger() != null ? source.getLogger() : Logger.getLogger(source.getLoggerName()), source.getTimeStamp(),
                source.getLevel(), source.getMessage(), source.getThreadName(), source.getThrowableInformation(), source.getNDC(),
                source.locationInformationExists() ? source.getLocationInformation() : null, props);
    }
}
