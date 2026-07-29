package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.History;
import edu.utsa.cs3443.mydosemate.model.DoseLog;
import edu.utsa.cs3443.mydosemate.model.MedicationTracker;
import edu.utsa.cs3443.mydosemate.model.UserManager;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
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

    private MedicationTracker medicationTracker;

    private History history;

    @FXML
    private void initialize() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMMM dd, yyyy");

        dateLabel.setText(today.format(formatter));
        greetingLabel.setText("Hello!");

        try {
            UserManager userManager = new UserManager();
            userManager.loadUser();

            if (userManager.getUser() != null) {
                greetingLabel.setText(
                        "Hello, "
                                + userManager.getUser().getFirstName()
                                + "!"
                );
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        try {
            medicationTracker = new MedicationTracker();
            medicationTracker.loadMedications();
            history = medicationTracker.getHistory();
            history.loadDoseLogs();
            populateHistory();
        } catch (IOException exception) {
            exception.printStackTrace();

            Label errorLabel = new Label(
                    "Unable to load dose history:\n"
                            + exception.getMessage()
            );

            errorLabel.setWrapText(true);
            errorLabel.setStyle(
                    "-fx-text-fill: #C62828;"
                            + "-fx-font-size: 14px;"
            );

            historyContainer.getChildren().setAll(errorLabel);
        }
    }

    private void populateHistory() {
        historyContainer.getChildren().clear();

        List<DoseLog> logs = history.getDoseLogs();

        if (logs.isEmpty()) {
            Label emptyLabel = new Label("No dose history yet.");
            emptyLabel.setStyle(
                    "-fx-text-fill: #6b6b6b;"
                            + "-fx-font-size: 16px;"
            );

            historyContainer.getChildren().add(emptyLabel);
            return;
        }

        for (int i = logs.size() - 1; i >= 0; i--) {
            DoseLog log = logs.get(i);
            Label logLabel = new Label(medicationTracker.doseLogToSentence(log));

            logLabel.setWrapText(true);
            logLabel.setMaxWidth(Double.MAX_VALUE);
            logLabel.setStyle(
                    "-fx-background-color: #f4f6f8;"
                            + "-fx-background-radius: 10;"
                            + "-fx-padding: 12;"
                            + "-fx-font-size: 14px;"
                            + "-fx-text-fill: #000000;"
            );

            historyContainer.getChildren().add(logLabel);
        }
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
