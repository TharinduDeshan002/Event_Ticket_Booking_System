package com.eventticketbooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/events")
public class EventController {

    @GetMapping
    public String list() {
        return "event/list";
    }

    @GetMapping("/add")
    public String add() {
        return "event/add";
    }
}
