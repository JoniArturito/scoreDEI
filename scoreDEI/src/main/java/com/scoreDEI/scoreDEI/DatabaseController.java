package com.scoreDEI.scoreDEI;

import com.scoreDEI.Entities.*;
import com.scoreDEI.Forms.GameForm;
import com.scoreDEI.Forms.PlayerForm;
import com.scoreDEI.Forms.UserForm;
import com.scoreDEI.Forms.TeamForm;
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

    @GetMapping("/registerTeam")
    public String registerTeamForm(Model model) {
        model.addAttribute("TeamForm", new TeamForm());
        return "registerTeam";
    }

    @PostMapping("/registerTeam")
    public String registerTeamSubmit(@ModelAttribute TeamForm form, Model model) throws Exception {
        model.addAttribute("TeamForm", form);

        Team dbTeam = new Team(form.getName(), form.getMultipartFile().getBytes());
        this.teamService.addTeam(dbTeam);

        return "redirect:/listTeams";
    }

    @GetMapping("/listTeams")
    public String listTeams(Model model) {
        model.addAttribute("teams", this.teamService.getAllTeams());

        return "/team/listTeams";
    }

    @GetMapping("/team/logo")
    public void getTeamLogo(@RequestParam(name="id", required = true) int id, HttpServletResponse response) throws IOException {
        response.setContentType("image/png");

        Optional<Team> t = this.teamService.getTeam(id);

        if(t.isPresent()) {
            Team team = t.get();
            InputStream image = new ByteArrayInputStream(team.getLogo());
            IOUtils.copy(image, response.getOutputStream());
        }
    }

    @GetMapping("/team/edit/logo")
    public String updateTeamLogo(@RequestParam(name="id", required = true) int id, Model model) {
        Optional<Team> t = this.teamService.getTeam(id);

        if(t.isPresent()) {
            model.addAttribute("teamForm", new TeamForm());
            model.addAttribute("team", t.get());

            return "team/updateLogo";
        }

        return "redirect:/listTeams";
    }

    @PostMapping("/team/edit/logo")
    public String updateTeamLogo(@RequestParam(name="id", required = true) int id, @ModelAttribute TeamForm form, Model model) throws IOException {
        model.addAttribute("teamForm", form);

        byte[] new_logo = form.getMultipartFile().getBytes();
        this.teamService.updateTeamLogo(id, new_logo);

        return "redirect:/listTeams";
    }

    @GetMapping("/team/edit/name")
    public String updateTeamName(@RequestParam(name="id", required = true) int id, Model model) {
        Optional<Team> t = this.teamService.getTeam(id);

        if(t.isPresent()) {
            model.addAttribute("teamForm", new TeamForm());
            model.addAttribute("team", t.get());

            return "team/updateTeamName";
        }

        return "redirect:/listTeams";
    }

    @PostMapping("/team/edit/name")
    public String updateTeamName(@RequestParam(name="id", required = true) int id, @ModelAttribute TeamForm form, Model model) throws IOException {
        model.addAttribute("teamForm", form);

        String name = form.getName();
        this.teamService.updateTeamName(id, name);

        return "redirect:/listTeams";
    }

    @GetMapping("/registerPlayer")
    public String registerPlayerForm(Model model) {
        model.addAttribute("teams", this.teamService.getAllTeams());
        model.addAttribute("PlayerForm", new PlayerForm());
        return "registerPlayer";
    }

    @PostMapping("/registerPlayer")
    public String registerPlayerSubmit(@ModelAttribute PlayerForm form, Model model){
        model.addAttribute("PlayerForm", form);

        String playerName = form.getName();
        String playerPosition = form.getPosition();
        Date playerBirthday = form.getBirthday();
        Optional<Team> playerTeam = this.teamService.getTeam(form.getTeamName());

        if (playerTeam.isPresent()) {
            Player dbPlayer = new Player(playerName, playerPosition, playerBirthday, playerTeam.get());
            this.playerService.addPlayer(dbPlayer);
        }
        else{
            System.out.println("Team not found");
        }

        return "registerPlayer";
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
            /*
            System.out.println(form.getHomeTeam());
            System.out.println(form.getVisitorTeam());
            System.out.println(form.getDateTime());
            System.out.println(form.getLocation());
            */
            Optional<Team> homeTeam = this.teamService.getTeam(form.getHomeTeam());
            Optional<Team> visitorTeam = this.teamService.getTeam(form.getVisitorTeam());
            String newDateTimeLocal = (form.getDateTime().replace("T", " ")).concat(":00");
            Timestamp dateAndTime = Timestamp.valueOf(newDateTimeLocal);
            String location = form.getLocation();

            if (homeTeam.isPresent() && visitorTeam.isPresent())
            {
                Team hTeam = homeTeam.get();
                Team vTeam = visitorTeam.get();
                ArrayList<Team> teams = new ArrayList<>();
                teams.add(hTeam);
                teams.add(vTeam);
                Game dbGame = new Game(dateAndTime, location);
                dbGame.setTeams(teams);
                hTeam.addGame(dbGame);
                vTeam.addGame(dbGame);
                this.gameService.addGame(dbGame);
                this.teamService.addTeam(hTeam);
                this.teamService.addTeam(vTeam);
            }
            else{
                System.out.println("At least one team isnt present");
            }
        }

        return "registerGame";
    }
}
