package com.scoreDEI.scoreDEI;

import com.scoreDEI.Entities.AdminUser;
import com.scoreDEI.Entities.RegularUser;
import com.scoreDEI.Others.PasswordHash;
import com.scoreDEI.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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

    @PostMapping("/saveData")
    public String saveData(Model m) {
        AdminUser[] admins = {
                new AdminUser("Rui", "abcdefgh@gmail.com", 123456789L, PasswordHash.toHexString(PasswordHash.getSha("bruh"))),
                new AdminUser("Joao", "ijklmnop@gmail.com", 123456788L, PasswordHash.toHexString(PasswordHash.getSha("ananas")))
        };
        RegularUser[] users = {
                new RegularUser("Paula", "qrstuvwx@gmail.com", 111111111L, PasswordHash.toHexString(PasswordHash.getSha("1234"))),
                new RegularUser("Artur", "yz@gmail.com", 222222222L, PasswordHash.toHexString(PasswordHash.getSha("1234"))),
                new RegularUser("Luísa", "aaaa@gmail.com", 333333333L, PasswordHash.toHexString(PasswordHash.getSha("1234"))),
                new RegularUser("Alexandra", "bbbb@gmail.com", 444444444L, PasswordHash.toHexString(PasswordHash.getSha("1234"))),
                new RegularUser("Carlos", "cccc@gmail.com", 555555555L, PasswordHash.toHexString(PasswordHash.getSha("1234")))
        };

        for (RegularUser us : users)
            this.userService.addUser(us);
        for (AdminUser ad: admins)
            this.userService.addUser(ad);

        return "redirect:/listUsers";
    }

    @PostMapping("/addAdmin")
    public String addAdmin(Model m)
    {
        m.addAttribute("adminUser", new AdminUser());
        return "editAdmin";
    }

    @PostMapping("/addRegUser")
    public String addRegUser(Model m)
    {
        m.addAttribute("regularUser", new RegularUser());
        return "editRegUser";
    }
}
