package com.busreservation.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a user in the system.
 * Implements Serializable so that User objects can be saved to a file.
 * This directly applies the concepts of Classes, Objects, and Encapsulation.
 */
public class User implements Serializable {

    // A unique ID for serialization to prevent issues when loading saved data.
    private static final long serialVersionUID = 1L;

    // Instance variables are private to enforce Encapsulation.
    private String name;
    private String phoneNumber;
    private String password;
    private ArrayList<Booking> bookingHistory; // To hold user's bookings

    // Constructor to initialize a new User object.
    public User(String name, String phoneNumber, String password) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.bookingHistory = new ArrayList<>(); // Initialize with an empty list
    }

    //Getters and Setters 
    // Public methods to access and modify the private instance variables.

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Booking> getBookingHistory() {
        return bookingHistory;
    }

    // --- Utility Methods ---

    public void addBooking(Booking booking) {
        this.bookingHistory.add(booking);
    }

    @Override
    public String toString() {
        return "User [Name=" + name + ", Phone=" + phoneNumber + "]";
    }
}