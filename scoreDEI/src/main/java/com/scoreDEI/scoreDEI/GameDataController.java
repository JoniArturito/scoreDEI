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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String registerGameSubmit(@ModelAttribute GameForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("GameForm", form);

            if (form.getHomeTeam().equals(form.getVisitorTeam())){
                redirAttrs.addFlashAttribute("error", "Failed to create game!");
                return "redirect:/game/list";
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

                    redirAttrs.addFlashAttribute("success", "Game created!");
                    return "redirect:/game/list";
                } else{
                    return "redirect:/error/";
                }
            }

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
    public String gameProfile(@RequestParam(name="id", required=true) int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Game> p = this.gameService.getGame(id);

            if(p.isPresent()) {
                model.addAttribute("game", p.get());
                return "/game/profile";
            }

            redirAttrs.addFlashAttribute("error", "The game does not exist!");
            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/edit/homeTeam")
    public String updateHomeTeam(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Game> game = this.gameService.getGame(id);

            if(game.isPresent()) {
                model.addAttribute("gameForm", new GameForm());
                model.addAttribute("game", game.get());
                model.addAttribute("teams", this.teamService.getAllTeams());

                return "game/updateHomeTeam";
            }

            redirAttrs.addFlashAttribute("error", "The game does not exist!");
            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/edit/homeTeam")
    public String updateHomeTeam(@RequestParam(name="id") int id,
                                 @ModelAttribute GameForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("gameForm", form);

            Optional<Team> homeTeam = this.teamService.getTeam(form.getHomeTeam());
            if(homeTeam.isPresent()) {
                boolean success = this.gameService.updateTeam(id, homeTeam.get(), true);
                if(success) {
                    redirAttrs.addFlashAttribute("success", String.format("Home team changed to %s!",
                            homeTeam.get().getName()));
                } else redirAttrs.addFlashAttribute("error", "Failed to change home team!");

                return String.format("redirect:/game/profile?id=%d", id);
            }

            redirAttrs.addFlashAttribute("error", "The game does not exist!");
            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/edit/visitorTeam")
    public String updateVisitorTeam(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Game> game = this.gameService.getGame(id);

            if(game.isPresent()) {
                model.addAttribute("gameForm", new GameForm());
                model.addAttribute("game", game.get());
                model.addAttribute("teams", this.teamService.getAllTeams());

                return "game/updateVisitorTeam";
            }

            redirAttrs.addFlashAttribute("error", "The game does not exist!");
            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/edit/visitorTeam")
    public String updateVisitorTeam(@RequestParam(name="id") int id,
                                 @ModelAttribute GameForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("gameForm", form);

            Optional<Team> team = this.teamService.getTeam(form.getVisitorTeam());
            if(team.isPresent()) {
                boolean success = this.gameService.updateTeam(id, team.get(), false);
                if(success) {
                    redirAttrs.addFlashAttribute("success", String.format("Visitor team changed to %s!",
                            team.get().getName()));
                } else redirAttrs.addFlashAttribute("error", "Failed to change home team!");

                return String.format("redirect:/game/profile?id=%d", id);
            }

            redirAttrs.addFlashAttribute("error", "The game does not exist!");
            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/edit/location")
    public String updateLocation(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Game> game = this.gameService.getGame(id);

            if(game.isPresent()) {
                model.addAttribute("gameForm", new GameForm());
                model.addAttribute("game", game.get());

                return "game/updateLocation";
            }

            redirAttrs.addFlashAttribute("error", "The game does not exist!");
            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/edit/location")
    public String updateLocation(@RequestParam(name="id") int id,
                                    @ModelAttribute GameForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("gameForm", form);

            Optional<Game> game = this.gameService.getGame(id);
            String location = form.getLocation();

            if(game.isPresent()) {
                boolean success = this.gameService.updateLocation(id, location);
                if(success) {
                    redirAttrs.addFlashAttribute("success", String.format("Game location changed to %s!",
                            location));
                } else redirAttrs.addFlashAttribute("error", "Failed to location!");

                return String.format("redirect:/game/profile?id=%d", id);
            }

            redirAttrs.addFlashAttribute("error", "The game does not exist!");
            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/edit/date")
    public String updateBeginDate(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Game> game = this.gameService.getGame(id);

            if(game.isPresent()) {
                model.addAttribute("gameForm", new GameForm());
                model.addAttribute("game", game.get());

                return "game/updateDate";
            }

            redirAttrs.addFlashAttribute("error", "The game does not exist!");
            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/edit/date")
    public String updateBeginDate(@RequestParam(name="id") int id,
                                 @ModelAttribute GameForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("gameForm", form);

            Optional<Game> game = this.gameService.getGame(id);
            String newDateTimeLocal = (form.getDateTime().replace("T", " ")).concat(":00");
            Timestamp beginDate = Timestamp.valueOf(newDateTimeLocal);

            if(game.isPresent()) {
                boolean success = this.gameService.updateDate(id, beginDate);
                if(success) {
                    redirAttrs.addFlashAttribute("success", String.format("Game begin changed to %s!",
                            newDateTimeLocal));
                } else redirAttrs.addFlashAttribute("error", "Failed to location!");

                return String.format("redirect:/game/profile?id=%d", id);
            }

            redirAttrs.addFlashAttribute("error", "The game does not exist!");
            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }
}
