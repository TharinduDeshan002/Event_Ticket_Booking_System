package com.eventticketbooking.controller;

import com.eventticketbooking.model.Event;
import com.eventticketbooking.service.AdminService;
import com.eventticketbooking.service.EventService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final AdminService adminService;

    public EventController(EventService eventService, AdminService adminService) {
        this.eventService = eventService;
        this.adminService = adminService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String category, Model model) {
        model.addAttribute("events", eventService.findByCategory(category));
        model.addAttribute("selectedCategory", category == null ? "all" : category.toLowerCase());
        return "event/list";
    }

    @GetMapping("/add")
    public String addForm(HttpSession session, Model model, RedirectAttributes redirect) {
        if (!AdminController.requireAdmin(session, adminService, redirect)) {
            return "redirect:/admin/login";
        }
        model.addAttribute("event", new Event());
        return "event/add";
    }

    @PostMapping("/add")
    public String add(@RequestParam String title,
                      @RequestParam String category,
                      @RequestParam String venue,
                      @RequestParam String city,
                      @RequestParam String country,
                      @RequestParam String date,
                      @RequestParam String time,
                      @RequestParam double price,
                      @RequestParam int availableTickets,
                      @RequestParam(required = false) String image,
                      HttpSession session,
                      RedirectAttributes redirect) {
        if (!AdminController.requireAdmin(session, adminService, redirect)) {
            return "redirect:/admin/login";
        }
        try {
            Event event = buildEvent(title, category, venue, city, country, date, time, price, availableTickets, image);
            eventService.save(event);
            redirect.addFlashAttribute("success", "Event added: " + event.getTitle());
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/events/add";
        }
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam String id, HttpSession session, Model model, RedirectAttributes redirect) {
        if (!AdminController.requireAdmin(session, adminService, redirect)) {
            return "redirect:/admin/login";
        }
        Event event = eventService.findById(id);
        if (event == null) {
            redirect.addFlashAttribute("error", "Event not found");
            return "redirect:/admin/dashboard";
        }
        model.addAttribute("event", event);
        return "event/edit";
    }

    @PostMapping("/edit")
    public String edit(@RequestParam String id,
                       @RequestParam String title,
                       @RequestParam String category,
                       @RequestParam String venue,
                       @RequestParam String city,
                       @RequestParam String country,
                       @RequestParam String date,
                       @RequestParam String time,
                       @RequestParam double price,
                       @RequestParam int availableTickets,
                       @RequestParam(required = false) String image,
                       HttpSession session,
                       RedirectAttributes redirect) {
        if (!AdminController.requireAdmin(session, adminService, redirect)) {
            return "redirect:/admin/login";
        }
        try {
            Event event = buildEvent(title, category, venue, city, country, date, time, price, availableTickets, image);
            event.setId(id);
            eventService.update(event);
            redirect.addFlashAttribute("success", "Event updated.");
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/events/edit?id=" + id;
        }
    }

    @PostMapping("/delete")
    public String delete(@RequestParam String id, HttpSession session, RedirectAttributes redirect) {
        if (!AdminController.requireAdmin(session, adminService, redirect)) {
            return "redirect:/admin/login";
        }
        try {
            eventService.delete(id);
            redirect.addFlashAttribute("success", "Event deleted.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    private Event buildEvent(String title, String category, String venue, String city, String country,
                             String date, String time, double price, int availableTickets, String image) {
        Event event = new Event();
        event.setTitle(title.trim());
        event.setCategory(category.trim());
        event.setVenue(venue.trim());
        event.setCity(city.trim());
        event.setCountry(country.trim());
        event.setDate(date);
        event.setTime(time);
        event.setPrice(price);
        event.setAvailableTickets(availableTickets);
        if (image != null && !image.isBlank()) {
            event.setImage(image.trim());
        }
        return event;
    }
}
