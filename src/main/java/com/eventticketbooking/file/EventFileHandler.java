package com.eventticketbooking.file;

import com.eventticketbooking.model.Event;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EventFileHandler {

    private final FileManager fileManager;
    private final String eventsPath;

    public EventFileHandler(FileManager fileManager,
                          @Value("${app.data.events}") String eventsPath) {
        this.fileManager = fileManager;
        this.eventsPath = eventsPath;
    }

    public List<Event> readAll() {
        List<Event> events = new ArrayList<>();
        for (String line : fileManager.readLines(eventsPath)) {
            events.add(parseLine(line));
        }
        return events;
    }

    public void writeAll(List<Event> events) {
        List<String> lines = new ArrayList<>();
        lines.add("# Format: eventId|title|category|venue|city|country|date|time|priceUSD|availableTickets|image");
        for (Event event : events) {
            lines.add(formatLine(event));
        }
        fileManager.writeLines(eventsPath, lines);
    }

    public Event parseLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 11) {
            throw new IllegalArgumentException("Invalid event line: " + line);
        }
        return new Event(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                parts[4].trim(),
                parts[5].trim(),
                parts[6].trim(),
                parts[7].trim(),
                Double.parseDouble(parts[8].trim()),
                Integer.parseInt(parts[9].trim()),
                parts[10].trim()
        );
    }

    public String formatLine(Event event) {
        return String.join("|",
                event.getId(),
                event.getTitle(),
                event.getCategory(),
                event.getVenue(),
                event.getCity(),
                event.getCountry(),
                event.getDate(),
                event.getTime(),
                String.format("%.2f", event.getPrice()),
                String.valueOf(event.getAvailableTickets()),
                event.getImage()
        );
    }
}
