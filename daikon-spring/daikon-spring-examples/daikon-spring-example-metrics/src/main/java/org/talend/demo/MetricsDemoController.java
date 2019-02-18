package org.talend.demo;

import io.micrometer.core.instrument.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.talend.daikon.spring.metrics.VolumeMetered;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

@RestController
public class MetricsDemoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsDemoController.class);

    @Autowired
    private Worker worker;

    @Autowired
    private SubWorker subWorker;

    @GetMapping("/invoke")
    public void invoke() throws InterruptedException {
        worker.work();
    }

    @GetMapping("/invoke/sub")
    public void invokeSub() throws InterruptedException {
        subWorker.work();
    }

    @VolumeMetered
    @PostMapping(value = "/post", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public void post(InputStream input, OutputStream output) throws IOException {
        final String message = IOUtils.toString(input);
        LOGGER.info("Message received: {}", message);
        try (final OutputStreamWriter writer = new OutputStreamWriter(output)) {
            writer.write("Some answer");
        }
    }
}
