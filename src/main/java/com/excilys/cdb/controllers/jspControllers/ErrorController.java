package com.excilys.cdb.controllers.jspControllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

    @RequestMapping(value = "/400")
    public String handle400() {
        return "errors/400";
    }

    @RequestMapping(value = "/403")
    public String handle403() {
        return "errors/403";
    }

    @RequestMapping(value = "/404")
    public String handle404() {
        return "errors/404";
    }

    @RequestMapping(value = "/500")
    public String handle500() {
        return "errors/500";
    }

}
