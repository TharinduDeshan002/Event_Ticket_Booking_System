package com.eventticketbooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookingController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "booking/list";
    }

    @GetMapping("/bookings")
    public String bookings() {
        return "booking/list";
    }

    @GetMapping("/bookings/book")
    public String book(@RequestParam(required = false) String eventId) {
        return "booking/book";
    }
}
