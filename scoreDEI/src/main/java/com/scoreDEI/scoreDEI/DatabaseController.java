package com.scoreDEI.scoreDEI;

import com.scoreDEI.Entities.*;
import com.scoreDEI.Forms.*;
import com.scoreDEI.Others.PasswordHash;
import com.scoreDEI.Services.*;
import com.scoreDEI.filters.AuthenticationFilter;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
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

    @GetMapping("/login")
    public String login(Model m) {
        m.addAttribute("userForm", new UserForm());
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
        HttpSession session = request.getSession();
        session.setAttribute("user", null);

        return "redirect:/login";
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
        bean.addUrlPatterns("/team/*");
        bean.addUrlPatterns("/game/register");

        bean.setOrder(1);
        return bean;
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
                    new AdminUser("Joao", "ijklmnop@gmail.com", 123456788L, PasswordHash.toHexString(PasswordHash.getSha("ananas")))
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

    @GetMapping("/registerEvent")
    public String registerEventForm(Model model) {
        model.addAttribute("games", this.gameService.getAllGames());
        model.addAttribute("EventForm", new EventForm());
        return "registerEvent";
    }

    @PostMapping("/registerEvent")
    public String registerEventSubmit(@ModelAttribute EventForm form, Model model) {
        switch(form.getTypeEvent()) {
            case 0:
                //model.addAttribute("GameStatusForm", new GameStatusForm());
                return String.format("redirect:/registerGameStatus?name=%s&type=0", form.getGame());
            case 1:
                //model.addAttribute("GameStatusForm", new GameStatusForm(opGame.get(), 1));
                return String.format("redirect:/registerGameStatus?name=%s&type=1", form.getGame());
            case 2:
                //model.addAttribute("GoalForm", new GoalForm(opGame.get()));
                return String.format("redirect:/registerGoal?name=%s", form.getGame());
            case 3:
                //model.addAttribute("CardForm", new CardForm(opGame.get(), true));
                return String.format("redirect:/registerCard?name=%s&isYellow=true", form.getGame());
            case 4:
                //model.addAttribute("CardForm", new CardForm(opGame.get(), false));
                return String.format("redirect:/registerCard?name=%s&isYellow=false", form.getGame());
            case 5:
                //model.addAttribute("GameStatusForm", new GameStatusForm(opGame.get(), 2));
                return String.format("redirect:/registerGameStatus?name=%s&type=2", form.getGame());
            case 6:
                //model.addAttribute("GameStatusForm", new GameStatusForm(opGame.get(), 3));
                return String.format("redirect:/registerGameStatus?name=%s&type=3", form.getGame());
            default:
                break;
        }

        return "registerEvent";
    }

    /*@GetMapping("/registerGameStatus")
    public String registerGameStatusForm(Model model) {
        model.addAttribute("GameStatusForm", new GameStatusForm());
        return "registerGameStatus";
    }*/

    @GetMapping("/registerGameStatus")
    public String registerGameStatusForm(@RequestParam(name="name", required=true) String name, @RequestParam(name="type", required=true) int type, Model model) {
        System.out.println(name);
        System.out.println(type);

        Optional<Game> opGame = this.gameService.getGame(name);
        if (opGame.isPresent()) {
            System.out.println(opGame.get());
            model.addAttribute("GameStatusForm", new GameStatusForm(opGame.get(), type));
            return "registerGameStatus";
        }

        return "registerEvent";
    }

    @PostMapping(value = "/registerGameStatus")
    public String registerGameStatusSubmit(@ModelAttribute GameStatusForm form, Model model) {
        model.addAttribute("GameStatusForm", form);
        form.setGameId(Integer.parseInt(form.getGameIdString()));
        form.setType(Integer.parseInt(form.getTypeString()));
        Optional<Game> opGame = this.gameService.getGame(form.getGameId());
        if (opGame.isPresent()) {
            form.setGame(opGame.get());

            System.out.println(form.getEventDate());
            System.out.println(form.getGame());
            System.out.println(form.getType());
            //System.out.println(form.getEventDate());

            String newDateTimeLocal = (form.getEventDate().replace("T", " ")).concat(":00");
            Timestamp dateAndTime = Timestamp.valueOf(newDateTimeLocal);

            GameStatus dbGameStatus = new GameStatus(dateAndTime, form.getGame(), form.getType());
            this.eventService.addGameEvent(dbGameStatus);
        }

        return "redirect:/";
    }

    @GetMapping("/registerGoal")
    public String registerGoalForm(@RequestParam(name="name", required=true) String name, Model model) {
        Optional<Game> opGame = this.gameService.getGame(name);
        if (opGame.isPresent()) {
            System.out.println(opGame.get());
            model.addAttribute("GoalForm", new GoalForm(opGame.get()));
            return "registerGoal";
        }

        return "registerEvent";
    }

    @PostMapping("/registerGoal")
    public String registerGoalSubmit(@ModelAttribute GoalForm form, Model model) {
        model.addAttribute("GoalForm", form);
        Optional<Game> opGame = this.gameService.getGame(Integer.parseInt(form.getGameIdString()));
        if (opGame.isPresent()) {
            System.out.println(opGame.get());
            form.setGame(opGame.get());
            String newDateTimeLocal = (form.getBeginDate().replace("T", " ")).concat(":00");
            Timestamp dateAndTime = Timestamp.valueOf(newDateTimeLocal);

            Optional<Player> opPlayer = this.playerService.getPlayer(form.getPlayerName());
            if (opPlayer.isPresent()) {
                Goal dbGoal = new Goal(dateAndTime, form.getGame(), opPlayer.get());
                this.eventService.addGoalEvent(dbGoal);
            }
        }

        return "redirect:/";
    }

    @GetMapping("/registerCard")
    public String registerCardForm(@RequestParam(name="name", required=true) String name, @RequestParam(name="isYellow", required=true) boolean isYellow, Model model) {
        Optional<Game> opGame = this.gameService.getGame(name);
        if (opGame.isPresent()) {
            System.out.println(opGame.get());
            model.addAttribute("CardForm", new CardForm(opGame.get(), isYellow));
            return "registerCard";
        }
        return "registerEvent";
    }

    @PostMapping("/registerCard")
    public String registerCardSubmit(@ModelAttribute CardForm form, Model model) {
        model.addAttribute("CardForm", form);
        Optional<Game> opGame = this.gameService.getGame(Integer.parseInt(form.getGameIdString()));
        if (opGame.isPresent()) {
            form.setGame(opGame.get());
            form.setYellow(Boolean.parseBoolean(form.getIsYellowString()));
            String newDateTimeLocal = (form.getBeginDate().replace("T", " ")).concat(":00");
            Timestamp dateAndTime = Timestamp.valueOf(newDateTimeLocal);

            Optional<Player> opPlayer = this.playerService.getPlayer(form.getPlayerName());
            if (opPlayer.isPresent()) {
                Card dbCard = new Card(dateAndTime, form.getGame(), form.isYellow(), opPlayer.get());
                this.eventService.addCardEvent(dbCard);
            }
        }

        return "redirect:/";
    }
}
