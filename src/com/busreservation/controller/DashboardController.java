package com.busreservation.controller;

import com.busreservation.model.Bus;
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
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private ComboBox<String> sourceComboBox;
    @FXML private ComboBox<String> destinationComboBox;
    @FXML private DatePicker datePicker;
    @FXML private RadioButton seaterRadio;
    @FXML private RadioButton sleeperRadio;
    @FXML private ToggleGroup busTypeGroup;
    @FXML private CheckBox acCheckBox;
    @FXML private TableView<Bus> busTableView;
    @FXML private TableColumn<Bus, String> operatorColumn;
    @FXML private TableColumn<Bus, String> busTypeColumn;
    @FXML private TableColumn<Bus, String> acColumn;
    @FXML private TableColumn<Bus, Double> fareColumn;
    @FXML private TableColumn<Bus, Void> actionColumn;

    private ObservableList<Bus> allBuses;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        if (DataManager.getCurrentUser() != null) {
            welcomeLabel.setText("Welcome, " + DataManager.getCurrentUser().getName() + "!");
        }

        allBuses = FXCollections.observableArrayList(DataManager.getBuses());
        sourceComboBox.setItems(allBuses.stream().map(bus -> bus.getRoute().getSource()).distinct().sorted().collect(Collectors.toCollection(FXCollections::observableArrayList)));
        destinationComboBox.setItems(allBuses.stream().map(bus -> bus.getRoute().getDestination()).distinct().sorted().collect(Collectors.toCollection(FXCollections::observableArrayList)));

        datePicker.setValue(LocalDate.now());

        setupTableColumns();
        
        busTableView.setItems(allBuses);
    }

    private void setupTableColumns() {
        operatorColumn.setCellValueFactory(new PropertyValueFactory<>("operatorName"));
        busTypeColumn.setCellValueFactory(new PropertyValueFactory<>("busType"));
        fareColumn.setCellValueFactory(new PropertyValueFactory<>("fare"));

        acColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().isAC() ? "AC" : "Non-AC")
        );

        // Custom cell factory to create a "Book Seats" button for each row.
        Callback<TableColumn<Bus, Void>, TableCell<Bus, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Bus, Void> call(final TableColumn<Bus, Void> param) {
                final TableCell<Bus, Void> cell = new TableCell<>() {
                    private final Button btn = new Button("Book Seats");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Bus bus = getTableView().getItems().get(getIndex());
                            
                            // Get the journey date from the DatePicker
                            LocalDate selectedDate = datePicker.getValue();

                            // Validate that a date has been selected
                            if (selectedDate == null) {
                                showAlert(Alert.AlertType.ERROR, "Date Not Selected", "Please select a journey date before booking.");
                                return;
                            }
                            
                            // Navigate to the seat selection screen
                            try {
                                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/SeatSelectionView.fxml"));
                                Parent seatSelectionView = loader.load();
                                
                                // Get the controller of the next screen
                                SeatSelectionController controller = loader.getController();
                                
                                // Pass BOTH the selected bus and the selected date
                                controller.initData(bus, selectedDate);
                                
                                // Show the new scene
                                Scene scene = new Scene(seatSelectionView);
                                stage.setScene(scene);
                                stage.setTitle("Select Your Seats");
                                stage.show();

                            } catch (IOException e) {
                                e.printStackTrace();
                                showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load the seat selection screen.");
                            }
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
        actionColumn.setCellFactory(cellFactory);
       
    }

    @FXML
    private void handleSearchAction(ActionEvent event) {
        String source = sourceComboBox.getValue();
        String destination = destinationComboBox.getValue();
        LocalDate date = datePicker.getValue();

        if (source == null || destination == null || date == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select source, destination, and date.");
            return;
        }

        ObservableList<Bus> filteredBuses = allBuses.stream()
            .filter(bus -> bus.getRoute().getSource().equals(source))
            .filter(bus -> bus.getRoute().getDestination().equals(destination))
            .filter(bus -> !acCheckBox.isSelected() || bus.isAC())
            .filter(bus -> {
                RadioButton selectedRadio = (RadioButton) busTypeGroup.getSelectedToggle();
                if (selectedRadio == null) return true;
                return bus.getBusType().equalsIgnoreCase(selectedRadio.getText());
            })
            .collect(Collectors.toCollection(FXCollections::observableArrayList));

        busTableView.setItems(filteredBuses);
    }
    
    @FXML
    private void handleClearFilters(ActionEvent event) {
        busTypeGroup.selectToggle(null);
        acCheckBox.setSelected(false);
        handleSearchAction(event); // Re-apply the main search filters
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("Logout clicked.");
        DataManager.logout();
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

    @FXML
    private void handleMyProfile(ActionEvent event) {
        System.out.println("My Profile clicked.");
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent profileView = FXMLLoader.load(getClass().getResource("../view/ProfileView.fxml"));
            Scene scene = new Scene(profileView);
            stage.setScene(scene);
            stage.setTitle("My Profile");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Load Error", "Failed to load the profile screen.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}