package org.talend.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.micrometer.core.annotation.Timed;

/**
 * A sample {@link Component} that presents the {@link Timed} annotation.
 */
@Component
public class Worker {

    @Autowired
    private SubWorker subWorker;

    @Timed
    public void work() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            subWorker.work();
        }
    }

}
