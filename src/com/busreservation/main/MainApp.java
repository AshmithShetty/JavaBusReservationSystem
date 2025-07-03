package com.busreservation.main;

import com.busreservation.util.DataManager; 
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


 // The main entry point for the Bus Reservation System application.
 
public class MainApp extends Application {

    /**
     * This method is called when the application is launched. It sets up the
     * initial view and loads all necessary data.
     */
    @Override
    public void start(Stage primaryStage) {
        // Load all application data from files at the very beginning
        DataManager.loadData();
       

        try {
            // Load the FXML file for the login view.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/LoginView.fxml"));
            Parent root = loader.load();

            // Create a new scene with the loaded root layout.
            Scene scene = new Scene(root);

            // Set the title of the window.
            primaryStage.setTitle("Bus Reservation System - Login");
            primaryStage.setScene(scene);

            // Show the window.
            primaryStage.show();
        } catch (IOException e) {
            // This is a form of exception handling for UI loading errors.
            System.err.println("Failed to load the initial LoginView.fxml file.");
            e.printStackTrace();
        }
    }

    /**
     * This method is called when the application should stop. This is the ideal
     * place to save the application's state. We will save data on a separate
     * thread to ensure the UI doesn't freeze if saving takes time.
     */
    @Override
    public void stop() {
        System.out.println("Application is closing. Saving data...");

        // Create a new thread to run the saveData method.
        
        Thread saveThread = new Thread(DataManager::saveData);
        saveThread.start();

        try {
            // Wait for the save thread to complete before the application fully exits.
            saveThread.join(2000);
        } catch (InterruptedException e) {
            // This 'catch' block handles the case where the waiting is interrupted.
            System.err.println("The save data thread was interrupted.");
            e.printStackTrace();
            // Ensure the thread is interrupted if this happens.
            Thread.currentThread().interrupt();
        }
        System.out.println("Data saving process finished. Exiting application.");
    }

    /**
     * The main method, which is the entry point for a standard Java application.
     * It calls launch() to start the JavaFX application lifecycle.
     */
    public static void main(String[] args) {
        launch(args);
    }
}