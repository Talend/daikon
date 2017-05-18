package org.talend.daikon.hystrix;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.daikon.exception.TalendRuntimeException;
import org.talend.daikon.hystrix.processors.InternalErrorCodes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.hystrix.HystrixCommand;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import rx.Observable;

public class CommandHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHelper.class);

    private CommandHelper() {
    }

    /**
     * Return a Publisher of type T out of the the hystrix command.
     *
     * @param clazz the wanted stream type.
     * @param mapper the object mapper used to parse objects.
     * @param command the hystrix command to deal with.
     * @param <T> the type of objects to stream.
     * @return a Publisher out of the hystrix command response body.
     */
    public static <T> Publisher<T> toPublisher(final Class<T> clazz, final ObjectMapper mapper,
            final HystrixCommand<InputStream> command) {
        AtomicInteger count = new AtomicInteger(0);
        return Flux.create(sink -> {
            final Observable<InputStream> observable = command.toObservable();
            observable.map(i -> {
                try {
                    return mapper.readerFor(clazz).<T> readValues(i);
                } catch (IOException e) {
                    throw new TalendRuntimeException(InternalErrorCodes.UNEXPECTED_EXCEPTION, e);
                }
            }) //
                    .doOnCompleted(() -> LOGGER.debug("Completed command '{}' (emits '{}') with '{}' records.",
                            command.getClass().getName(), clazz.getName(), count.get())) //
                    .toBlocking() //
                    .forEach(s -> {
                        while (s.hasNext()) {
                            sink.next(s.next());
                            count.incrementAndGet();
                        }
                        sink.complete();
                    });
        }, FluxSink.OverflowStrategy.BUFFER);
    }

    /**
     * Return a {@link Stream} of type T out of the the hystrix command.
     *
     * @param clazz The class of stream content.
     * @param mapper The object mapper used to parse objects.
     * @param command The hystrix command to deal with.
     * @param <T> the type of objects to stream.
     * @return a Stream out of the hystrix command response body.
     */
    public static <T> Stream<T> toStream(Class<T> clazz, ObjectMapper mapper, HystrixCommand<InputStream> command) {
        return Flux.from(toPublisher(clazz, mapper, command)).toStream(1);
    }
}
