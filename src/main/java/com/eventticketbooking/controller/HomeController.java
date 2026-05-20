package com.eventticketbooking.controller;

import com.eventticketbooking.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final EventService eventService;

    public HomeController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("featuredEvents", eventService.findFeatured());
        model.addAttribute("totalEvents", eventService.findAll().size());
        model.addAttribute("totalCountries", eventService.countCountries());
        return "index";
    }
}
