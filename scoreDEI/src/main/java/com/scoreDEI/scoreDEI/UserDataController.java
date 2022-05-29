package com.scoreDEI.scoreDEI;

import com.scoreDEI.Entities.*;
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

    @GetMapping("/profile")
    public String userProfile(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<User> user = this.userService.getUser(id);

            if(user.isPresent()) {
                model.addAttribute("user", user.get());
                return "/user/profile";
            }

            redirAttrs.addFlashAttribute("error", "User does not exist!");
            return "redirect:/user/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/edit/name")
    public String updateUsername(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<User> op = this.userService.getUser(id);
            model.addAttribute("editForm", new UserForm());

            if (op.isPresent()) {
                model.addAttribute("user", op.get());
                return "user/updateUsername";
            }

            redirAttrs.addFlashAttribute("error", "User does not exist!");
            return "redirect:/user/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }

    }

    @PostMapping("/edit/name")
    public String updateUsername(@RequestParam(name="id") int id,
                                 @ModelAttribute UserForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("editForm", form);

            String username = form.getUsername();
            boolean feedback = userService.updateUsername(id, username);
            if(feedback) {
                redirAttrs.addFlashAttribute("success", String.format("Username changed to %s!", username));
            } else redirAttrs.addFlashAttribute("error", "Failed to change username!");

            return String.format("redirect:/user/profile?id=%d", id);
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/edit/email")
    public String updateEmail(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<User> op = this.userService.getUser(id);
            model.addAttribute("editForm", new UserForm());

            if (op.isPresent()) {
                model.addAttribute("user", op.get());
                return "user/updateEmail";
            }

            redirAttrs.addFlashAttribute("error", "User does not exist!");
            return "redirect:/user/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/edit/email")
    public String updateEmail(@RequestParam(name="id") int id,
                              @ModelAttribute UserForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("editForm", form);

            String email = form.getUsername();
            boolean feedback = userService.updateEmail(id, email);
            if(feedback) {
                redirAttrs.addFlashAttribute("success", String.format("Email changed to %s!", email));
            } else redirAttrs.addFlashAttribute("error", "Failed to change username!");


            return String.format("redirect:/user/profile?id=%d", id);
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/edit/phone")
    public String updatePhone(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<User> op = this.userService.getUser(id);
            model.addAttribute("editForm", new UserForm());

            if (op.isPresent()) {
                model.addAttribute("user", op.get());
                return "user/updatePhone";
            }

            redirAttrs.addFlashAttribute("error", "User does not exist!");
            return "redirect:/user/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/edit/phone")
    public String updatePhone(@RequestParam(name="id") int id,
                              @ModelAttribute UserForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("editForm", form);

            long phone = form.getPhone();
            boolean feedback = userService.updatePhone(id, phone);
            if(feedback) {
                redirAttrs.addFlashAttribute("success", String.format("Phone changed to %d!", phone));
            } else redirAttrs.addFlashAttribute("error", "Failed to change phone number!");

            return String.format("redirect:/user/profile?id=%d", id);
        } catch (Exception e) {
            return "redirect:/error/";
        }

    }

    @GetMapping("/edit/password")
    public String updatePassword(@RequestParam(name="id") int id, Model model, RedirectAttributes redirAttrs) {
        try {
            Optional<User> op = this.userService.getUser(id);
            model.addAttribute("editForm", new UserForm());

            if (op.isPresent()) {
                model.addAttribute("user", op.get());
                return "user/updatePassword";
            }

            redirAttrs.addFlashAttribute("error", "User does not exist!");
            return "redirect:/user/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/edit/password")
    public String updatePassword(@RequestParam(name="id", required = true) int id,
                                 @ModelAttribute UserForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("editForm", form);

            long phone = form.getPhone();
            boolean feedback = userService.updatePhone(id, phone);
            if(feedback) {
                redirAttrs.addFlashAttribute("success", "Password changed!");
            } else redirAttrs.addFlashAttribute("error", "Failed to change password!");

            return String.format("redirect:/user/profile?id=%d", id);
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam(name = "id", required = true) int id, Model model) {
        try {
            model.addAttribute("id", id);
            return "user/delete";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    @PostMapping("/delete")
    public String deleteUserConfirm(@RequestParam(name = "id", required = true) int id, Model model){
        try{
            this.userService.deleteUser(id);
            return "redirect:/user/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

}
