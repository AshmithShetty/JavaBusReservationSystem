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
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the Login View. Handles user authentication.
 */
public class LoginController {

    // @FXML annotations link these fields to the components defined in LoginView.fxml
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    
     // Handles the action of clicking the 'Login' button.
     
    @FXML
    protected void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Basic input validation using control statements and String handling
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Username and password cannot be empty.");
            return;
        }

        // Check credentials against the user list from DataManager
        boolean loginSuccess = false;
        for (User user : DataManager.getUsers()) {
            if (user.getName().equals(username) && user.getPassword().equals(password)) {
                DataManager.setCurrentUser(user); // Set the logged-in user
                loginSuccess = true;
                break; // Exit the loop once a match is found
            }
        }

        if (loginSuccess) {
            statusLabel.setText("Login Successful!");
            System.out.println("Login successful for user: " + DataManager.getCurrentUser().getName());
            
            // Navigate to the dashboard view
            try {
                // Get the stage from the event source
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Parent dashboardView = FXMLLoader.load(getClass().getResource("../view/DashboardView.fxml"));
                Scene scene = new Scene(dashboardView);
                stage.setScene(scene);
                stage.setTitle("Dashboard");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                statusLabel.setText("Failed to load dashboard.");
            }
            
        } else {
            statusLabel.setText("Invalid username or password.");
        }
    }

    /**
     * Handles the action of clicking the 'Register' button.
     */
    @FXML
    protected void handleRegisterButtonAction(ActionEvent event) {
         System.out.println("Register button clicked. Navigating to registration screen.");
         // Navigation logic to the registration screen
         try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent registerView = FXMLLoader.load(getClass().getResource("../view/RegisterView.fxml"));
            Scene scene = new Scene(registerView);
            stage.setScene(scene);
            stage.setTitle("User Registration");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}