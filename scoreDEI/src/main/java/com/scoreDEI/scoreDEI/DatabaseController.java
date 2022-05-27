package com.scoreDEI.scoreDEI;

import com.scoreDEI.Entities.*;
import com.scoreDEI.Forms.*;
import com.scoreDEI.Others.PasswordHash;
import com.scoreDEI.Services.*;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
        return "redirect:/user/list";
    }

    @GetMapping({"/createData"})
    public String createData() {
        return "createData";
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

        for (AdminUser ad: admins)
            this.userService.addUser(ad);
        for (RegularUser us : users)
            this.userService.addUser(us);

        return "redirect:/";
    }

    @GetMapping("/registerGame")
    public String registerGameForm(Model model) {
        model.addAttribute("teams", this.teamService.getAllTeams());
        model.addAttribute("Game", new GameForm());
        return "registerGame";
    }

    @PostMapping("/registerGame")
    public String registerGameSubmit(@ModelAttribute GameForm form, Model model) {
        model.addAttribute("GameForm", form);

        if (form.getHomeTeam().equals(form.getVisitorTeam())){
            System.out.println("Team cant play against itself");
        }
        else{
            Optional<Team> homeTeam = this.teamService.getTeam(form.getHomeTeam());
            Optional<Team> visitorTeam = this.teamService.getTeam(form.getVisitorTeam());
            String newDateTimeLocal = (form.getDateTime().replace("T", " ")).concat(":00");
            Timestamp dateAndTime = Timestamp.valueOf(newDateTimeLocal);
            String location = form.getLocation();

            if (homeTeam.isPresent() && visitorTeam.isPresent())
            {
                Team hTeam = homeTeam.get();
                Team vTeam = visitorTeam.get();
                Game dbGame = new Game(dateAndTime, location, hTeam, vTeam);
                this.gameService.addGame(dbGame);
            }
            else{
                System.out.println("At least one team isnt present");
            }
        }

        return "registerGame";
    }

    @GetMapping("/registerEvent")
    public String registerEventForm(Model model) {
        model.addAttribute("games", this.gameService.getAllGames());
        model.addAttribute("EventForm", new EventForm());
        return "registerEvent";
    }

    @PostMapping("/registerEvent")
    public String registerEventSubmit(@ModelAttribute EventForm form, Model model) {

        System.out.println(form.getGame());
        Optional<Game> opGame = this.gameService.getGame(form.getGame());

        if (opGame.isPresent()) {
            switch(form.getTypeEvent()) {
                case 0:
                    GameStatusForm toModel = new GameStatusForm(opGame.get(), 0);
                    model.addAttribute("GameStatusForm", toModel);
                    return "registerGameStatus";
                case 1:
                    model.addAttribute("GameStatusForm", new GameStatusForm(opGame.get(), 1));
                    return "registerGameStatus";
                case 2:
                    model.addAttribute("GoalForm", new GoalForm(opGame.get()));
                    return "registerGoal";
                case 3:
                    model.addAttribute("CardForm", new CardForm(opGame.get(), true));
                    return "registerGameStatus";
                case 4:
                    model.addAttribute("CardForm", new CardForm(opGame.get(), false));
                    return "registerGameStatus";
                case 5:
                    model.addAttribute("GameStatusForm", new GameStatusForm(opGame.get(), 2));
                    return "registerGameStatus";
                case 6:
                    model.addAttribute("GameStatusForm", new GameStatusForm(opGame.get(), 3));
                    return "registerGameStatus";
                default:
                    break;
            }
        }

        return "registerEvent";
    }

    /*@GetMapping("/registerGameStatus")
    public String registerGameStatusForm(@ModelAttribute GameStatusForm form, Model model) {
        model.addAttribute("GameStatusForm", form);
        return "registerGameStatus";
    }*/

    @PostMapping("/registerGameStatus")
    public String registerGameStatusSubmit(@ModelAttribute GameStatusForm form, Model model) {
        model.addAttribute("GameStatusForm", form);

        System.out.println(form.getGame());
        System.out.println(form.getType());
        System.out.println(form.getEventDate());

        String newDateTimeLocal = (form.getEventDate().replace("T", " ")).concat(":00");
        Timestamp dateAndTime = Timestamp.valueOf(newDateTimeLocal);

        GameStatus dbGameStatus = new GameStatus(dateAndTime, form.getGame(), form.getType());
        this.eventService.addGameEvent(dbGameStatus);

        return "redirect:/";
    }

    @GetMapping("/registerGoal")
    public String registerGoalForm(@ModelAttribute GoalForm form, Model model) {
        model.addAttribute("GoalForm", form);
        return "registerGoal";
    }

    @PostMapping("/registerGoal")
    public String registerGoalSubmit(@ModelAttribute GoalForm form, Model model) {
        String newDateTimeLocal = (form.getBeginDate().replace("T", " ")).concat(":00");
        Timestamp dateAndTime = Timestamp.valueOf(newDateTimeLocal);

        Optional<Player> opPlayer = this.playerService.getPlayer(form.getPlayerName());
        if (opPlayer.isPresent()) {
            Goal dbGoal = new Goal(dateAndTime, form.getGame(), opPlayer.get());
            this.eventService.addGoalEvent(dbGoal);
        }

        return "redirect:/";
    }

    @GetMapping("/registerCard")
    public String registerCardForm(@ModelAttribute CardForm form, Model model) {
        model.addAttribute("CardForm", form);
        return "registerCard";
    }

    @PostMapping("/registerCard")
    public String registerCardSubmit(@ModelAttribute CardForm form, Model model) {
        String newDateTimeLocal = (form.getBeginDate().replace("T", " ")).concat(":00");
        Timestamp dateAndTime = Timestamp.valueOf(newDateTimeLocal);

        Optional<Player> opPlayer = this.playerService.getPlayer(form.getPlayerName());
        if (opPlayer.isPresent()) {
            Card dbCard = new Card(dateAndTime, form.getGame(), form.isYellow(), opPlayer.get());
            this.eventService.addCardEvent(dbCard);
        }

        return "redirect:/";
    }
}
