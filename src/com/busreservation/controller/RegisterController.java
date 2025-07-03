package com.busreservation.controller;

import com.busreservation.model.User;
import com.busreservation.util.DataManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the registration view.
 */
public class RegisterController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Label statusLabel;

    /**
     * Handles the 'Register' button click. Validates input and creates a new user.
     */
    @FXML
    private void handleRegisterAction(ActionEvent event) {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Input Validation 
        if (name.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            setStatusLabel(Color.RED, "All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            setStatusLabel(Color.RED, "Passwords do not match.");
            return;
        }

        // Check if a user with the same name already exists
        for (User existingUser : DataManager.getUsers()) {
            if (existingUser.getName().equalsIgnoreCase(name)) {
                setStatusLabel(Color.RED, "A user with this name already exists.");
                return;
            }
        }

        //  User Creation 
        User newUser = new User(name, phone, password);
        DataManager.addUser(newUser);

        System.out.println("New user registered: " + newUser);
        System.out.println("Total users: " + DataManager.getUsers().size());

        // Display success message and navigate back to login
        setStatusLabel(Color.GREEN, "Registration successful! Please login.");
        

    }

    /**
     * Handles the 'Back to Login' button click. 
     */
    @FXML
    private void handleBackToLoginAction(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent loginView = FXMLLoader.load(getClass().getResource("../view/LoginView.fxml"));
            Scene scene = new Scene(loginView);
            stage.setScene(scene);
            stage.setTitle("Bus Reservation System - Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to set the text and color of the status label.
     */
    private void setStatusLabel(Color color, String text) {
        statusLabel.setTextFill(color);
        statusLabel.setText(text);
    }
}