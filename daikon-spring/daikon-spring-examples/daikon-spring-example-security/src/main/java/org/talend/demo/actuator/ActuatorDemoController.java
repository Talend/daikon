package org.talend.demo.actuator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActuatorDemoController {

    private final Counter counter;

    public ActuatorDemoController(MeterRegistry registry) {
        counter = registry.counter("hi_count");
    }

    @GetMapping("/say/hi")
    public String sayHi() {
        counter.increment();
        return "Hi";
    }

    @GetMapping("/say/secure/hi")
    public String saySecureHi() {
        counter.increment();
        return "Secure Hi";
    }
}
