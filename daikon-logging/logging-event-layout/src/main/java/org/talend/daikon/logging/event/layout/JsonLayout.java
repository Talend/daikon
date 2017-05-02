package org.talend.daikon.logging.event.layout;

import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.spi.DeferredProcessingAware;

public class JsonLayout<Event extends DeferredProcessingAware> extends LayoutBase<Event> {

    @Override
    public String doLayout(Event event) {
        return null;
    }

}
