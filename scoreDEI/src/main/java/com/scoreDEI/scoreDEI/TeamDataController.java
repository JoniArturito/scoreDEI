package com.scoreDEI.scoreDEI;

import com.scoreDEI.Entities.Player;
import com.scoreDEI.Entities.Team;
import com.scoreDEI.Forms.TeamForm;
import com.scoreDEI.Services.TeamService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/register")
    public String registerTeamForm(Model model) {
        model.addAttribute("TeamForm", new TeamForm());
        return "/team/register";
    }

    @PostMapping("/register")
    public String registerTeamSubmit(@ModelAttribute TeamForm form, Model model) throws Exception {
        model.addAttribute("TeamForm", form);

        Team dbTeam = new Team(form.getName(), form.getMultipartFile().getBytes());
        this.teamService.addTeam(dbTeam);

        return "redirect:/team/list";
    }

    @GetMapping("/list")
    public String listTeams(Model model) {
        model.addAttribute("teams", this.teamService.getAllTeams());

        return "/team/list";
    }

    @GetMapping("/profile")
    public String teamProfile(@RequestParam(name="id", required = true) int id, Model model) {
        Optional<Team> t = this.teamService.getTeam(id);

        if(t.isPresent()) {
            List<Player> players = t.get().getPlayers();
            model.addAttribute("team", t.get());
            model.addAttribute("players", players);
            return "/team/profile";
        }

        return "redirect:/team/list";
    }

    @GetMapping("/logo")
    public void getTeamLogo(@RequestParam(name="id", required = true) int id, HttpServletResponse response) throws IOException {
        response.setContentType("image/png");

        Optional<Team> t = this.teamService.getTeam(id);

        if(t.isPresent()) {
            Team team = t.get();
            InputStream image = new ByteArrayInputStream(team.getLogo());
            IOUtils.copy(image, response.getOutputStream());
        }
    }

    @GetMapping("/edit/logo")
    public String updateTeamLogo(@RequestParam(name="id", required = true) int id, Model model) {
        Optional<Team> t = this.teamService.getTeam(id);

        if(t.isPresent()) {
            model.addAttribute("teamForm", new TeamForm());
            model.addAttribute("team", t.get());

            return "team/updateLogo";
        }

        return "redirect:/team/list";
    }

    @PostMapping("/edit/logo")
    public String updateTeamLogo(@RequestParam(name="id", required = true) int id, @ModelAttribute TeamForm form, Model model) throws IOException {
        model.addAttribute("teamForm", form);

        byte[] new_logo = form.getMultipartFile().getBytes();
        this.teamService.updateTeamLogo(id, new_logo);

        return "redirect:/team/list";
    }

    @GetMapping("/edit/name")
    public String updateTeamName(@RequestParam(name="id", required = true) int id, Model model) {
        Optional<Team> t = this.teamService.getTeam(id);

        if(t.isPresent()) {
            model.addAttribute("teamForm", new TeamForm());
            model.addAttribute("team", t.get());

            return "team/updateTeamName";
        }

        return "redirect:/team/list";
    }

    @PostMapping("/edit/name")
    public String updateTeamName(@RequestParam(name="id", required = true) int id, @ModelAttribute TeamForm form, Model model) throws IOException {
        model.addAttribute("teamForm", form);

        String name = form.getName();
        this.teamService.updateTeamName(id, name);

        return "redirect:/team/list";
    }
}
