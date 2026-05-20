package com.eventticketbooking.file;

import com.eventticketbooking.model.Booking;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookingFileHandler {

    private final FileManager fileManager;
    private final String bookingsPath;

    public BookingFileHandler(FileManager fileManager,
                              @Value("${app.data.bookings}") String bookingsPath) {
        this.fileManager = fileManager;
        this.bookingsPath = bookingsPath;
    }

    public List<Booking> readAll() {
        List<Booking> bookings = new ArrayList<>();
        for (String line : fileManager.readLines(bookingsPath)) {
            bookings.add(parseLine(line));
        }
        return bookings;
    }

    public void writeAll(List<Booking> bookings) {
        List<String> lines = new ArrayList<>();
        lines.add("# FORMAT: bookingId|userId|eventId|customerName|customerEmail|ticketCount|unitPriceUSD|totalPriceUSD|status|bookingDate");
        for (Booking booking : bookings) {
            lines.add(formatLine(booking));
        }
        fileManager.writeLines(bookingsPath, lines);
    }

    public Booking parseLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 10) {
            throw new IllegalArgumentException("Invalid booking line: " + line);
        }
        Booking booking = new Booking();
        booking.setId(p[0].trim());
        booking.setUserId(p[1].trim());
        booking.setEventId(p[2].trim());
        booking.setCustomerName(p[3].trim());
        booking.setCustomerEmail(p[4].trim());
        booking.setTicketCount(Integer.parseInt(p[5].trim()));
        booking.setUnitPrice(Double.parseDouble(p[6].trim()));
        booking.setTotalPrice(Double.parseDouble(p[7].trim()));
        booking.setStatus(p[8].trim());
        booking.setBookingDate(p[9].trim());
        return booking;
    }

    public String formatLine(Booking booking) {
        return String.join("|",
                booking.getId(),
                booking.getUserId(),
                booking.getEventId(),
                booking.getCustomerName(),
                booking.getCustomerEmail(),
                String.valueOf(booking.getTicketCount()),
                String.format("%.2f", booking.getUnitPrice()),
                String.format("%.2f", booking.getTotalPrice()),
                booking.getStatus(),
                booking.getBookingDate()
        );
    }
}
