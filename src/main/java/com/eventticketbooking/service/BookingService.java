package com.eventticketbooking.service;

import com.eventticketbooking.file.BookingFileHandler;
import com.eventticketbooking.model.Booking;
import com.eventticketbooking.model.Event;
import com.eventticketbooking.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingFileHandler bookingFileHandler;
    private final EventService eventService;

    public BookingService(BookingFileHandler bookingFileHandler, EventService eventService) {
        this.bookingFileHandler = bookingFileHandler;
        this.eventService = eventService;
    }

    public List<Booking> findAll() {
        return enrichAll(bookingFileHandler.readAll());
    }

    public Booking findById(String id) {
        Booking booking = bookingFileHandler.readAll().stream()
                .filter(b -> b.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
        if (booking != null) {
            enrich(booking);
        }
        return booking;
    }

    public List<Booking> findByUserId(String userId) {
        return enrichAll(bookingFileHandler.readAll().stream()
                .filter(b -> b.getUserId().equalsIgnoreCase(userId))
                .collect(Collectors.toList()));
    }

    public Booking create(Booking booking) {
        Event event = eventService.findById(booking.getEventId());
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        if (booking.getTicketCount() < 1) {
            throw new IllegalArgumentException("At least 1 ticket required");
        }
        if (booking.getTicketCount() > event.getAvailableTickets()) {
            throw new IllegalArgumentException("Not enough tickets available");
        }

        booking.setId(IdGenerator.nextId("B", bookingFileHandler.readAll(), Booking::getId));
        booking.setUnitPrice(event.getPrice());
        booking.setTotalPrice(booking.getTicketCount() * event.getPrice());
        booking.setStatus("CONFIRMED");
        booking.setBookingDate(LocalDate.now().toString());

        List<Booking> bookings = bookingFileHandler.readAll();
        bookings.add(booking);
        bookingFileHandler.writeAll(bookings);

        event.setAvailableTickets(event.getAvailableTickets() - booking.getTicketCount());
        eventService.update(event);

        enrich(booking);
        return booking;
    }

    public void cancel(String bookingId, String userId) {
        List<Booking> bookings = bookingFileHandler.readAll();
        for (Booking booking : bookings) {
            if (booking.getId().equals(bookingId)) {
                if (!booking.getUserId().equals(userId)) {
                    throw new IllegalArgumentException("Not allowed to cancel this booking");
                }
                if (booking.isCancelled()) {
                    return;
                }
                booking.setStatus("CANCELLED");
                bookingFileHandler.writeAll(bookings);

                Event event = eventService.findById(booking.getEventId());
                if (event != null) {
                    event.setAvailableTickets(event.getAvailableTickets() + booking.getTicketCount());
                    eventService.update(event);
                }
                return;
            }
        }
        throw new IllegalArgumentException("Booking not found");
    }

    private List<Booking> enrichAll(List<Booking> bookings) {
        bookings.forEach(this::enrich);
        return bookings;
    }

    private void enrich(Booking booking) {
        Event event = eventService.findById(booking.getEventId());
        if (event != null) {
            booking.setEventTitle(event.getTitle());
            booking.setEventImage(event.getImage());
            booking.setEventVenue(event.getVenue());
            booking.setEventCity(event.getCity());
            booking.setEventCountry(event.getCountry());
            booking.setEventDate(event.getFormattedDate());
            booking.setEventTime(event.getTime());
        }
    }
}
