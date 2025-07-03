package com.busreservation.controller;

import com.busreservation.model.Booking;
import com.busreservation.model.Bus;
import com.busreservation.model.payment.CashPayment;
import com.busreservation.model.payment.OnlinePayment;
import com.busreservation.model.payment.Payable;
import com.busreservation.util.DataManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SeatSelectionController {

    @FXML private Label busDetailsLabel;
    @FXML private Label routeDetailsLabel;
    @FXML private GridPane seatGridPane;
    @FXML private FlowPane selectedSeatsPane;
    @FXML private Label totalFareLabel;

    private Bus selectedBus;
    private List<String> selectedSeatNumbers = new ArrayList<>();
    private LocalDate journeyDate;

    public void initData(Bus bus, LocalDate date) {
        this.selectedBus = bus;
        this.journeyDate = date;

        busDetailsLabel.setText(bus.getOperatorName() + " - " + bus.getBusType() + (bus.isAC() ? " (AC)" : " (Non-AC)"));
        routeDetailsLabel.setText("Route: " + bus.getRoute().getSource() + " to " + bus.getRoute().getDestination() + " | Date: " + this.journeyDate.toString());
        createSeatLayout();
    }

    /**
     * Dynamically creates the grid of seats.
     * This method now calculates availability based on all bookings for the given date.
     */
    private void createSeatLayout() {
        
        // Get the list of seats that are already booked for this bus on this specific date.
        List<String> bookedSeats = DataManager.getBookedSeatsForDate(selectedBus.getBusId(), journeyDate);


        String[] columns = {"A", "B", "C", "D", "E"};

        for (int row = 0; row < 10; row++) { // 10 rows
            for (int col = 0; col < 5; col++) { // 5 columns
                if (col == 2) continue; 

                String seatNumber = columns[col] + (row + 1);
                ToggleButton seatButton = new ToggleButton(seatNumber);
                seatButton.setPrefWidth(60);

                // Check if this seat is in our calculated list of booked seats.
                if (bookedSeats.contains(seatNumber)) {
                    seatButton.setDisable(true);
                    seatButton.setStyle("-fx-base: #cccccc;");
                } else {
                    seatButton.setOnAction(event -> handleSeatSelection(seatButton, seatNumber));
                }

                int gridCol = col > 2 ? col - 1 : col;
                seatGridPane.add(seatButton, gridCol, row);
            }
        }
    }

    private void handleSeatSelection(ToggleButton seatButton, String seatNumber) {
        if (seatButton.isSelected()) {
            selectedSeatNumbers.add(seatNumber);
        } else {
            selectedSeatNumbers.remove(seatNumber);
        }
        updateSelectedSeatsDisplay();
        updateTotalFare();
    }

    private void updateSelectedSeatsDisplay() {
        selectedSeatsPane.getChildren().clear();
        for (String seatNum : selectedSeatNumbers) {
            selectedSeatsPane.getChildren().add(new Label(seatNum));
        }
    }

    private void updateTotalFare() {
        double totalFare = selectedBus.getFare() * selectedSeatNumbers.size();
        totalFareLabel.setText(String.format("Total Fare: ₹%.2f", totalFare));
    }

    @FXML
    private void handleProceedToPayment(ActionEvent event) {
        if (selectedSeatNumbers.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "No Seats Selected", "Please select at least one seat to proceed.");
            return;
        }

        double totalFare = selectedBus.getFare() * selectedSeatNumbers.size();

        List<String> choices = Arrays.asList("Cash", "Online Payment (UPI)");
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Cash", choices);
        dialog.setTitle("Payment Method");
        dialog.setHeaderText("Select your preferred payment method");
        dialog.setContentText("Choose one:");
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(choice -> {
            Payable paymentMethod;
            if (choice.equals("Cash")) {
                paymentMethod = new CashPayment();
            } else {
                paymentMethod = new OnlinePayment();
            }

            boolean paymentSuccess = false;
            try {
                paymentSuccess = paymentMethod.processPayment(totalFare);
            } catch (Exception e) {
                System.err.println("An error occurred during payment processing: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Payment Error", "An unexpected error occurred.");
            }

            if (paymentSuccess) {
             
                // Create the booking object
                Booking newBooking = new Booking(DataManager.getCurrentUser(), selectedBus, new ArrayList<>(selectedSeatNumbers), totalFare, this.journeyDate);
                
                // Use the central method in DataManager to add the booking to the main list
                DataManager.addBooking(newBooking);



                System.out.println("Booking confirmed: " + newBooking);
                showAlert(Alert.AlertType.INFORMATION, "Booking Confirmed", "Your seats have been successfully booked! Booking ID: " + newBooking.getBookingId());
                
                handleBackToDashboard(event);
            } else {
                System.out.println("Payment failed or was cancelled by the user.");
            }
        });
    }
    
    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent dashboardView = FXMLLoader.load(getClass().getResource("../view/DashboardView.fxml"));
            Scene scene = new Scene(dashboardView);
            stage.setScene(scene);
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private int[] convertSeatNumberToIndex(String seatNumber) {
        if (seatNumber == null || seatNumber.length() < 2) return null;
        try {
            char colChar = seatNumber.charAt(0);
            int row = Integer.parseInt(seatNumber.substring(1)) - 1;
            int col = colChar - 'A';
            if (row >= 0 && row < 10 && col >= 0 && col < 5) {
                return new int[]{row, col};
            }
        } catch (NumberFormatException e) {
            System.err.println("Could not parse seat number: " + seatNumber);
        }
        return null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}