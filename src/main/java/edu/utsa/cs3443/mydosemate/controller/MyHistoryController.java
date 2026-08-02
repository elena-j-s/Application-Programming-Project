package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.History;
import edu.utsa.cs3443.mydosemate.model.DoseLog;
import edu.utsa.cs3443.mydosemate.model.MedicationTracker;
import edu.utsa.cs3443.mydosemate.model.UserManager;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controls the history screen by loading persisted dose logs and presenting
 * them in reverse chronological insertion order.
 */
public class MyHistoryController {

    @FXML
    private Label dateLabel;

    @FXML
    private Label greetingLabel;

    @FXML
    private VBox historyContainer;

    private MedicationTracker medicationTracker;

    private History history;

    /**
     * Loads the date, user greeting, medications, and dose history before
     * populating the history view.
     */
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
            UiErrorHandler.showError(
                    "Profile Error",
                    "Your greeting could not be loaded, but history is still available.",
                    exception
            );
        } catch (RuntimeException exception) {
            UiErrorHandler.showError(
                    "Profile Error",
                    "Your greeting could not be loaded, but history is still available.",
                    exception
            );
        }

        try {
            medicationTracker = new MedicationTracker();
            medicationTracker.loadMedications();
            history = medicationTracker.getHistory();
            history.loadDoseLogs();
            populateHistory();
        } catch (IOException exception) {
            showHistoryError(exception);
        } catch (RuntimeException exception) {
            showHistoryError(exception);
        }
    }

    /**
     * Replaces the history contents with an error message and reports the
     * underlying failure without closing the screen.
     *
     * @param exception the history-loading failure
     */
    private void showHistoryError(Throwable exception) {
        String detail = exception == null || exception.getMessage() == null
                ? "The saved history could not be read."
                : exception.getMessage();
        Label errorLabel = new Label("Unable to load dose history:\n" + detail);

        errorLabel.setWrapText(true);
        errorLabel.setStyle(
                "-fx-text-fill: #C62828;"
                        + "-fx-font-size: 14px;"
        );

        historyContainer.getChildren().setAll(errorLabel);
        UiErrorHandler.showError(
                "History Error",
                "Dose history could not be loaded. Please check the data files.",
                exception
        );
    }

    /** Builds the visible history list from the currently loaded dose logs. */
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

    /**
     * Replaces the current scene with the requested FXML view.
     *
     * @param event the action event used to locate the current stage
     * @param fxml the classpath location of the destination FXML file
     */
    @FXML
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
     * Opens the medication list.
     *
     * @param event the navigation action event
     */
    @FXML
    private void goToMedicine(ActionEvent event) {
        switchScene(event, "/edu/utsa/cs3443/mydosemate/view/my-medications.fxml");
    }

    /**
     * Reloads the dose-history screen.
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
        switchScene(event, "/edu/utsa/cs3443/mydosemate/view/settings.fxml");
    }

}
