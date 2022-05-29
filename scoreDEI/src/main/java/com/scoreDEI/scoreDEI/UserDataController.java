package com.scoreDEI.scoreDEI;

import com.scoreDEI.Entities.AdminUser;
import com.scoreDEI.Entities.RegularUser;
import com.scoreDEI.Entities.User;
import com.scoreDEI.Forms.UserForm;
import com.scoreDEI.Others.PasswordHash;
import com.scoreDEI.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserDataController {
    @Autowired
    UserService userService;

    @GetMapping("/register")
    public String registerUserForm(Model model) {

        try {
            model.addAttribute("registerForm", new UserForm());

            return "/user/register";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/register")
    public String registerUserSubmit(@ModelAttribute UserForm form, Model model, RedirectAttributes redirAttrs) {

        try {
            model.addAttribute("registerForm", form);

            String username = form.getUsername();
            String password = PasswordHash.toHexString(PasswordHash.getSha(form.getPassword()));
            String email = form.getEmail();
            long phone = form.getPhone();

            if(form.isAdmin_role()) {
                AdminUser user = new AdminUser(username, email, phone, password);
                this.userService.addUser(user);
            } else {
                RegularUser user = new RegularUser(username, email, phone, password);
                this.userService.addUser(user);
            }

            redirAttrs.addFlashAttribute("success", String.format("User %s registered!", username));
            return "redirect:/user/list";
        } catch (Exception e) {
            redirAttrs.addFlashAttribute("error", "Failed to register user!");
            return "redirect:/user/list";
        }
    }

    @GetMapping("/list")
    public String listUsers(Model m) {
        try {
            m.addAttribute("users", this.userService.getAllUsers());
            m.addAttribute("admins", this.userService.getAllAdmins());
            m.addAttribute("regular_users", this.userService.getAllRegUsers());

            return "/user/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/edit/name")
    public String updateUsername(@RequestParam(name="id", required = true) int id, Model model) {
        try {
            Optional<User> op = this.userService.getUser(id);
            model.addAttribute("editForm", new UserForm());

            if (op.isPresent()) {
                model.addAttribute("user", op.get());
                return "user/updateUsername";
            }

            return "redirect:/error/";
        } catch (Exception e) {
            return "redirect:/error/";
        }

    }

    @PostMapping("/edit/name")
    public String updateUsername(@RequestParam(name="id", required = true) int id,
                                 @ModelAttribute UserForm form, Model model) {
        try {
            model.addAttribute("editForm", form);

            String username = form.getUsername();
            userService.updateUsername(id, username);

            return "redirect:/user/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/edit/email")
    public String updateEmail(@RequestParam(name="id", required = true) int id, Model model) {
        try {
            Optional<User> op = this.userService.getUser(id);
            model.addAttribute("editForm", new UserForm());

            if (op.isPresent()) {
                model.addAttribute("user", op.get());
                return "user/updatePassword";
            }

            return "redirect:/error/";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/edit/email")
    public String updateEmail(@RequestParam(name="id", required = true) int id,
                              @ModelAttribute UserForm form, Model model) {
        try {
            model.addAttribute("editForm", form);

            String email = form.getUsername();
            userService.updateEmail(id, email);

            return "redirect:/user/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/edit/phone")
    public String updatePhone(@RequestParam(name="id", required = true) int id, Model model) {
        try {
            Optional<User> op = this.userService.getUser(id);
            model.addAttribute("editForm", new UserForm());

            if (op.isPresent()) {
                model.addAttribute("user", op.get());
                return "user/updatePhone";
            }

            return "redirect:/error/";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/edit/phone")
    public String updatePhone(@RequestParam(name="id", required = true) int id,
                              @ModelAttribute UserForm form, Model model) {
        try {
            model.addAttribute("editForm", form);

            long phone = form.getPhone();
            userService.updatePhone(id, phone);

            return "redirect:/user/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }

    }

    @GetMapping("/edit/password")
    public String updatePassword(@RequestParam(name="id", required = true) int id, Model model) {
        try {
            Optional<User> op = this.userService.getUser(id);
            model.addAttribute("editForm", new UserForm());

            if (op.isPresent()) {
                model.addAttribute("user", op.get());
                return "user/updatePassword";
            }

            return "redirect:/error/";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/edit/password")
    public String updatePassword(@RequestParam(name="id", required = true) int id,
                                 @ModelAttribute UserForm form, Model model) {
        try {
            model.addAttribute("editForm", form);

            long phone = form.getPhone();
            userService.updatePhone(id, phone);

            return "redirect:/user/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

}
