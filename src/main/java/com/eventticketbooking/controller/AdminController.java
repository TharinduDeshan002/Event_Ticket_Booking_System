package com.eventticketbooking.controller;

import com.eventticketbooking.model.Booking;
import com.eventticketbooking.service.AdminService;
import com.eventticketbooking.service.BookingService;
import com.eventticketbooking.service.EventService;
import com.eventticketbooking.service.UserService;
import com.eventticketbooking.web.SessionHelper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final EventService eventService;
    private final UserService userService;
    private final BookingService bookingService;
    private final AdminService adminService;

    public AdminController(EventService eventService, UserService userService,
                           BookingService bookingService, AdminService adminService) {
        this.eventService = eventService;
        this.userService = userService;
        this.bookingService = bookingService;
        this.adminService = adminService;
    }

    @GetMapping("/admin/login")
    public String loginForm(HttpSession session) {
        if (SessionHelper.isAdminLoggedIn(session)) {
            return "redirect:/admin/dashboard";
        }
        return "admin/login";
    }

    @PostMapping("/admin/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes redirect) {
        try {
            var admin = adminService.login(username, password);
            SessionHelper.logout(session);
            SessionHelper.loginAdmin(session, admin);
            redirect.addFlashAttribute("success", "Welcome, " + admin.getFullName());
            return "redirect:/admin/dashboard";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/login";
        }
    }

    @GetMapping("/admin/logout")
    public String logout(HttpSession session, RedirectAttributes redirect) {
        SessionHelper.logoutAdmin(session);
        redirect.addFlashAttribute("success", "Admin signed out.");
        return "redirect:/admin/login";
    }

    @GetMapping({"/admin", "/admin/"})
    public String adminRoot(HttpSession session) {
        if (SessionHelper.isAdminLoggedIn(session)) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/admin/login";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session, Model model, RedirectAttributes redirect) {
        var admin = SessionHelper.currentAdmin(session, adminService);
        if (admin == null) {
            redirect.addFlashAttribute("error", "Please sign in to the admin portal.");
            return "redirect:/admin/login";
        }

        var bookings = bookingService.findAll();
        long ticketsSold = bookings.stream()
                .filter(Booking::isConfirmed)
                .mapToInt(Booking::getTicketCount)
                .sum();
        double revenue = bookings.stream()
                .filter(Booking::isConfirmed)
                .mapToDouble(Booking::getTotalPrice)
                .sum();

        model.addAttribute("events", eventService.findAll());
        model.addAttribute("users", userService.findAll());
        model.addAttribute("totalEvents", eventService.findAll().size());
        model.addAttribute("totalUsers", userService.findAll().size());
        model.addAttribute("ticketsSold", ticketsSold);
        model.addAttribute("revenue", revenue);
        model.addAttribute("admin", admin);
        return "admin/dashboard";
    }

    @PostMapping("/admin/users/delete")
    public String deleteUser(@RequestParam String id, HttpSession session, RedirectAttributes redirect) {
        if (!requireAdmin(session, redirect)) {
            return "redirect:/admin/login";
        }
        try {
            userService.delete(id);
            redirect.addFlashAttribute("success", "User deleted.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    static boolean requireAdmin(HttpSession session, AdminService adminService, RedirectAttributes redirect) {
        if (SessionHelper.currentAdmin(session, adminService) == null) {
            redirect.addFlashAttribute("error", "Admin sign-in required.");
            return false;
        }
        return true;
    }

    private boolean requireAdmin(HttpSession session, RedirectAttributes redirect) {
        return requireAdmin(session, adminService, redirect);
    }
}
