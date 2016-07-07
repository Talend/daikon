package org.talend.daikon.services;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.talend.daikon.annotation.Call;
import org.talend.daikon.annotation.Service;

@Service(name = "GatewayService")
public interface GatewayService {

    @RequestMapping(value = "/api/say", method = RequestMethod.GET)
    String say();

    @RequestMapping(value = "/api/sayMyName", method = RequestMethod.GET)
    String sayMyName(@RequestParam("name") String name);

    @RequestMapping(value = "/api/missingOperation", method = RequestMethod.GET)
    String missingOperation();

    @RequestMapping(value = "/api/missingService", method = RequestMethod.GET)
    String missingService();

    @RequestMapping(value = "/api/custom", method = RequestMethod.GET)
    String custom();

}
