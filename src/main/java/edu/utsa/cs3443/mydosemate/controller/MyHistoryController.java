package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.Medication;
import edu.utsa.cs3443.mydosemate.model.MedicationTracker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MyHistoryController {

    @FXML
    private Label dateLabel;

    @FXML
    private Label greetingLabel;

    @FXML
    private VBox historyContainer;

    public void initialize() {
        // Current Date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        dateLabel.setText(today.format(formatter));

        // Greet User
//        try {
//            UserManager userManager = new UserManager();
//            userManager.loadUser();
//
//            if (userManager.getUser() != null) {
//                greetingLabel.setText(
//                        "Hello, " + userManager.getUser().getFirstName() + "!"
//                );
//            }
//
//        } catch (IOException e) {
//            greetingLabel.setText("Hello!");
//            e.printStackTrace();
//        }
    }


    @FXML
    private void switchScene(ActionEvent event, String fxml) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void goToDash(ActionEvent event) throws IOException {
        switchScene(event, "/edu/utsa/cs3443/mydosemate/view/home-dashboard.fxml");
    }
    @FXML
    private void goToMedicine(ActionEvent event) throws IOException {
        switchScene(event, "/edu/utsa/cs3443/mydosemate/view/my-medications.fxml");
    }
    @FXML
    private void goToHistory(ActionEvent event) throws IOException{
        switchScene(event, "/edu/utsa/cs3443/mydosemate/view/my-history.fxml");
    }
    @FXML
    private void addMedication(ActionEvent event) throws IOException{
        switchScene(event, "/edu/utsa/cs3443/mydosemate/view/add-medication.fxml");
    }
    @FXML
    private void goToSettings(ActionEvent event) throws IOException{
        switchScene(event, "/edu/utsa/cs3443/mydosemate/view/settings.fxml");
    }

}
