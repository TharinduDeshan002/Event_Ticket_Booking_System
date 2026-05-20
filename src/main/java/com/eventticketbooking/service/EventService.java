package com.eventticketbooking.service;

import com.eventticketbooking.file.EventFileHandler;
import com.eventticketbooking.model.Event;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventFileHandler eventFileHandler;

    public EventService(EventFileHandler eventFileHandler) {
        this.eventFileHandler = eventFileHandler;
    }

    public List<Event> findAll() {
        return eventFileHandler.readAll();
    }

    public Event findById(String id) {
        return findAll().stream()
                .filter(e -> e.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public List<Event> findFeatured() {
        return findAll().stream().limit(3).collect(Collectors.toList());
    }

    public List<Event> findByCategory(String category) {
        if (category == null || category.isBlank() || category.equalsIgnoreCase("all")) {
            return findAll();
        }
        return findAll().stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public long countCountries() {
        return findAll().stream().map(Event::getCountry).distinct().count();
    }

    public Event save(Event event) {
        event.setId(com.eventticketbooking.util.IdGenerator.nextId("E", findAll(), Event::getId));
        if (event.getImage() == null || event.getImage().isBlank()) {
            event.setImage(imageForCategory(event.getCategory()));
        }
        java.util.List<Event> events = findAll();
        events.add(event);
        eventFileHandler.writeAll(events);
        return event;
    }

    public void update(Event updated) {
        java.util.List<Event> events = findAll();
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(updated.getId())) {
                if (updated.getImage() == null || updated.getImage().isBlank()) {
                    updated.setImage(imageForCategory(updated.getCategory()));
                }
                events.set(i, updated);
                eventFileHandler.writeAll(events);
                return;
            }
        }
        throw new IllegalArgumentException("Event not found");
    }

    public void delete(String id) {
        java.util.List<Event> events = findAll();
        if (!events.removeIf(e -> e.getId().equals(id))) {
            throw new IllegalArgumentException("Event not found");
        }
        eventFileHandler.writeAll(events);
    }

    public static String imageForCategory(String category) {
        if (category == null) {
            return "hero-concert.jpg";
        }
        return switch (category.toLowerCase()) {
            case "music" -> "event-music.jpg";
            case "sports" -> "event-sports.jpg";
            case "tech", "gaming" -> "event-tech.jpg";
            default -> "hero-concert.jpg";
        };
    }
}
