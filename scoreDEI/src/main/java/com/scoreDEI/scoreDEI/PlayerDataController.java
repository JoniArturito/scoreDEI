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
    public String registerPlayerSubmit(@ModelAttribute PlayerForm form, Model model){
        try {
            model.addAttribute("PlayerForm", form);

            String playerName = form.getName();
            String playerPosition = form.getPosition();
            Date playerBirthday = form.getBirthday();
            Optional<Team> playerTeam = this.teamService.getTeam(form.getTeamName());

            if (playerTeam.isPresent()) {
                Player dbPlayer = new Player(playerName, playerPosition, playerBirthday, playerTeam.get());
                if (this.playerService.isPlayerExist(dbPlayer)){
                    System.out.println("Already exists");
                    return "redirect:/error/";
                }
                else{
                    this.playerService.addPlayer(dbPlayer);
                }
            } else {
                return "redirect:/error/";
            }

            return "redirect:/player/list";
        } catch (Exception e) {
            return "redirect:/error/";
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
    public String playerProfile(@RequestParam(name="id") int id, Model model) {
        try {
            Optional<Player> p = this.playerService.getPlayer(id);

            if(p.isPresent()) {
                model.addAttribute("player", p.get());
                return "/player/profile";
            }

            return "redirect:/error/";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/edit/name")
    public String updatePlayerName(@RequestParam(name="id") int id, Model model) {
        try {
            Optional<Player> p = this.playerService.getPlayer(id);

            if(p.isPresent()) {
                model.addAttribute("playerForm", new PlayerForm());
                model.addAttribute("player", p.get());

                return "player/updateName";
            }

            return "redirect:/error/";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/edit/name")
    public String updatePlayerName(@RequestParam(name="id") int id,
                                   @ModelAttribute PlayerForm form, Model model) {
        try {
            model.addAttribute("playerForm", form);

            String name = form.getName();
            this.playerService.updateName(id, name);

            return "redirect:/player/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/edit/position")
    public String updatePosition(@RequestParam(name="id") int id, Model model) {
        try {
            Optional<Player> p = this.playerService.getPlayer(id);

            if(p.isPresent()) {
                model.addAttribute("playerForm", new PlayerForm());
                model.addAttribute("player", p.get());

                return "player/updatePosition";
            }

            return "redirect:/error/";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/edit/position")
    public String updatePosition(@RequestParam(name="id") int id,
                                 @ModelAttribute PlayerForm form, Model model) {
        try {
            model.addAttribute("playerForm", form);

            String position = form.getPosition();
            this.playerService.updatePosition(id, position);

            return "redirect:/player/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/edit/birthday")
    public String updateBirthday(@RequestParam(name="id") int id, Model model) {
        try {
            Optional<Player> p = this.playerService.getPlayer(id);

            if(p.isPresent()) {
                model.addAttribute("playerForm", new PlayerForm());
                model.addAttribute("player", p.get());

                return "player/updateBirthday";
            }

            return "redirect:/error/";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/edit/birthday")
    public String updateBirthday(@RequestParam(name="id") int id,
                                 @ModelAttribute PlayerForm form, Model model) {
        try {
            model.addAttribute("playerForm", form);

            Date birthday = form.getBirthday();
            this.playerService.updateBirthday(id, birthday);

            return "redirect:/player/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/edit/team")
    public String updatePlayerTeam(@RequestParam(name="id") int id, Model model) {
        try {
            Optional<Player> p = this.playerService.getPlayer(id);

            if(p.isPresent()) {
                model.addAttribute("playerForm", new PlayerForm());
                model.addAttribute("player", p.get());
                model.addAttribute("teams", this.teamService.getAllTeams());

                return "player/updateTeam";
            }

            return "redirect:/error/";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/edit/team")
    public String updatePlayerTeam(@RequestParam(name="id") int id,
                                   @ModelAttribute PlayerForm form, Model model) {
        try {
            model.addAttribute("playerForm", form);
            Optional<Team> playerTeam = this.teamService.getTeam(form.getTeamName());
            playerTeam.ifPresent(team -> this.playerService.updateTeam(id, team));

            return "redirect:/player/list";
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
