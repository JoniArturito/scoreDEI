package com.scoreDEI.scoreDEI;

import com.scoreDEI.Entities.Game;
import com.scoreDEI.Entities.Player;
import com.scoreDEI.Entities.Team;
import com.scoreDEI.Forms.TeamForm;
import com.scoreDEI.Services.EventService;
import com.scoreDEI.Services.GameService;
import com.scoreDEI.Services.PlayerService;
import com.scoreDEI.Services.TeamService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/team")
public class TeamDataController {
    @Autowired
    TeamService teamService;

    @Autowired
    PlayerService playerService;

    @Autowired
    GameService gameService;

    @Autowired
    EventService eventService;

    @GetMapping("/register")
    public String registerTeamForm(Model model) {
        try {
            model.addAttribute("TeamForm", new TeamForm());
            return "/team/register";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/register")
    public String registerTeamSubmit(@ModelAttribute TeamForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("TeamForm", form);

            Team dbTeam = new Team(form.getName(), form.getMultipartFile().getBytes());
            this.teamService.addTeam(dbTeam);

            redirAttrs.addFlashAttribute("success", String.format("Team %s registered!", dbTeam.getName()));
            return "redirect:/team/list";
        } catch (Exception e) {
            redirAttrs.addFlashAttribute("error", "Failed to register team!");
            return "redirect:/team/list";
        }
    }

    @GetMapping("/list")
    public String listTeams(Model model) {
        try {
            Optional<Player> opScorer = this.playerService.getBestScorer();
            if (opScorer.isPresent()) {
                Player scorer = opScorer.get();
                model.addAttribute("Eusebio", scorer);
            }
            else{
                model.addAttribute("Eusebio", null);
            }
            model.addAttribute("teams", this.teamService.getOrderedTeams());
            return "/team/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/profile")
    public String teamProfile(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Team> t = this.teamService.getTeam(id);

            if(t.isPresent()) {
                Team proT = t.get();
                List<Player> players = proT.getPlayers();
                model.addAttribute("team", proT);
                model.addAttribute("players", players);
                return "/team/profile";
            }

            redirAttrs.addFlashAttribute("error", "Team does not exist!");
            return "redirect:/team/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/logo")
    public String getTeamLogo(@RequestParam(name="id") int id, HttpServletResponse response, RedirectAttributes redirAttrs) {
        try {
            response.setContentType("image/png");

            Optional<Team> t = this.teamService.getTeam(id);

            if(t.isPresent()) {
                Team team = t.get();
                InputStream image = new ByteArrayInputStream(team.getLogo());
                IOUtils.copy(image, response.getOutputStream());
                return null;
            }

            redirAttrs.addFlashAttribute("error", "Team does not exist!");
            return "redirect:/team/list";
        } catch (IOException e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/edit/logo")
    public String updateTeamLogo(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Team> t = this.teamService.getTeam(id);

            if(t.isPresent()) {
                model.addAttribute("teamForm", new TeamForm());
                model.addAttribute("team", t.get());

                return "team/updateLogo";
            }

            redirAttrs.addFlashAttribute("error", "Team does not exist!");
            return "redirect:/team/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/edit/logo")
    public String updateTeamLogo(@RequestParam(name="id") int id,
                                 @ModelAttribute TeamForm form, Model model, RedirectAttributes redirAttrs){

        try {
            model.addAttribute("teamForm", form);

            byte[] new_logo = form.getMultipartFile().getBytes();
            boolean feedback = this.teamService.updateTeamLogo(id, new_logo);
            if(feedback) {
                redirAttrs.addFlashAttribute("success", "Team logo changed!");
            } else redirAttrs.addFlashAttribute("error", "Failed to team logo!");

            return String.format("redirect:/team/profile?id=%d", id);
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/edit/name")
    public String updateTeamName(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<Team> t = this.teamService.getTeam(id);

            if(t.isPresent()) {
                model.addAttribute("teamForm", new TeamForm());
                model.addAttribute("team", t.get());

                return "team/updateTeamName";
            }

            redirAttrs.addFlashAttribute("error", "Team does not exist!");
            return "redirect:/team/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/edit/name")
    public String updateTeamName(@RequestParam(name="id") int id,
                                 @ModelAttribute TeamForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("teamForm", form);

            String name = form.getName();
            boolean feedback = this.teamService.updateTeamName(id, name);
            if(feedback) {
                redirAttrs.addFlashAttribute("success", String.format("Team name changed to %s!", name));
            } else redirAttrs.addFlashAttribute("error", "Failed to change team name!");

            return String.format("redirect:/team/profile?id=%d", id);
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/delete")
    public String deleteTeam(@RequestParam(name = "id", required = true) int id, Model model) {
        try {
            model.addAttribute("id", id);
            return "team/delete";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/delete")
    public String deleteTeamConfirm(@RequestParam(name = "id", required = true) int id, Model model){
        try{
            this.teamService.deleteTeam(id);
            return "redirect:/player/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }
}
