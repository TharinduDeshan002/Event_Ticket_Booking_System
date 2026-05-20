package com.eventticketbooking.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents an event that users can book tickets for.
 */
public class Event {

    private String id;
    private String title;
    private String category;
    private String venue;
    private String city;
    private String country;
    private String date;
    private String time;
    private double price;
    private int availableTickets;
    private String image;

    public Event() {
    }

    public Event(String id, String title, String category, String venue, String city,
                 String country, String date, String time, double price,
                 int availableTickets, String image) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.venue = venue;
        this.city = city;
        this.country = country;
        this.date = date;
        this.time = time;
        this.price = price;
        this.availableTickets = availableTickets;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(int availableTickets) {
        this.availableTickets = availableTickets;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFormattedPrice() {
        if (price == (long) price) {
            return String.format("$%,d", (long) price);
        }
        return String.format("$%,.2f", price);
    }

    public String getLocation() {
        return city + ", " + country;
    }

    public String getFormattedDate() {
        try {
            LocalDate parsed = LocalDate.parse(date);
            return parsed.format(DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH));
        } catch (Exception e) {
            return date;
        }
    }

    public String getFullDescription() {
        return title + " at " + venue + " in " + getLocation()
                + " on " + getFormattedDate() + " at " + time + ". From " + getFormattedPrice() + " per ticket.";
    }
}
