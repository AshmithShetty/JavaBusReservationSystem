package com.busreservation.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * Represents a confirmed booking made by a user.
 */
public class Booking implements Serializable {

    private static final long serialVersionUID = 1L;
    private String bookingId;
    private User user;
    private Bus bus;
    private List<String> seatNumbers;
    private double totalFare;
    private LocalDate journeyDate;

    public Booking(User user, Bus bus, List<String> seatNumbers, double totalFare, LocalDate journeyDate) {
        // Generate a simple unique booking ID
        this.bookingId = "BKG-" + System.currentTimeMillis();
        this.user = user;
        this.bus = bus;
        this.seatNumbers = seatNumbers;
        this.totalFare = totalFare;
        this.journeyDate = journeyDate;
    }

    // Getters for all fields
    public String getBookingId() { return bookingId; }
    public User getUser() { return user; }
    public Bus getBus() { return bus; }
    public List<String> getSeatNumbers() { return seatNumbers; }
    public double getTotalFare() { return totalFare; }
    public LocalDate getJourneyDate() { return journeyDate; }

    @Override
    public String toString() {
        return "Booking ID: " + bookingId + " | Bus: " + bus.getOperatorName() + " | Seats: " + seatNumbers + " | Fare: ₹" + totalFare;
    }
}