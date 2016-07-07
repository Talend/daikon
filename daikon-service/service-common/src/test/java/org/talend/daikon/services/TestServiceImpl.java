package org.talend.daikon.services;

import org.springframework.web.bind.annotation.PathVariable;
import org.talend.daikon.annotation.ServiceImplementation;

@ServiceImplementation
class TestServiceImpl implements TestService {

    @Override
    public String sayHi() {
        return I_SAY_HI;
    }

    @Override
    public String sayHiWithMyName(@PathVariable String name) {
        return "Hi " + name;
    }
}
