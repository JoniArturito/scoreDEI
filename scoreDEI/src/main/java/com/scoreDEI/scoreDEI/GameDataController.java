package com.scoreDEI.scoreDEI;

import com.scoreDEI.Entities.Game;
import com.scoreDEI.Entities.Team;
import com.scoreDEI.Forms.GameForm;
import com.scoreDEI.Services.GameService;
import com.scoreDEI.Services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Optional;

@Controller
@RequestMapping("/game")
public class GameDataController {
    @Autowired
    TeamService teamService;

    @Autowired
    GameService gameService;

    @GetMapping("/register")
    public String registerGameForm(Model model) {
        try {
            model.addAttribute("teams", this.teamService.getAllTeams());
            model.addAttribute("Game", new GameForm());

            return "/game/register";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/register")
    public String registerGameSubmit(@ModelAttribute GameForm form, Model model) {
        try {
            model.addAttribute("GameForm", form);

            if (form.getHomeTeam().equals(form.getVisitorTeam())){
                return "redirect:/error/";
            } else {
                Optional<Team> homeTeam = this.teamService.getTeam(form.getHomeTeam());
                Optional<Team> visitorTeam = this.teamService.getTeam(form.getVisitorTeam());
                String newDateTimeLocal = (form.getDateTime().replace("T", " ")).concat(":00");
                Timestamp dateAndTime = Timestamp.valueOf(newDateTimeLocal);
                String location = form.getLocation();

                if (this.gameService.isStadiumOccupied(location, dateAndTime)){
                    System.out.println("Stadium is already reserved");
                    return "redirect:/error/";
                }

                if (homeTeam.isPresent() && visitorTeam.isPresent()) {
                    Team hTeam = homeTeam.get();
                    Team vTeam = visitorTeam.get();
                    Game dbGame = new Game(dateAndTime, location, hTeam, vTeam);
                    this.gameService.addGame(dbGame);
                }
                else{
                    return "redirect:/error/";
                }
            }

            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/list")
    public String listGames(Model model) {
        try {
            model.addAttribute("games", this.gameService.getAllGames());

            return "/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/profile")
    public String playerProfile(@RequestParam(name="id", required=true) int id, Model model) {
        try {
            Optional<Game> p = this.gameService.getGame(id);

            if(p.isPresent()) {
                model.addAttribute("game", p.get());
                return "/game/profile";
            }

            return "redirect:/error/";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }
}
