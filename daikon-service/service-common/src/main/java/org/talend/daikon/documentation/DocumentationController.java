package org.talend.daikon.documentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DocumentationController {

    @RequestMapping("/docs")
    String documentation() {
        return "redirect:/docs/apidocs/index.html";
    }

}
