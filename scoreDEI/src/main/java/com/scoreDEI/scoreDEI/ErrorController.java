package com.scoreDEI.scoreDEI;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/error")
public class ErrorController {

    @GetMapping("/")
    public String defaultError() {
        return "/error/default";
    }

    @GetMapping("/team/register")
    public String teamRegistrationError(@RequestParam(name="name", required = true) String name, Model m) {
        m.addAttribute("name", name);
        return "/error/teamRegistration";
    }
}
