package com.eventticketbooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @GetMapping
    public String list() {
        return "booking/list";
    }

    @GetMapping("/book")
    public String book() {
        return "booking/book";
    }
}
