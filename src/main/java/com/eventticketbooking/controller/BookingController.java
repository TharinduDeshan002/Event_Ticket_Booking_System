package com.eventticketbooking.controller;

import com.eventticketbooking.model.Booking;
import com.eventticketbooking.model.Event;
import com.eventticketbooking.model.User;
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

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class BookingController {

    private final EventService eventService;
    private final BookingService bookingService;
    private final UserService userService;

    public BookingController(EventService eventService, BookingService bookingService, UserService userService) {
        this.eventService = eventService;
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model, RedirectAttributes redirect) {
        User user = SessionHelper.currentUser(session, userService);
        if (user == null) {
            redirect.addFlashAttribute("error", "Please sign in to view your bookings.");
            return "redirect:/users/login";
        }
        List<Booking> bookings = bookingService.findByUserId(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("bookings", bookings);
        model.addAttribute("upcomingBookings", bookings.stream()
                .filter(Booking::isConfirmed)
                .collect(Collectors.toList()));
        return "booking/list";
    }

    @GetMapping("/bookings")
    public String bookings(HttpSession session, Model model, RedirectAttributes redirect) {
        return dashboard(session, model, redirect);
    }

    @GetMapping("/bookings/book")
    public String bookForm(@RequestParam String eventId, HttpSession session, Model model, RedirectAttributes redirect) {
        User user = SessionHelper.currentUser(session, userService);
        if (user == null) {
            redirect.addFlashAttribute("error", "Please sign in to book tickets.");
            return "redirect:/users/login?redirect=/bookings/book?eventId=" + eventId;
        }
        Event event = eventService.findById(eventId);
        if (event == null) {
            redirect.addFlashAttribute("error", "Event not found.");
            return "redirect:/events";
        }
        model.addAttribute("event", event);
        model.addAttribute("user", user);
        return "booking/book";
    }

    @PostMapping("/bookings/book")
    public String book(@RequestParam String eventId,
                       @RequestParam String customerName,
                       @RequestParam String customerEmail,
                       @RequestParam int ticketCount,
                       HttpSession session,
                       RedirectAttributes redirect) {
        User user = SessionHelper.currentUser(session, userService);
        if (user == null) {
            redirect.addFlashAttribute("error", "Please sign in to book tickets.");
            return "redirect:/users/login";
        }
        try {
            Booking booking = new Booking();
            booking.setUserId(user.getId());
            booking.setEventId(eventId);
            booking.setCustomerName(customerName.trim());
            booking.setCustomerEmail(customerEmail.trim());
            booking.setTicketCount(ticketCount);
            bookingService.create(booking);
            redirect.addFlashAttribute("success", "Booking confirmed! Check your dashboard.");
            return "redirect:/dashboard";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/bookings/book?eventId=" + eventId;
        }
    }

    @PostMapping("/bookings/cancel")
    public String cancel(@RequestParam String bookingId,
                         HttpSession session,
                         RedirectAttributes redirect) {
        User user = SessionHelper.currentUser(session, userService);
        if (user == null) {
            redirect.addFlashAttribute("error", "Please sign in first.");
            return "redirect:/users/login";
        }
        try {
            bookingService.cancel(bookingId, user.getId());
            redirect.addFlashAttribute("success", "Booking cancelled.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }
}
