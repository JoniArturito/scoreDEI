/**
 * This class is responsible for the game's data management
 */
package com.scoreDEI.scoreDEI;

import com.scoreDEI.Entities.Game;
import com.scoreDEI.Entities.GameEvent;
import com.scoreDEI.Entities.Team;
import com.scoreDEI.Forms.GameForm;
import com.scoreDEI.Services.EventService;
import com.scoreDEI.Services.GameService;
import com.scoreDEI.Services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/game")
public class GameDataController {
    @Autowired
    TeamService teamService;

    @Autowired
    GameService gameService;

    @Autowired
    EventService eventService;

    /**
     * This function is used to register a game
     *
     * @param model This is the model that will be passed to the view.
     * @return A string
     */
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

    /**
     * The function checks if the home team and visitor team aren't the same, if the stadium isn't occupied at the time of the
     * game, and if the teams exist. If all of these conditions are met, the game is created
     *
     * @param form The form that is used to create a new game.
     * @param model The model is a Map of objects that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirect.
     * @return A string
     */
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
                    redirAttrs.addFlashAttribute("error", "Failed to create game!");
                    return "redirect:/game/list";
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

    /**
     * This function gets a list of games from the game service and adds them to the model
     *
     * @param model This is the model that will be passed to the view.
     * @return A list of games
     */
    @GetMapping("/list")
    public String listGames(Model model) {
        try {
            model.addAttribute("games", this.gameService.getOrderedGames());

            return "/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It gets the game with the given id, and if it exists, it gets the events for that game, and then returns the game
     * profile page with the game and events
     *
     * @param id the id of the game to be displayed
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirect.
     * @return A game profile page
     */
    @GetMapping("/profile")
    public String gameProfile(@RequestParam(name="id", required=true) int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Game> p = this.gameService.getGame(id);

            if(p.isPresent()) {
                Game g = p.get();
                List<GameEvent> gameEvents = eventService.getChronologicEvents(g);
                model.addAttribute("game", g);
                model.addAttribute("events", gameEvents);
                model.addAttribute("score", gameService.getScore(g));
                model.addAttribute("beginGame", eventService.beginningGameExists(g));
                return "/game/profile";
            }

            redirAttrs.addFlashAttribute("error", "The game does not exist!");
            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * This function is used to update the home team of a game
     *
     * @param id The id of the game that we want to update.
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirect.
     * @return A String
     */
    @GetMapping("/edit/homeTeam")
    public String updateHomeTeam(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Game> game = this.gameService.getGame(id);

            if(game.isPresent()) {
                model.addAttribute("gameForm", new GameForm());
                model.addAttribute("game", game.get());
                model.addAttribute("teams", this.teamService.getAllTeams());

                if(eventService.beginningGameExists(game.get())) {
                    redirAttrs.addFlashAttribute("error", "The game has already begun!");
                    return "redirect:/game/list";
                }

                return "game/updateHomeTeam";
            }

            redirAttrs.addFlashAttribute("error", "The game does not exist!");
            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It updates the home team of a game
     *
     * @param id The id of the game to be updated.
     * @param form The form that is used to update the game.
     * @param model The model object that is used to pass data to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirected page.
     * @return A string
     */
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

    /**
     * This function is used to update the visitor team of a game
     *
     * @param id The id of the game that we want to update.
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirect.
     * @return A String
     */
    @GetMapping("/edit/visitorTeam")
    public String updateVisitorTeam(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Game> game = this.gameService.getGame(id);

            if(game.isPresent()) {
                model.addAttribute("gameForm", new GameForm());
                model.addAttribute("game", game.get());
                model.addAttribute("teams", this.teamService.getAllTeams());

                if(eventService.beginningGameExists(game.get())) {
                    redirAttrs.addFlashAttribute("error", "The game has already begun!");
                    return "redirect:/game/list";
                }

                return "game/updateVisitorTeam";
            }

            redirAttrs.addFlashAttribute("error", "The game does not exist!");
            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It updates the visitor team of a game
     *
     * @param id The id of the game to be updated.
     * @param form The form that is used to update the game.
     * @param model The model object that is used to pass data to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirected page.
     * @return A string
     */
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

    /**
     * This function is used to update the location of a game
     *
     * @param id The id of the game that we want to update.
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirect.
     * @return A string
     */
    @GetMapping("/edit/location")
    public String updateLocation(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Game> game = this.gameService.getGame(id);

            if(game.isPresent()) {
                model.addAttribute("gameForm", new GameForm());
                model.addAttribute("game", game.get());

                if(eventService.beginningGameExists(game.get())) {
                    redirAttrs.addFlashAttribute("error", "The game has already begun!");
                    return "redirect:/game/list";
                }

                return "game/updateLocation";
            }

            redirAttrs.addFlashAttribute("error", "The game does not exist!");
            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It takes in a game id, a GameForm object, a Model object, and a RedirectAttributes object, and it returns a String
     *
     * @param id The id of the game to be updated
     * @param form The form that is used to update the game.
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the next request.
     * @return A string
     */
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

    /**
     * This function is used to update the begin date of a game
     *
     * @param id The id of the game to be updated
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirect.
     * @return A String
     */
    @GetMapping("/edit/date")
    public String updateBeginDate(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Game> game = this.gameService.getGame(id);

            if(game.isPresent()) {
                model.addAttribute("gameForm", new GameForm());
                model.addAttribute("game", game.get());

                if(eventService.beginningGameExists(game.get())) {
                    redirAttrs.addFlashAttribute("error", "The game has already begun!");
                    return "redirect:/game/list";
                }

                return "game/updateDate";
            }

            redirAttrs.addFlashAttribute("error", "The game does not exist!");
            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It takes the id of a game, a GameForm object, a Model object, and a RedirectAttributes object. It then uses the
     * GameForm object to get the new date and time of the game, and then uses the GameService to update the game's date
     * and time
     *
     * @param id the id of the game to be updated
     * @param form The form that is used to update the game.
     * @param model The model is a map of values that will be passed to the view.
     * @param redirAttrs RedirectAttributes is a Spring class that allows you to add attributes to the redirect.
     * @return A redirect to the game profile page.
     */
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

    /**
     * This function is called when the user clicks the delete button on the game page. It takes the id of the game as a
     * parameter and returns the delete page
     *
     * @param id The id of the game to be deleted.
     * @param model The model is an object that holds data that you want to pass to the view.
     * @return A string
     */
    @GetMapping("/delete")
    public String deleteGame(@RequestParam(name = "id", required = true) int id, Model model) {
        try {
            model.addAttribute("id", id);
            return "game/delete";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * This function is called when the user clicks the delete button on the game list page. It takes the id of the game to
     * be deleted as a parameter and deletes the game from the database
     *
     * @param id the id of the game to be deleted
     * @param model This is the model that will be passed to the view.
     * @return A string that is the name of the view to be rendered.
     */
    @PostMapping("/delete")
    public String deleteGameConfirm(@RequestParam(name = "id", required = true) int id, Model model){
        try{
            this.gameService.deleteGame(id);
            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }
}
