package com.scoreDEI.scoreDEI;

import com.scoreDEI.Entities.*;
import com.scoreDEI.Forms.*;
import com.scoreDEI.Others.PasswordHash;
import com.scoreDEI.Services.*;
import com.scoreDEI.filters.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
        try {
            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/error")
    public String error(){
        return "redirect:/error/";
    }

    @GetMapping("/login")
    public String login(Model m) {
        m.addAttribute("userForm", new UserForm());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute UserForm form) {
        try {
            String email = form.getEmail();
            String password = PasswordHash.toHexString(PasswordHash.getSha(form.getPassword()));

            Optional<User> user = userService.getUser(email, password);

            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                            .getRequest();
            HttpSession session = request.getSession();

            if(user.isPresent()) {
                session.setAttribute("user", user.get());
            } else {
                session.setAttribute("user", null);

            }

            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authFilter() {
        FilterRegistrationBean<AuthenticationFilter> bean = new FilterRegistrationBean<>();

        bean.setFilter(new AuthenticationFilter());

        bean.addUrlPatterns("/user/*");

        List<String> teamUrlPatterns = Arrays.asList("register", "edit/*", "delete");
        for (String urlPattern : teamUrlPatterns) {
            bean.addUrlPatterns(String.format("/team/%s", urlPattern));
        }

        List<String> gameUrlPatterns = Arrays.asList("register", "edit/*", "delete");
        for (String urlPattern : gameUrlPatterns) {
            bean.addUrlPatterns(String.format("/game/%s", urlPattern));
        }

        List<String> playerUrlPatterns = Arrays.asList("register", "edit/*", "delete");
        for (String urlPattern : playerUrlPatterns) {
            bean.addUrlPatterns(String.format("/player/%s", urlPattern));
        }

        bean.setOrder(1);
        return bean;
    }

    @GetMapping("/logout")
    public String logout() {

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
        HttpSession session = request.getSession();
        session.setAttribute("user", null);

        return "redirect:/game/list";
    }

    @GetMapping({"/createData"})
    public String createData() {
        try {
            return "createData";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/saveData")
    public String saveData(Model m) {
        try {
            AdminUser[] admins = {
                    new AdminUser("Rui", "abcdefgh@gmail.com", 123456789L, PasswordHash.toHexString(PasswordHash.getSha("bruh"))),
                    new AdminUser("Joao", "admin@mail.com", 123456788L, PasswordHash.toHexString(PasswordHash.getSha("123")))
            };
            RegularUser[] users = {
                    new RegularUser("Paula", "qrstuvwx@gmail.com", 111111111L, PasswordHash.toHexString(PasswordHash.getSha("1234"))),
                    new RegularUser("Artur", "yz@gmail.com", 222222222L, PasswordHash.toHexString(PasswordHash.getSha("1234"))),
                    new RegularUser("Luísa", "aaaa@gmail.com", 333333333L, PasswordHash.toHexString(PasswordHash.getSha("1234"))),
                    new RegularUser("Alexandra", "bbbb@gmail.com", 444444444L, PasswordHash.toHexString(PasswordHash.getSha("1234"))),
                    new RegularUser("Carlos", "cccc@gmail.com", 555555555L, PasswordHash.toHexString(PasswordHash.getSha("1234")))
            };

            for (AdminUser ad: admins)
                this.userService.addUser(ad);
            for (RegularUser us : users)
                this.userService.addUser(us);

            return "redirect:/";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }
}
