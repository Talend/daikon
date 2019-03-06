package org.talend.demo;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.micrometer.core.annotation.Timed;

@Component
public class SubWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubWorker.class);

    @Timed
    public void work() throws InterruptedException {
        final int timeout = new Random().nextInt(5);
        LOGGER.info("Sleep {} seconds...", timeout);
        TimeUnit.SECONDS.sleep(timeout);
    }
}
