package com.busreservation.model.payment;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

/**
 * A concrete implementation of Payable for online payments.
 */
public class OnlinePayment implements Payable {
    @Override
    public boolean processPayment(double amount) {
        System.out.println("Processing online payment of ₹" + amount);
        
        // Simulate asking for a UPI ID
        TextInputDialog dialog = new TextInputDialog("yourid@upi");
        dialog.setTitle("Online Payment");
        dialog.setHeaderText("Enter your UPI ID to pay ₹" + String.format("%.2f", amount));
        dialog.setContentText("UPI ID:");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent() && !result.get().isEmpty()) {
            // Simulate processing
            System.out.println("Payment request sent to: " + result.get());
            showAlert("Online Payment Successful", "Payment of ₹" + String.format("%.2f", amount) + " was successful!");
            return true; // Assume success if user provides an ID
        } else {
            showAlert("Online Payment Cancelled", "The online payment was cancelled.");
            return false; // Payment failed or was cancelled
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}