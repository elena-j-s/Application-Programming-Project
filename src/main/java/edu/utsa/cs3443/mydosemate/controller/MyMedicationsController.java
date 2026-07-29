package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.Medication;
import edu.utsa.cs3443.mydosemate.model.MedicationTracker;
import edu.utsa.cs3443.mydosemate.model.UserManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MyMedicationsController {

    @FXML
    private ListView<String> medicationList;

    private MedicationTracker medicationTracker;

    @FXML
    private Label dateLabel;

    @FXML
    private Label greetingLabel;

    @FXML
    private VBox medicationContainer;


    @FXML
    public void initialize() {
        // Current Date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        dateLabel.setText(today.format(formatter));

        try {
            UserManager userManager = new UserManager();
            userManager.loadUser();

            if (userManager.getUser() != null) {
                greetingLabel.setText(
                        "Hello, " + userManager.getUser().getFirstName() + "!"
                );
            }

        } catch (IOException e) {
            greetingLabel.setText("Hello!");
            e.printStackTrace();
        }

        displayMedications();
    }

    private void displayMedications() {

        MedicationTracker tracker = new MedicationTracker();

        List<Medication> medications = tracker.getMedications();

        for (Medication medication : medications) {
            createMedicationCard(medication);
        }
    }

    private void createMedicationCard(Medication medication) {

        Button medicationCard = new Button();


        String displayText =
                medication.getName() + ", " +
                        medication.getDosage() + " " + medication.getUnit() + ", " +
                        medication.getScheduledTimes();


        medicationCard.setText(displayText);

        // Card size
        medicationCard.setPrefWidth(510);
        medicationCard.setPrefHeight(50);


        // Card style
        medicationCard.setStyle(
                "-fx-background-color: white;" +
                        "-fx-font-family: 'Arial Rounded MT Bold';" +
                        "-fx-background-radius: 15;" +
                        "-fx-alignment: CENTER;" +
                        "-fx-padding: 15;"
        );


        medicationCard.setOnAction(event -> {
            showMedicationDetails(medication);
        });


        medicationContainer.getChildren().add(medicationCard);
    }

    private void showMedicationDetails(Medication medication) {

        System.out.println(
                "Selected: " + medication.getName()
        );

        // TO DO:
        // Load medication-details.fxml
        // Pass selected medication to controller
    }



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
        switchScene(event,"/edu/utsa/cs3443/mydosemate/view/settings.fxml");
    }

}
