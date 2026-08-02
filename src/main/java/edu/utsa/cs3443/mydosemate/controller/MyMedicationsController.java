package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.Medication;
import edu.utsa.cs3443.mydosemate.model.MedicationTracker;
import edu.utsa.cs3443.mydosemate.model.UserManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

/**
 * Controls the medication-list screen by loading saved medications, rendering
 * a selectable card for each one, and handling application navigation.
 */
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


    /**
     * Initializes the medication model, date and greeting, then displays all
     * saved medications.
     */
    @FXML
    public void initialize() {
        medicationTracker = new MedicationTracker();

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
            UiErrorHandler.showError(
                    "Profile Error",
                    "Your greeting could not be loaded, but medications are still available.",
                    e
            );
        } catch (RuntimeException e) {
            greetingLabel.setText("Hello!");
            UiErrorHandler.showError(
                    "Profile Error",
                    "Your greeting could not be loaded, but medications are still available.",
                    e
            );
        }

        try {
            displayMedications();
        } catch (RuntimeException exception) {
            medicationContainer.getChildren().setAll(
                    new Label("Unable to display saved medications."));
            UiErrorHandler.showError(
                    "Medication Error",
                    "Saved medications could not be displayed. Please check the data files.",
                    exception
            );
        }
    }

    /** Rebuilds the medication-card container from the model's current list. */
    private void displayMedications() {
        medicationContainer.getChildren().clear();
        List<Medication> medications = medicationTracker.getMedications();

        for (Medication medication : medications) {
            createMedicationCard(medication);
        }
    }

    /**
     * Creates and displays a selectable card for one medication.
     *
     * @param medication the medication represented by the card
     */
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

    /**
     * Opens the detail screen for a selected medication.
     *
     * @param medication the medication to pass to the detail controller
     */
    private void showMedicationDetails(Medication medication) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/edu/utsa/cs3443/mydosemate/view/medication-card.fxml")
            );
            Parent root = loader.load();

            MedicationCardController controller = loader.getController();
            controller.setMedication(medication);

            Stage stage = (Stage) medicationContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException exception) {
            UiErrorHandler.showError(
                    "Medication Error",
                    "The selected medication could not be opened.",
                    exception
            );
        } catch (RuntimeException exception) {
            UiErrorHandler.showError(
                    "Medication Error",
                    "The selected medication could not be opened.",
                    exception
            );
        }
    }



    /**
     * Replaces the current scene with the requested FXML view.
     *
     * @param event the action event used to locate the current stage
     * @param fxml the classpath location of the destination FXML file
     */
    private void switchScene(ActionEvent event, String fxml) {
        UiErrorHandler.switchScene(event, getClass(), fxml);
    }

    /**
     * Opens the home dashboard.
     *
     * @param event the navigation action event
     */
    @FXML
    private void goToDash(ActionEvent event) {
        switchScene(event, "/edu/utsa/cs3443/mydosemate/view/home-dashboard.fxml");
    }

    /**
     * Reloads the medication list.
     *
     * @param event the navigation action event
     */
    @FXML
    private void goToMedicine(ActionEvent event) {
        switchScene(event, "/edu/utsa/cs3443/mydosemate/view/my-medications.fxml");
    }

    /**
     * Opens the dose-history screen.
     *
     * @param event the navigation action event
     */
    @FXML
    private void goToHistory(ActionEvent event) {
        switchScene(event, "/edu/utsa/cs3443/mydosemate/view/my-history.fxml");
    }

    /**
     * Opens the add-medication form.
     *
     * @param event the navigation action event
     */
    @FXML
    private void addMedication(ActionEvent event) {
        switchScene(event, "/edu/utsa/cs3443/mydosemate/view/add-medication.fxml");
    }

    /**
     * Opens the settings screen.
     *
     * @param event the navigation action event
     */
    @FXML
    private void goToSettings(ActionEvent event) {
        switchScene(event,"/edu/utsa/cs3443/mydosemate/view/settings.fxml");
    }

}
