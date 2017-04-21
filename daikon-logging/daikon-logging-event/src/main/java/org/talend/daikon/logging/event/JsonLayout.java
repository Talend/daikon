package org.talend.daikon.logging.event;

import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.spi.DeferredProcessingAware;

public abstract class JsonLayout<Event extends DeferredProcessingAware> extends LayoutBase<Event> {

}
