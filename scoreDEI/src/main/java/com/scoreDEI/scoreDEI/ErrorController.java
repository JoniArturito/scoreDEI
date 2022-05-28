package com.scoreDEI.scoreDEI;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorController {

    @GetMapping("/")
    public String defaultError() {
        return "/error/default";
    }

    @GetMapping("/custom")
    public String customError(Model m) {
        return "/error/custom";
    }

}
