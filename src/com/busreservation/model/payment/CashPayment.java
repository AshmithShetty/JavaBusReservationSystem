package com.busreservation.model.payment;

import javafx.scene.control.Alert;

/**
 * A concrete implementation of Payable for cash payments.
 */
public class CashPayment implements Payable {
    @Override
    public boolean processPayment(double amount) {
        System.out.println("Processing cash payment of ₹" + amount);
        
        showAlert("Cash Payment", "Please pay ₹" + String.format("%.2f", amount) + " at the counter upon boarding.");
        return true;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}