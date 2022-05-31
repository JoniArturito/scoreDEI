/**
 * This class is responsible for the team's data management
 */
package com.scoreDEI.scoreDEI;

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

    /**
     * This function is used to display the registration form for a new team
     *
     * @param model The model is a Map that is used to store the data that will be displayed on the view page.
     * @return A string
     */
    @GetMapping("/register")
    public String registerTeamForm(Model model) {
        try {
            model.addAttribute("TeamForm", new TeamForm());
            return "/team/register";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It takes a TeamForm object, converts it to a Team object, and saves it to the database
     *
     * @param form The form object that will be used to populate the model.
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the next request.
     * @return A redirect to the list page.
     */
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

    /**
     * It gets the best scorer and if it's present, it adds it to the model, otherwise it adds null
     * The list of teams is also added to the model to be displayed
     *
     * @param model The model is a Map that is used to pass data to the view.
     * @return A list of teams ordered by points.
     */
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

    /**
     * This function is used to display the profile of a team
     *
     * @param id The id of the team to be displayed
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirected page.
     * @return A team profile page.
     */
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

    /**
     * "Get the logo of the team with the given id and return it as a response."
     *
     * The first thing we do is set the content type of the response to "image/png". This is the content type of the image
     * we're going to return
     *
     * @param id The id of the team to get the logo for
     * @param response The response object that will be used to send the image to the client.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirected page.
     * @return A redirect to the error page.
     */
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

    /**
     * This function is used to update the logo of a team
     *
     * @param id The id of the team to be updated
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirected page.
     * @return A String
     */
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

    /**
     * It takes the id of the team, the form object, the model object and the redirect attributes object as parameters. It
     * then tries to update the team logo with the new logo from the form object. If it succeeds, it redirects to the team
     * profile page with a success message. If it fails, it redirects to the team profile page with an error message
     *
     * @param id the id of the team to be updated
     * @param form the form object that contains the multipart file
     * @param model This is the model object that is used to pass data from the controller to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirected page.
     * @return A redirect to the team profile page.
     */
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

    /**
     * This function is used to update the name of a team
     *
     * @param id The id of the team to be updated
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirect.
     * @return A String
     */
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

    /**
     * It takes in a team id, a team form, a model, and redirect attributes, and then it updates the team name, and then it
     * redirects to the team profile page
     *
     * @param id the id of the team to be updated
     * @param form the form object that contains the new name
     * @param model The model object that is used to pass data to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirected page.
     * @return A string
     */
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

    /**
     * This function is used to delete a team
     *
     * @param id the id of the team to be deleted
     * @param model The model is an object that holds the data that you want to pass to the view.
     * @return A string
     */
    @GetMapping("/delete")
    public String deleteTeam(@RequestParam(name = "id", required = true) int id, Model model) {
        try {
            model.addAttribute("id", id);
            return "team/delete";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * This function deletes a team from the database
     *
     * @param id the id of the team to be deleted
     * @param model This is the model object that is used to pass data from the controller to the view.
     * @return A string
     */
    @PostMapping("/delete")
    public String deleteTeamConfirm(@RequestParam(name = "id", required = true) int id, Model model){
        try{
            this.teamService.deleteTeam(id);
            return "redirect:/team/list";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "redirect:/error/";
        }
    }
}
