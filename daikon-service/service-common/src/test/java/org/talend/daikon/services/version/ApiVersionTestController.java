package org.talend.daikon.services.version;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.talend.daikon.annotation.ApiVersion;

@ApiVersion({ "0", "1" })
@RestController
public class ApiVersionTestController {

    @RequestMapping("classVersionRoute")
    public String defaultVersionRoute() {
        return "foo";
    }

    @ApiVersion({ "2-SNAPSHOT", "BAR" })
    @RequestMapping("methodVersionRoute")
    public String methodeVersionRoute() {
        return "bar";
    }

    @ApiVersion({ "new" })
    @RequestMapping("methodVersionRoute")
    public String methodeVersionRouteNew() {
        return "newbar";
    }

}