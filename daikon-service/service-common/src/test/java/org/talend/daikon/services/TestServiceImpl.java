package org.talend.daikon.services;

import org.springframework.web.bind.annotation.PathVariable;
import org.talend.daikon.annotation.ServiceImplementation;

@ServiceImplementation
public class TestServiceImpl implements TestService {

    @Override
    public String sayHi() {
        return I_SAY_HI;
    }

    @Override
    public String sayHiWithMyName(String name) {
        return "Hi " + name;
    }

    @Override
    public String sayHiWithMyNameInImplementation(@PathVariable("name") String name) {
        return "Hi from implementation: " + name;
    }

    @Override
    public String sayHiWithMyNameAndValue(String name, String value, String body) {
        return "Hi " + name + " " + value + " " + body;
    }
}
