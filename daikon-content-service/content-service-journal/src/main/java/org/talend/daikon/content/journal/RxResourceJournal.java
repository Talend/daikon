package org.talend.daikon.content.journal;

import reactor.core.publisher.FluxSink;
import reactor.core.publisher.UnicastProcessor;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class RxResourceJournal implements ResourceJournal {

    private final ResourceJournal delegate;

    private final FluxSink<String> addSink;

    private final FluxSink<String> removeSink;

    private RxResourceJournal(ResourceJournal delegate) {
        this.delegate = delegate;
        this.addSink = connect(delegate::add);
        this.removeSink = connect(delegate::remove);
    }

    public static ResourceJournal asReactive(ResourceJournal journal) {
        return new RxResourceJournal(journal);
    }

    private FluxSink<String> connect(Consumer<String> consumer) {
        final UnicastProcessor<String> processor = UnicastProcessor.create();
        processor.subscribe(consumer);
        return processor.sink();
    }

    @Override
    public void sync() {
        delegate.sync();
    }

    @Override
    public Stream<String> matches(String pattern) throws IOException {
        return delegate.matches(pattern);
    }

    @Override
    public void clear(String location) {
        delegate.clear(location);
    }

    @Override
    public void add(String location) {
        addSink.next(location);
    }

    @Override
    public void remove(String location) {
        removeSink.next(location);
    }

    @Override
    public void move(String source, String target) {
        delegate.move(source, target);
    }

    @Override
    public boolean exist(String location) {
        return delegate.exist(location);
    }
}
