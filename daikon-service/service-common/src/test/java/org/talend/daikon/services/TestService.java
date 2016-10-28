package org.talend.daikon.services;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.talend.daikon.annotation.Service;

@Service(name = "TestService")
public interface TestService {

    String I_SAY_HI = "I say hi!";

    @RequestMapping(value = "/path/to/service", method = RequestMethod.GET)
    String sayHi();

    @RequestMapping(value = "/path/to/service/{name}", method = RequestMethod.GET)
    String sayHiWithMyName(@PathVariable("name") String name);

    @RequestMapping(value = "/path/to/service/implementation/{name}", method = RequestMethod.GET)
    String sayHiWithMyNameInImplementation(@PathVariable("name") String name);
}
