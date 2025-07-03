package com.busreservation.util;

import com.busreservation.model.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all application data, including loading from and saving to files.
 * This class now handles a central list of all bookings to manage daily inventory.
 */
public class DataManager {

    private static final String USERS_FILE = "data/users.dat";
    private static final String BUSES_FILE = "data/buses.dat";
    //NEW: File for all bookings 
    private static final String BOOKINGS_FILE = "data/bookings.dat";

    private static ArrayList<User> users = new ArrayList<>();
    private static ArrayList<Bus> buses = new ArrayList<>();
    //NEW: Central list for all bookings 
    private static ArrayList<Booking> allBookings = new ArrayList<>();
    private static User currentUser;

    /**
     * Loads user, bus, and booking data from files. If files don't exist, it creates default data.
     */
    public static void loadData() {
        // --- Loading Users (unchanged) ---
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USERS_FILE))) {
            users = (ArrayList<User>) ois.readObject();
            System.out.println("Successfully loaded " + users.size() + " users from file.");
        } catch (FileNotFoundException e) {
            System.out.println("Users file not found. Creating default users.");
            createDefaultUsers();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading users data: " + e.getMessage());
            e.printStackTrace();
            createDefaultUsers();
        }

        // --- Loading Buses (unchanged) ---
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BUSES_FILE))) {
            buses = (ArrayList<Bus>) ois.readObject();
            System.out.println("Successfully loaded " + buses.size() + " buses from file.");
        } catch (FileNotFoundException e) {
            System.out.println("Buses file not found. Creating default buses.");
            createDefaultBuses();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading buses data: " + e.getMessage());
            e.printStackTrace();
            createDefaultBuses();
        }
        
        //NEW: Loading All Bookings
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BOOKINGS_FILE))) {
            allBookings = (ArrayList<Booking>) ois.readObject();
            System.out.println("Successfully loaded " + allBookings.size() + " total bookings from file.");
        } catch (FileNotFoundException e) {
            System.out.println("Bookings file not found. Starting with an empty booking list.");
            allBookings = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading bookings data: " + e.getMessage());
            e.printStackTrace();
            allBookings = new ArrayList<>();
        }
    }

    /**
     * Saves the current state of users, buses, and all bookings to files.
     */
    public static void saveData() {
        new File("data").mkdirs();

        //Saving Users 
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
            oos.writeObject(users);
            System.out.println("Successfully saved users data to " + USERS_FILE);
        } catch (IOException e) {
            System.err.println("Error saving users data: " + e.getMessage());
            e.printStackTrace();
        }

        //Saving Buses 
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BUSES_FILE))) {
            oos.writeObject(buses);
            System.out.println("Successfully saved buses data to " + BUSES_FILE);
        } catch (IOException e) {
            System.err.println("Error saving buses data: " + e.getMessage());
            e.printStackTrace();
        }
        
        //Saving All Bookings
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BOOKINGS_FILE))) {
            oos.writeObject(allBookings);
            System.out.println("Successfully saved all " + allBookings.size() + " bookings to " + BOOKINGS_FILE);
        } catch (IOException e) {
            System.err.println("Error saving all bookings data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createDefaultUsers() {
        users.clear();
        users.add(new User("Rohan", "9876543210", "rohan123"));
        users.add(new User("Priya", "9123456789", "priya123"));
    }

    private static void createDefaultBuses() {
        buses.clear();
        Route route1 = new Route("Mumbai", "Pune");
        Route route2 = new Route("Delhi", "Jaipur");
        Route route3 = new Route("Bengaluru", "Chennai");
        Route route4 = new Route("Mumbai", "Goa");

        buses.add(new SeaterBus("MH01-1111", "Shivneri Travels", route1, true, 750.00));
        buses.add(new SleeperBus("MH01-2222", "Neeta Tours", route1, true, 1100.00));
        buses.add(new SeaterBus("DL02-3333", "RSRTC Deluxe", route2, false, 550.00));
        buses.add(new SleeperBus("DL02-4444", "Rajputana Express", route2, true, 900.00));
        buses.add(new SeaterBus("KA03-5555", "KSRTC Airavat", route3, true, 850.00));
        buses.add(new SleeperBus("TN04-6666", "Parveen Travels", route3, false, 1200.00));
        buses.add(new SleeperBus("MH01-7777", "VRL Logistics", route4, true, 1500.00));
    }
    
    //Getters and Setters for users and buses
    public static ArrayList<User> getUsers() { return users; }
    public static void addUser(User user) { users.add(user); }
    public static User getCurrentUser() { return currentUser; }
    public static void setCurrentUser(User user) { currentUser = user; }
    public static void logout() { currentUser = null; }
    public static ArrayList<Bus> getBuses() { return buses; }

    //Centralized methods for managing bookings
    
    /**
     * Adds a new booking to the central list and to the user's personal history.
     * @param booking The completed booking to add.
     */
    public static void addBooking(Booking booking) {
        allBookings.add(booking);
        // Also add to the user's personal list for their profile view
        booking.getUser().addBooking(booking);
    }

    /**
     * Removes a booking from the central list and from the user's personal history.
     * @param booking The booking to remove.
     */
    public static void removeBooking(Booking booking) {
        allBookings.remove(booking);
        // Also remove from the user's personal list
        booking.getUser().getBookingHistory().remove(booking);
    }

    /**
     * Calculates which seats are booked for a specific bus on a specific date by
     * checking the central list of all bookings.
     * @param busId The ID of the bus to check.
     * @param date The date to check for.
     * @return A list of seat numbers that are booked.
     */
    public static List<String> getBookedSeatsForDate(String busId, LocalDate date) {
        List<String> bookedSeats = new ArrayList<>();
        for (Booking booking : allBookings) {
            // Check if the booking is for the correct bus AND the correct date
            if (booking.getBus().getBusId().equals(busId) && booking.getJourneyDate().equals(date)) {
                bookedSeats.addAll(booking.getSeatNumbers());
            }
        }
        return bookedSeats;
    }
}