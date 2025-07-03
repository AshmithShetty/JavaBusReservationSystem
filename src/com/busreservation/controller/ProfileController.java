package com.busreservation.controller;

import com.busreservation.model.Booking;
import com.busreservation.model.User;
import com.busreservation.util.DataManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class ProfileController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmNewPasswordField;
    @FXML private Label passwordStatusLabel;
    @FXML private TableView<Booking> bookingHistoryTable;
    @FXML private TableColumn<Booking, String> bookingIdColumn;
    @FXML private TableColumn<Booking, String> busDetailsColumn;
    @FXML private TableColumn<Booking, LocalDate> journeyDateColumn;
    @FXML private TableColumn<Booking, String> seatsColumn;
    @FXML private TableColumn<Booking, Double> fareColumn;
    @FXML private TableColumn<Booking, Void> cancelColumn;

    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = DataManager.getCurrentUser();
        if (currentUser == null) {
            System.err.println("No user is logged in. Cannot display profile.");
            return;
        }
        nameField.setText(currentUser.getName());
        phoneField.setText(currentUser.getPhoneNumber());
        setupBookingHistoryTable();
        loadBookingHistory();
    }

    private void setupBookingHistoryTable() {
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        journeyDateColumn.setCellValueFactory(new PropertyValueFactory<>("journeyDate"));
        fareColumn.setCellValueFactory(new PropertyValueFactory<>("totalFare"));

        busDetailsColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBus().getOperatorName() + " (" + cellData.getValue().getBus().getBusType() + ")")
        );
        seatsColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.join(", ", cellData.getValue().getSeatNumbers()))
        );

        Callback<TableColumn<Booking, Void>, TableCell<Booking, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Booking, Void> call(final TableColumn<Booking, Void> param) {
                final TableCell<Booking, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("Cancel");
                    {
                        btn.setStyle("-fx-background-color: #ff6347; -fx-text-fill: white;");
                        btn.setOnAction((ActionEvent event) -> {
                            Booking bookingToCancel = getTableView().getItems().get(getIndex());
                            handleCancelBooking(bookingToCancel);
                        });
                    }
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };
        cancelColumn.setCellFactory(cellFactory);
    }

    private void loadBookingHistory() {
        ObservableList<Booking> bookings = FXCollections.observableArrayList(currentUser.getBookingHistory());
        bookingHistoryTable.setItems(bookings);
    }

    //Cancellation
    private void handleCancelBooking(Booking bookingToCancel) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Cancellation");
        confirmation.setHeaderText("Are you sure you want to cancel this booking?");
        confirmation.setContentText("Booking ID: " + bookingToCancel.getBookingId() + "\nThis action cannot be undone.");

        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            
            // We just call the central method in DataManager to remove the booking record.
            DataManager.removeBooking(bookingToCancel);

            // Refresh the table view to show the change.
            loadBookingHistory();

            System.out.println("Booking " + bookingToCancel.getBookingId() + " has been cancelled.");
            
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Cancellation Successful");
            info.setHeaderText(null);
            info.setContentText("Your booking has been successfully cancelled.");
            info.showAndWait();
        }
    }
    
    


    @FXML
    private void handleChangePassword(ActionEvent event) {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmNewPassword = confirmNewPasswordField.getText();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            setStatusLabel(Color.RED, "All password fields are required.");
            return;
        }
        if (!currentUser.getPassword().equals(currentPassword)) {
            setStatusLabel(Color.RED, "Current password is incorrect.");
            return;
        }
        if (newPassword.length() < 6) {
            setStatusLabel(Color.RED, "New password must be at least 6 characters long.");
            return;
        }
        if (!newPassword.equals(confirmNewPassword)) {
            setStatusLabel(Color.RED, "New passwords do not match.");
            return;
        }

        currentUser.setPassword(newPassword);
        setStatusLabel(Color.GREEN, "Password updated successfully!");
        System.out.println("Password for user " + currentUser.getName() + " has been changed.");

        currentPasswordField.clear();
        newPasswordField.clear();
        confirmNewPasswordField.clear();
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

    private void setStatusLabel(Color color, String text) {
        passwordStatusLabel.setTextFill(color);
        passwordStatusLabel.setText(text);
    }
}