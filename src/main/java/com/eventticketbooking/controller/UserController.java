package com.eventticketbooking.controller;

import com.eventticketbooking.model.User;
import com.eventticketbooking.service.UserService;
import com.eventticketbooking.web.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerForm() {
        return "user/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String phone,
                           @RequestParam String password,
                           @RequestParam(required = false, defaultValue = "") String city,
                           @RequestParam(required = false, defaultValue = "") String country,
                           HttpSession session,
                           RedirectAttributes redirect) {
        try {
            User user = new User();
            user.setFullName(fullName.trim());
            user.setEmail(email.trim());
            user.setPhone(phone.trim());
            user.setPassword(password);
            user.setCity(city.trim());
            user.setCountry(country.trim());
            user.setRole("USER");
            user = userService.register(user);
            SessionHelper.logoutAdmin(session);
            SessionHelper.login(session, user);
            redirect.addFlashAttribute("success", "Account created successfully!");
            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/register";
        }
    }

    @GetMapping("/login")
    public String loginForm() {
        return "user/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirect) {
        try {
            User user = userService.login(email.trim(), password);
            SessionHelper.logoutAdmin(session);
            SessionHelper.login(session, user);
            redirect.addFlashAttribute("success", "Welcome back, " + user.getFullName() + "!");
            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirect) {
        SessionHelper.logout(session);
        redirect.addFlashAttribute("success", "You have been signed out.");
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model, RedirectAttributes redirect) {
        User user = SessionHelper.currentUser(session, userService);
        if (user == null) {
            redirect.addFlashAttribute("error", "Please sign in to view your profile.");
            return "redirect:/users/login";
        }
        model.addAttribute("user", user);
        return "user/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String fullName,
                                @RequestParam String phone,
                                @RequestParam String city,
                                @RequestParam String country,
                                HttpSession session,
                                RedirectAttributes redirect) {
        User user = SessionHelper.currentUser(session, userService);
        if (user == null) {
            redirect.addFlashAttribute("error", "Please sign in first.");
            return "redirect:/users/login";
        }
        try {
            user.setFullName(fullName.trim());
            user.setPhone(phone.trim());
            user.setCity(city.trim());
            user.setCountry(country.trim());
            userService.update(user);
            redirect.addFlashAttribute("success", "Profile updated.");
            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/users/profile";
        }
    }
}
