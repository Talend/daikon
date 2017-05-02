package org.talend.daikon.logging.event.layout;

import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.spi.DeferredProcessingAware;

public abstract class JsonLayout<E extends DeferredProcessingAware> extends LayoutBase<E> {

}
