package com.scoreDEI.scoreDEI;

import com.scoreDEI.Entities.Player;
import com.scoreDEI.Entities.Team;
import com.scoreDEI.Forms.PlayerForm;
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

    @GetMapping("/list")
    public String listTeams(Model model) {
        try {
            model.addAttribute("teams", this.teamService.getAllTeams());

            return "/player/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/profile")
    public String playerProfile(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Player> opP = this.playerService.getPlayer(id);

            if(opP.isPresent()) {
                Player op = opP.get();
                model.addAttribute("player", op);
                //model.addAttribute("team", op.getTeam().getName());
                return "/player/profile";
            }

            redirAttrs.addFlashAttribute("error", "Player does not exist!");
            return "redirect:/team/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

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

    @GetMapping("/delete")
    public String deletePlayer(@RequestParam(name = "id", required = true) int id, Model model) {
        try {
            model.addAttribute("id", id);
            return "player/delete";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

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
