package org.talend.tenancy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A fall back implementation of {@link ForAll} in case code is running with no tenancy enabled.
 */
@Component
@ConditionalOnProperty(name = "task.scheduled.enabled", havingValue = "false", matchIfMissing = true)
public class DefaultForAll implements ForAll {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultForAll.class);

    @PostConstruct
    public void init() {
        LOGGER.info("ForAll: multi tenancy disabled.");
    }

    @Override
    public After execute(Supplier<Boolean> condition, Runnable runnable) {
        return new After() {

            private Runnable after = null;

            @Override
            public After after(Runnable runnable) {
                after = runnable;
                return this;
            }

            @Override
            public void run() {
                try {
                    if (condition.get()) {
                        runnable.run();
                    } else {
                        LOGGER.debug("Unable to run '{}' (condition disallowed run of it).", runnable);
                    }
                } catch (Exception e) {
                    LOGGER.warn("Unable to execute run '{}'. Skip execution.", runnable);
                    LOGGER.debug("Unable to execute run '{}'. Skip execution error.", e);
                } finally {
                    Optional.ofNullable(after).ifPresent(Runnable::run);
                }
            }
        };

    }

    @Override
    public ForAllConditionBuilder condition() {
        // This ForAllConditionBuilder implementation always returns a Supplier that returns true
        return bean -> () -> true;
    }
}
