package com.scoreDEI.scoreDEI;

import com.scoreDEI.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DatabaseController {
    @Autowired
    UserService userService;

    @Autowired
    TeamService teamService;

    @Autowired
    PlayerService playerService;

    @Autowired
    GameService gameService;

    @Autowired
    EventService eventService;

    @GetMapping("/")
    public String redirect(){
        return "redirect:/listUsers";
    }

    @GetMapping("/listUsers")
    public String listUsers(Model m)
    {
        m.addAttribute("users", this.userService.getAllUsers());
        return "listUsers";
    }
}
