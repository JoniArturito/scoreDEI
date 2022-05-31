/**
 * It's a controller that handles the requests to the root of the application, and it also handles the login and logout
 * requests
 */
package com.scoreDEI.scoreDEI;

import com.scoreDEI.Entities.*;
import com.scoreDEI.Forms.*;
import com.scoreDEI.Others.PasswordHash;
import com.scoreDEI.Services.*;
import com.scoreDEI.filters.AdminFilter;
import com.scoreDEI.filters.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class Base {
    @Autowired
    UserService userService;

    /**
     * This function redirects the user to the game list page
     *
     * @return A redirect to the game list page.
     */
    @GetMapping("/")
    public String redirect(){
        try {
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                            .getRequest();
            HttpSession session = request.getSession();

            session.setAttribute("login", true);
            session.setAttribute("logout", null);

            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It redirects the user to the error page
     *
     * @return A redirect to the error page.
     */
    @GetMapping("/error")
    public String error(){
        return "redirect:/error/";
    }

    /**
     * The function takes a Model object as a parameter, adds a new UserForm object to the model, and returns the name of
     * the view to be rendered
     *
     * @param m The model object.
     * @return A string that is the name of the html file that is being returned.
     */
    @GetMapping("/login")
    public String login(Model m) {
        m.addAttribute("userForm", new UserForm());
        return "login";
    }

    /**
     * The function takes in a user's email and password, hashes the password, and then checks if the user exists in the
     * database. If the user exists, the user is added to the session
     *
     * @param form The form object that will be used to get the parameters from the form.
     * @return A redirect to the game list page.
     */
    @PostMapping("/login")
    public String login(@ModelAttribute UserForm form) {
        try {
            String email = form.getEmail();
            String password = PasswordHash.toHexString(PasswordHash.getSha(form.getPassword()));

            Optional<User> user = userService.getUser(email, password);

            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                            .getRequest();
            HttpSession session = request.getSession();

            if(user.isPresent()) {
                session.setAttribute("user", user.get());
                if(user.get().getType() == 1) session.setAttribute("admin", user.get());
                else {
                    session.setAttribute("admin", null);
                    session.setAttribute("login", true);
                    session.setAttribute("logout", null);
                }
            } else {
                session.setAttribute("user", null);

            }

            return "redirect:/game/list";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * "Create a new filter registration bean, set the filter to be an instance of the AuthenticationFilter class, add the
     * URL patterns that the filter should be applied to, and set the order of the filter to be 1."
     *
     * The first line of the function creates a new FilterRegistrationBean object. This is a Spring class that allows us to
     * register a filter with the Spring framework
     *
     * @return A FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authFilter() {
        FilterRegistrationBean<AuthenticationFilter> bean = new FilterRegistrationBean<>();

        bean.setFilter(new AuthenticationFilter());

        List<String> eventUrlPatterns = Arrays.asList("register");
        for (String urlPattern : eventUrlPatterns) {
            bean.addUrlPatterns(String.format("/event/%s", urlPattern));
        }

        bean.setOrder(1);
        return bean;
    }

    /**
     * This function is called when the user tries to access a page that they are not authorized to access
     *
     * @return A string
     */
    @GetMapping("/forbidden")
    public String forbidden() {
        try {
            return "forbidden";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * "Create a new FilterRegistrationBean, set the filter to be the AdminFilter, add the URL patterns that the filter
     * should be applied to, and set the order to 2."
     *
     * The first line of the function creates a new FilterRegistrationBean. This is a Spring class that allows us to
     * register a filter with Spring
     *
     * @return A FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean<AdminFilter> adminAccessFilter() {
        FilterRegistrationBean<AdminFilter> bean = new FilterRegistrationBean<>();

        bean.setFilter(new AdminFilter());

        bean.addUrlPatterns("/user/*");

        List<String> teamUrlPatterns = Arrays.asList("register", "edit/*", "delete");
        for (String urlPattern : teamUrlPatterns) {
            bean.addUrlPatterns(String.format("/team/%s", urlPattern));
        }

        List<String> gameUrlPatterns = Arrays.asList("register", "edit/*", "delete");
        for (String urlPattern : gameUrlPatterns) {
            bean.addUrlPatterns(String.format("/game/%s", urlPattern));
        }

        List<String> playerUrlPatterns = Arrays.asList("register", "edit/*", "delete");
        for (String urlPattern : playerUrlPatterns) {
            bean.addUrlPatterns(String.format("/player/%s", urlPattern));
        }

        List<String> eventUrlPatterns = Arrays.asList("delete");
        for (String urlPattern : eventUrlPatterns) {
            bean.addUrlPatterns(String.format("/event/%s", urlPattern));
        }

        bean.setOrder(2);
        return bean;
    }

    /**
     * If the user is logged in, log them out and redirect them to the game list page.
     *
     * @return A redirect to the game list page.
     */
    @GetMapping("/logout")
    public String logout() {

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest();
        HttpSession session = request.getSession();
        session.setAttribute("user", null);
        session.setAttribute("login", true);
        session.setAttribute("logout", null);

        return "redirect:/game/list";
    }

    /**
     * This function is used to create data
     *
     * @return A string
     */
    @GetMapping({"/createData"})
    public String createData() {
        try {
            return "createData";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }

    /**
     * It adds two admin users and five regular users to the database
     *
     * @param m Model object
     * @return A string
     */
    @PostMapping("/saveData")
    public String saveData(Model m) {
        try {
            AdminUser[] admins = {
                    new AdminUser("Rui", "abcdefgh@gmail.com", 123456789L, PasswordHash.toHexString(PasswordHash.getSha("bruh"))),
                    new AdminUser("Joao", "admin@mail.com", 123456788L, PasswordHash.toHexString(PasswordHash.getSha("123")))
            };
            RegularUser[] users = {
                    new RegularUser("Paula", "qrstuvwx@gmail.com", 111111111L, PasswordHash.toHexString(PasswordHash.getSha("1234"))),
                    new RegularUser("Artur", "yz@gmail.com", 222222222L, PasswordHash.toHexString(PasswordHash.getSha("1234"))),
                    new RegularUser("Luísa", "aaaa@gmail.com", 333333333L, PasswordHash.toHexString(PasswordHash.getSha("1234"))),
                    new RegularUser("Alexandra", "bbbb@gmail.com", 444444444L, PasswordHash.toHexString(PasswordHash.getSha("1234"))),
                    new RegularUser("Carlos", "cccc@gmail.com", 555555555L, PasswordHash.toHexString(PasswordHash.getSha("1234")))
            };

            for (AdminUser ad: admins)
                this.userService.addUser(ad);
            for (RegularUser us : users)
                this.userService.addUser(us);

            return "redirect:/";
        } catch (Exception e) {
            return "redirect:/error/";
        }
    }
}
