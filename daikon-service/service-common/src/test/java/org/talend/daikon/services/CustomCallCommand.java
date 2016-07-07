package org.talend.daikon.services;

import com.netflix.hystrix.HystrixCommand;
import org.springframework.stereotype.Component;

@Component
public class CustomCallCommand extends HystrixCommand<String> {

    protected CustomCallCommand() {
        super(() -> "customGroup");
    }

    @Override
    protected String run() throws Exception {
        return "custom";
    }
}
