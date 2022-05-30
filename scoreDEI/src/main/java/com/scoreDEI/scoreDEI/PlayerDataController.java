/**
 * This class is responsible for the player's data management
 */
package com.scoreDEI.scoreDEI;

import com.scoreDEI.Entities.Player;
import com.scoreDEI.Entities.Team;
import com.scoreDEI.Forms.PlayerForm;
import com.scoreDEI.Services.EventService;
import com.scoreDEI.Services.PlayerService;
import com.scoreDEI.Services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.sql.Date;
import java.util.Optional;

@Controller
@RequestMapping("/player")
public class PlayerDataController {
    @Autowired
    TeamService teamService;

    @Autowired
    PlayerService playerService;

    @Autowired
    EventService eventService;

    /**
     * This function is used to display the register player form
     *
     * @param model This is the model that will be passed to the view.
     * @return A string
     */
    @GetMapping("/register")
    public String registerPlayerForm(Model model) {
        try {
            model.addAttribute("teams", this.teamService.getAllTeams());
            model.addAttribute("PlayerForm", new PlayerForm());

            return "player/register";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It registers a player.
     *
     * @param form The form object that is bound to the form data.
     * @param model This is the model object that is used to pass data from the controller to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows you to add attributes to the redirect.
     * @return A redirect to the player list page.
     */
    @PostMapping("/register")
    public String registerPlayerSubmit(@ModelAttribute PlayerForm form, Model model, RedirectAttributes redirAttrs){
        try {
            model.addAttribute("PlayerForm", form);

            String playerName = form.getName();
            String playerPosition = form.getPosition();
            Date playerBirthday = form.getBirthday();
            Optional<Team> playerTeam = this.teamService.getTeam(form.getTeamName());

            if (playerTeam.isPresent()) {
                Player dbPlayer = new Player(playerName, playerPosition, playerBirthday, playerTeam.get());
                if (this.playerService.isPlayerExist(dbPlayer)){
                    redirAttrs.addFlashAttribute("error", "Failed to register player!");
                } else{
                    this.playerService.addPlayer(dbPlayer);
                    redirAttrs.addFlashAttribute("success", String.format("Player %s registered!", playerName));
                }

                return "redirect:/player/list";
            } else {
                redirAttrs.addFlashAttribute("error", "Failed to register player!");
                return "redirect:/player/list";
            }

        } catch (Exception e) {
            redirAttrs.addFlashAttribute("error", "Failed to register player!");
            return "redirect:/player/list";
        }
    }

    /**
     * This function is a GET request that adds a list of all teams in the database to the model
     *
     * @param model This is the model object that will be used to pass data to the view.
     * @return A list of all the teams in the database.
     */
    @GetMapping("/list")
    public String listTeams(Model model) {
        try {
            model.addAttribute("teams", this.teamService.getAllTeams());

            return "/player/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * The function takes in a player id, and returns the player's profile page
     *
     * @param id the id of the player
     * @param model This is the model object that is used to pass data from the controller to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows you to add attributes to the redirect.
     * @return A player profile page.
     */
    @GetMapping("/profile")
    public String playerProfile(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Player> opP = this.playerService.getPlayer(id);

            if(opP.isPresent()) {
                Player op = opP.get();
                model.addAttribute("player", op);
                model.addAttribute("statistic", eventService.getPlayerStatistic(op));
                model.addAttribute("teamName", playerService.getTeam(op.getPlayerId()));
                //model.addAttribute("team", op.getTeam().getName());
                return "/player/profile";
            }

            redirAttrs.addFlashAttribute("error", "Player does not exist!");
            return "redirect:/team/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It takes in a player id, and returns a form to update the player's name
     *
     * @param id the id of the player to be updated
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the next request.
     * @return A String
     */
    @GetMapping("/edit/name")
    public String updatePlayerName(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Player> p = this.playerService.getPlayer(id);

            if(p.isPresent()) {
                model.addAttribute("playerForm", new PlayerForm());
                model.addAttribute("player", p.get());

                return "player/updateName";
            }

            redirAttrs.addFlashAttribute("error", "Player does not exist!");
            return "redirect:/team/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It takes in a player id, a player form, a model, and redirect attributes, and then it updates the player's name, and
     * then it redirects to the player's profile page
     *
     * @param id the id of the player
     * @param form the form object that contains the data that the user has entered
     * @param model This is the model object that is used to pass data from the controller to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows you to add attributes to the redirect.
     * @return A string
     */
    @PostMapping("/edit/name")
    public String updatePlayerName(@RequestParam(name="id") int id,
                                   @ModelAttribute PlayerForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("playerForm", form);

            String name = form.getName();
            boolean feedback = this.playerService.updateName(id, name);
            if(feedback) {
                redirAttrs.addFlashAttribute("success", "Team logo changed!");
            } else redirAttrs.addFlashAttribute("error", "Failed to team logo!");

            return String.format("redirect:/player/profile?id=%d", id);
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * This function is used to update the position of a player
     *
     * @param id the id of the player to be updated
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirected page.
     * @return A string
     */
    @GetMapping("/edit/position")
    public String updatePosition(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Player> p = this.playerService.getPlayer(id);

            if(p.isPresent()) {
                model.addAttribute("playerForm", new PlayerForm());
                model.addAttribute("player", p.get());

                return "player/updatePosition";
            }

            redirAttrs.addFlashAttribute("error", "Player does not exist!");
            return "redirect:/team/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It takes in the id of the player, the form object, the model object, and the redirect attributes object. It then
     * updates the position of the player with the id and the position in the form object. It then redirects to the
     * player's profile page
     *
     * @param id the id of the player
     * @param form the form that contains the new position
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirected page.
     * @return A string
     */
    @PostMapping("/edit/position")
    public String updatePosition(@RequestParam(name="id") int id,
                                 @ModelAttribute PlayerForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("playerForm", form);

            String position = form.getPosition();
            boolean feedback = this.playerService.updatePosition(id, position);
            if(feedback) {
                redirAttrs.addFlashAttribute("success", "Player's position changed!");
            } else redirAttrs.addFlashAttribute("error", "Failed to player's position!");

            return String.format("redirect:/player/profile?id=%d", id);
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * This function is used to update the birthday of a player
     *
     * @param id the id of the player to be updated
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the next request.
     * @return A String
     */
    @GetMapping("/edit/birthday")
    public String updateBirthday(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Player> p = this.playerService.getPlayer(id);

            if(p.isPresent()) {
                model.addAttribute("playerForm", new PlayerForm());
                model.addAttribute("player", p.get());

                return "player/updateBirthday";
            }

            redirAttrs.addFlashAttribute("error", "Player does not exist!");
            return "redirect:/team/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It takes in a player's id, a PlayerForm object, a Model object, and a RedirectAttributes object, and it returns a
     * String
     *
     * @param id the id of the player to be updated
     * @param form the form that contains the new birthday
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirected page.
     * @return A string
     */
    @PostMapping("/edit/birthday")
    public String updateBirthday(@RequestParam(name="id") int id,
                                 @ModelAttribute PlayerForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("playerForm", form);

            Date birthday = form.getBirthday();
            boolean feedback = this.playerService.updateBirthday(id, birthday);
            if(feedback) {
                redirAttrs.addFlashAttribute("success", "Player's birthday changed!");
            } else redirAttrs.addFlashAttribute("error", "Failed to change player's birthday!");

            return String.format("redirect:/player/profile?id=%d", id);
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It takes in a player id, and then it returns a page that allows the user to update the player's team
     *
     * @param id the id of the player we want to update
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the next request.
     * @return A String
     */
    @GetMapping("/edit/team")
    public String updatePlayerTeam(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Player> p = this.playerService.getPlayer(id);

            if(p.isPresent()) {
                model.addAttribute("playerForm", new PlayerForm());
                model.addAttribute("player", p.get());
                model.addAttribute("teams", this.teamService.getAllTeams());

                return "player/updateTeam";
            }

            redirAttrs.addFlashAttribute("error", "Player does not exist!");
            return "redirect:/team/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It takes in a player id, a form object, a model object, and a redirectAttributes object. It then tries to get the
     * team from the form object, and if it exists, it tries to update the player's team. If it succeeds, it adds a success
     * message to the redirectAttributes object, and if it fails, it adds an error message to the redirectAttributes
     * object. It then redirects to the player's profile page
     *
     * @param id The id of the player to be updated
     * @param form The form that is used to update the player's team.
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirected page.
     * @return A string
     */
    @PostMapping("/edit/team")
    public String updatePlayerTeam(@RequestParam(name="id") int id,
                                   @ModelAttribute PlayerForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("playerForm", form);
            Optional<Team> playerTeam = this.teamService.getTeam(form.getTeamName());

            if(playerTeam.isPresent()) {

                boolean feedback = this.playerService.updateTeam(id, playerTeam.get());
                if(feedback) {
                    redirAttrs.addFlashAttribute("success", "Player's team changed!");
                } else redirAttrs.addFlashAttribute("error", "Failed to change player's team!");
            }

            redirAttrs.addFlashAttribute("error", "Player does not have a team!");
            return String.format("redirect:/player/profile?id=%d", id);
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * This function is called when the user clicks on the delete button on the player page. It takes the id of the player
     * as a parameter and returns the delete page
     *
     * @param id the id of the player to be deleted
     * @param model The model is an object that holds data that you want to pass to the view.
     * @return A string
     */
    @GetMapping("/delete")
    public String deletePlayer(@RequestParam(name = "id", required = true) int id, Model model) {
        try {
            model.addAttribute("id", id);
            return "player/delete";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * This function is called when the user clicks the delete button on the player list page. It takes the id of the
     * player to be deleted as a parameter, and then calls the deletePlayer function in the playerService. If the delete is
     * successful, the user is redirected to the player list page. If the delete is unsuccessful, the user is redirected to
     * the error page
     *
     * @param id the id of the player to be deleted
     * @param model This is the model object that is used to pass data from the controller to the view.
     * @return A string that redirects to the list of players.
     */
    @PostMapping("/delete")
    public String deletePlayerConfirm(@RequestParam(name = "id", required = true) int id, Model model){
        try{
            this.playerService.deletePlayer(id);
            return "redirect:/player/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }
}
