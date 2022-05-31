/**
 * This class is responsible for the user's data management
 */
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

    /**
     * This function is used to display the register form to the user
     *
     * @param model This is the model object that will be used to pass data to the view.
     * @return A string
     */
    @GetMapping("/register")
    public String registerUserForm(Model model) {

        try {
            model.addAttribute("registerForm", new UserForm());

            return "/user/register";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It takes in a UserForm object, which is a form that contains the user's username, password, email, and phone number.
     * It then creates a new user object, and adds it to the database
     *
     * @param form The form object that is bound to the form in the view.
     * @param model This is the model object that is used to pass data from the controller to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows you to add attributes to the redirect.
     * @return A string
     */
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

    /**
     * This function is used to list all the users in the database
     *
     * @param m Model
     * @return A list of all users, admins, and regular users.
     */
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

    /**
     * If the user exists, return the user profile page, otherwise return the user list page with an error message
     *
     * @param id The id of the user to be displayed
     * @param model This is the model object that will be used to pass data to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirected page.
     * @return A user profile page.
     */
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

    /**
     * It takes in an id, and a model, and a redirectAttributes object, and it returns a string
     *
     * @param id the id of the user to be updated
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows you to add attributes to the redirect.
     * @return A String
     */
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

    /**
     * It takes in a user id, a form object, a model object, and a redirect attribute object. It then uses the user id to
     * update the username in the database, and then redirects the user to the profile page with a success or error message
     *
     * @param id the id of the user to be updated
     * @param form the form object that contains the new username
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows us to pass attributes to the redirected page.
     * @return A string
     */
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

    /**
     * It takes in an id, and a model, and a redirectAttributes object, and it returns a string
     *
     * @param id The id of the user to be updated
     * @param model This is the model object that will be used to pass data to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows you to pass attributes to the next request.
     * @return A string
     */
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

    /**
     * It takes in a user id, a UserForm object, a Model object, and a RedirectAttributes object. It then updates the
     * user's email with the email in the UserForm object, and returns a redirect to the user's profile page
     *
     * @param id the id of the user
     * @param form the form that the user fills out to change their email
     * @param model This is the model that will be passed to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows you to add attributes to the redirect.
     * @return A string
     */
    @PostMapping("/edit/email")
    public String updateEmail(@RequestParam(name="id") int id,
                              @ModelAttribute UserForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("editForm", form);

            String email = form.getEmail();
            System.out.println(email);
            boolean feedback = userService.updateEmail(id, email);
            if(feedback) {
                redirAttrs.addFlashAttribute("success", String.format("Email changed to %s!", email));
            } else redirAttrs.addFlashAttribute("error", "Failed to change username!");


            return String.format("redirect:/user/profile?id=%d", id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "redirect:/error/";
        }
    }

    /**
     * It takes in an id, and a model, and a redirectAttributes object, and it returns a string
     *
     * @param id the id of the user to be updated
     * @param model This is the model object that will be used to pass data to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows you to pass attributes to the next request.
     * @return A string
     */
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
            System.out.println(e.getMessage());
            return "redirect:/error/";
        }
    }

    /**
     * It takes in a user id, a UserForm object, a Model object, and a RedirectAttributes object, and returns a String
     *
     * @param id the id of the user
     * @param form the form that contains the new phone number
     * @param model the model object that is used to pass data to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows you to add attributes to the redirect.
     * @return A string
     */
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

    /**
     * It gets the user with the id passed in the request parameter, and if the user exists, it returns the updatePassword
     * view with the user object
     *
     * @param id The id of the user to be edited.
     * @param model This is the model object that will be used to pass data to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows you to pass attributes to the next request.
     * @return A string
     */
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

    /**
     * It updates the password of a user.
     *
     * @param id the id of the user
     * @param form the form that contains the new password
     * @param model This is the model object that is used to pass data from the controller to the view.
     * @param redirAttrs This is a RedirectAttributes object that allows you to add attributes to the redirect.
     * @return A string
     */
    @PostMapping("/edit/password")
    public String updatePassword(@RequestParam(name="id", required = true) int id,
                                 @ModelAttribute UserForm form, Model model, RedirectAttributes redirAttrs) {
        try {
            model.addAttribute("editForm", form);

            String newPass = PasswordHash.toHexString(PasswordHash.getSha(form.getPassword()));
            boolean feedback = userService.updatePassword(id, newPass);
            if(feedback) {
                redirAttrs.addFlashAttribute("success", "Password changed!");
            } else redirAttrs.addFlashAttribute("error", "Failed to change password!");

            return String.format("redirect:/user/profile?id=%d", id);
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * This function is called when the user clicks on the delete button on the user list page. It takes the id of the user
     * to be deleted as a parameter and returns the delete page
     *
     * @param id The id of the user to be deleted.
     * @param model The model is an object that holds the data that you want to pass to the view.
     * @return A string
     */
    @GetMapping("/delete")
    public String deleteUser(@RequestParam(name = "id", required = true) int id, Model model) {
        try {
            model.addAttribute("id", id);
            return "user/delete";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * This function is called when the user clicks the delete button on the user list page. It takes the id of the user to
     * be deleted as a parameter and deletes the user from the database
     *
     * @param id the id of the user to be deleted
     * @param model The model is an object that holds data that you want to pass to the view.
     * @return A string that is the name of the view to be rendered.
     */
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
