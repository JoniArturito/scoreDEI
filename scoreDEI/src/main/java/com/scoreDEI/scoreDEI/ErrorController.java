package com.scoreDEI.scoreDEI;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * If the user tries to access the root of the application, they will be redirected to the default error page
 */
@Controller
@RequestMapping("/error")
public class ErrorController {

    /**
     * If the user tries to access the root of the application, they will be redirected to the default error page
     *
     * @return A String
     */
    @GetMapping("/")
    public String defaultError() {
        return "/error/default";
    }

}
