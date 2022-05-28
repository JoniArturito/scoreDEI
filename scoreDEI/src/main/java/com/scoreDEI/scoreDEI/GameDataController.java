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
        model.addAttribute("teams", this.teamService.getAllTeams());
        model.addAttribute("Game", new GameForm());
        return "/game/register";
    }

    @PostMapping("/register")
    public String registerGameSubmit(@ModelAttribute GameForm form, Model model) {
        model.addAttribute("GameForm", form);

        if (form.getHomeTeam().equals(form.getVisitorTeam())){
            System.out.println("Team cant play against itself.");
        } else {
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
                System.out.println("At least one team isn't present.");
            }
        }

        return "redirect:/game/list";
    }

    @GetMapping("/list")
    public String listGames(Model model) {
        model.addAttribute("games", this.gameService.getAllGames());

        return "/game/list";
    }

    @GetMapping("/profile")
    public String playerProfile(@RequestParam(name="id", required=true) int id, Model model) {
        Optional<Game> p = this.gameService.getGame(id);

        if(p.isPresent()) {
            model.addAttribute("game", p.get());
            return "/game/profile";
        }

        return "redirect:/game/list";
    }
}
