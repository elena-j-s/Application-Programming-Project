package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.Medication;
import edu.utsa.cs3443.mydosemate.model.MedicationTracker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.LocalTime;

/**
 * Controls the medication-detail screen, allowing an existing medication to
 * be viewed, validated, updated, or deleted.
 */
public class MedicationCardController {

    private MedicationTracker medicationTracker;
    private Medication medication;

    @FXML
    private Label titleLabel;

    @FXML
    private TextField nameField;

    @FXML
    private TextField dosageField;

    @FXML
    private TextField unitField;

    @FXML
    private TextField frequencyField;

    @FXML
    private TextField timesPerDayField;

    @FXML
    private TextField scheduledTimesField;

    @FXML
    private TextField startDateField;

    @FXML
    private TextField currentAmountField;

    @FXML
    private TextArea notesArea;

    @FXML
    private Label nameErrorLabel;

    @FXML
    private Label dosageErrorLabel;

    @FXML
    private Label unitErrorLabel;

    @FXML
    private Label frequencyErrorLabel;

    @FXML
    private Label timesPerDayErrorLabel;

    @FXML
    private Label scheduledTimesErrorLabel;

    @FXML
    private Label startDateErrorLabel;

    @FXML
    private Label currentAmountErrorLabel;

    @FXML
    private Label notesErrorLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Button deleteButton;

    /** Initializes the medication model and clears any stale form messages. */
    @FXML
    public void initialize() {
        medicationTracker = new MedicationTracker();
        clearMessages();
    }

    /**
     * Supplies the medication represented by this detail screen and populates
     * the editable fields.
     *
     * @param medication the medication to display
     */
    public void setMedication(Medication medication) {
        this.medication = medication;
        populateFields();
    }

    /**
     * Validates the edited values and persists the updated medication.
     *
     * @param event the save-button action event
     */
    @FXML
    private void saveMedicationClicked(ActionEvent event) {
        try {
            clearMessages();

            if (medication == null) {
                showAlert("Missing Medication", "No medication was loaded.");
                return;
            }

            String name = textOrEmpty(nameField);
            String dosageText = textOrEmpty(dosageField);
            String unit = textOrEmpty(unitField);
            String frequency = textOrEmpty(frequencyField);
            String timesPerDayText = textOrEmpty(timesPerDayField);
            String scheduledTimesText = textOrEmpty(scheduledTimesField);
            String startDateText = textOrEmpty(startDateField);
            String currentAmountText = textOrEmpty(currentAmountField);
            String notes = notesArea == null || notesArea.getText() == null ? "" : notesArea.getText().trim();

            boolean hasErrors = false;

            if (name.isEmpty()) {
                setError(nameErrorLabel, "Medication name is required.");
                hasErrors = true;
            }

            double dosage = 0.0;
            try {
                dosage = Double.parseDouble(dosageText);
                if (dosage <= 0) {
                    setError(dosageErrorLabel, "Dosage must be greater than zero.");
                    hasErrors = true;
                }
            } catch (NumberFormatException exception) {
                setError(dosageErrorLabel, "Enter a valid dosage amount.");
                hasErrors = true;
            }

            if (unit.isEmpty()) {
                setError(unitErrorLabel, "Unit is required.");
                hasErrors = true;
            }

            if (frequency.isEmpty()) {
                setError(frequencyErrorLabel, "Frequency is required.");
                hasErrors = true;
            }

            int timesPerDay = 0;
            try {
                timesPerDay = Integer.parseInt(timesPerDayText);
                if (timesPerDay <= 0) {
                    setError(timesPerDayErrorLabel, "Times per day must be greater than zero.");
                    hasErrors = true;
                }
            } catch (NumberFormatException exception) {
                setError(timesPerDayErrorLabel, "Enter a whole number for times per day.");
                hasErrors = true;
            }

            String normalizedScheduledTimes = normalizeScheduledTimes(scheduledTimesText);
            if (normalizedScheduledTimes.isEmpty()) {
                setError(scheduledTimesErrorLabel, "Scheduled times are required.");
                hasErrors = true;
            } else if (!hasErrors) {
                try {
                    validateScheduledTimes(normalizedScheduledTimes, timesPerDay);
                } catch (IllegalArgumentException exception) {
                    setError(scheduledTimesErrorLabel, exception.getMessage());
                    hasErrors = true;
                }
            }

            if (startDateText.isEmpty()) {
                setError(startDateErrorLabel, "Start date is required.");
                hasErrors = true;
            } else {
                try {
                    LocalDate.parse(startDateText);
                } catch (DateTimeParseException exception) {
                    setError(startDateErrorLabel, "Use YYYY-MM-DD for the start date.");
                    hasErrors = true;
                }
            }

            int currentAmount = 0;
            try {
                currentAmount = Integer.parseInt(currentAmountText);
                if (currentAmount < 0) {
                    setError(currentAmountErrorLabel, "Current amount cannot be negative.");
                    hasErrors = true;
                }
            } catch (NumberFormatException exception) {
                setError(currentAmountErrorLabel, "Enter a whole number for current amount.");
                hasErrors = true;
            }

            if (hasErrors) {
                return;
            }

            Medication updatedMedication = new Medication(
                    medication.getMedicationId(),
                    name,
                    dosage,
                    unit,
                    frequency,
                    timesPerDay,
                    normalizedScheduledTimes,
                    startDateText,
                    currentAmount,
                    notes
            );

            boolean updated = medicationTracker.updateMedication(updatedMedication);
            if (!updated) {
                showAlert("Unable to Save", "The selected medication could not be found.");
                return;
            }
            medication = updatedMedication;
            populateFields();
            statusLabel.setText("Medication updated.");
        } catch (IllegalArgumentException exception) {
            clearMessages();
            showAlert("Invalid Medication", exception.getMessage());
        } catch (IOException exception) {
            clearMessages();
            showAlert("Unable to Save", exception.getMessage());
        } catch (RuntimeException exception) {
            clearMessages();
            showAlert("Unable to Save", "Unexpected error while saving medication.");
        }
    }

    /**
     * Confirms and permanently removes the displayed medication.
     *
     * @param event the delete-button action event
     */
    @FXML
    private void deleteMedicationClicked(ActionEvent event) {
        if (medication == null) {
            showAlert("Missing Medication", "No medication was loaded.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Medication");
        confirm.setHeaderText(null);
        confirm.setContentText("Delete " + medication.getName() + "?");

        if (confirm.showAndWait().orElse(null) != javafx.scene.control.ButtonType.OK) {
            return;
        }

        try {
            boolean removed = medicationTracker.removeMedication(medication.getMedicationId());
            if (!removed) {
                showAlert("Unable to Delete", "The selected medication could not be found.");
                return;
            }
            goBack(event);
        } catch (IOException exception) {
            showAlert("Unable to Delete", exception.getMessage());
        } catch (RuntimeException exception) {
            showAlert("Unable to Delete", "Unexpected error while deleting medication.");
        }
    }

    /**
     * Returns to the medication list, displaying an alert if navigation fails.
     *
     * @param event the navigation action event
     */
    @FXML
    private void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/edu/utsa/cs3443/mydosemate/view/my-medications.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException exception) {
            showAlert("Navigation Error", "Unable to open the medications list.");
        } catch (RuntimeException exception) {
            showAlert("Navigation Error", "Unable to open the medications list.");
        }
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
     * Opens the dose-history screen.
     *
     * @param event the navigation action event
     */
    @FXML
    private void goToHistory(ActionEvent event) {
        switchScene(event, "/edu/utsa/cs3443/mydosemate/view/my-history.fxml");
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

    /**
     * Replaces the current scene with the requested FXML view.
     *
     * @param event the action event used to locate the current stage
     * @param fxml the classpath location of the destination FXML file
     */
    private void switchScene(ActionEvent event, String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException exception) {
            showAlert("Navigation Error", "Unable to open the requested screen.");
        } catch (RuntimeException exception) {
            showAlert("Navigation Error", "Unable to open the requested screen.");
        }
    }

    /** Copies the current medication values into the detail form. */
    private void populateFields() {
        if (medication == null) {
            return;
        }

        if (titleLabel != null) {
            titleLabel.setText(medication.getName());
        }

        if (nameField != null) {
            nameField.setText(medication.getName());
        }
        if (dosageField != null) {
            dosageField.setText(formatDosageAmount(medication.getDosage()));
        }
        if (unitField != null) {
            unitField.setText(medication.getUnit());
        }
        if (frequencyField != null) {
            frequencyField.setText(medication.getFrequency());
        }
        if (timesPerDayField != null) {
            timesPerDayField.setText(String.valueOf(medication.getTimesPerDay()));
        }
        if (scheduledTimesField != null) {
            scheduledTimesField.setText(medication.getScheduledTimes());
        }
        if (startDateField != null) {
            startDateField.setText(medication.getStartDate());
        }
        if (currentAmountField != null) {
            currentAmountField.setText(String.valueOf(medication.getCurrentAmount()));
        }
        if (notesArea != null) {
            notesArea.setText(medication.getNotes() == null ? "" : medication.getNotes());
        }
    }

    /** Clears all field-level validation messages and the status message. */
    private void clearMessages() {
        setError(nameErrorLabel, "");
        setError(dosageErrorLabel, "");
        setError(unitErrorLabel, "");
        setError(frequencyErrorLabel, "");
        setError(timesPerDayErrorLabel, "");
        setError(scheduledTimesErrorLabel, "");
        setError(startDateErrorLabel, "");
        setError(currentAmountErrorLabel, "");
        setError(notesErrorLabel, "");
        if (statusLabel != null) {
            statusLabel.setText("");
        }
    }

    /**
     * Writes a validation message to a label when that label is available.
     *
     * @param label the label associated with an input field
     * @param message the message to display, or {@code null} to clear it
     */
    private void setError(Label label, String message) {
        if (label != null) {
            label.setText(message == null ? "" : message);
        }
    }

    /**
     * Displays an error dialog.
     *
     * @param title the dialog title
     * @param message the error message to display
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Reads and trims a text field safely.
     *
     * @param field the field to read
     * @return the trimmed value, or an empty string when unavailable
     */
    private String textOrEmpty(TextField field) {
        return field == null || field.getText() == null ? "" : field.getText().trim();
    }

    /**
     * Trims each semicolon-separated schedule time and removes blank entries.
     *
     * @param scheduledTimesText the schedule text entered by the user
     * @return normalized schedule times separated by semicolons
     */
    private String normalizeScheduledTimes(String scheduledTimesText) {
        if (scheduledTimesText == null || scheduledTimesText.trim().isEmpty()) {
            return "";
        }

        String[] parts = scheduledTimesText.split(";", -1);
        StringBuilder builder = new StringBuilder();

        for (String part : parts) {
            String value = part.trim();
            if (value.isEmpty()) {
                continue;
            }

            if (builder.length() > 0) {
                builder.append(";");
            }

            builder.append(value);
        }

        return builder.toString();
    }

    /**
     * Verifies that the schedule contains the required number of valid ISO
     * local times.
     *
     * @param scheduledTimes semicolon-separated times in {@code HH:mm} format
     * @param timesPerDay the expected number of scheduled times
     * @throws IllegalArgumentException if the count or time format is invalid
     */
    private void validateScheduledTimes(String scheduledTimes, int timesPerDay) {
        String[] times = scheduledTimes.split(";", -1);

        if (times.length != timesPerDay) {
            throw new IllegalArgumentException("Scheduled time count must match times per day.");
        }

        for (String time : times) {
            try {
                LocalTime.parse(time.trim());
            } catch (DateTimeParseException exception) {
                throw new IllegalArgumentException("Scheduled times must use HH:mm format.");
            }
        }
    }

    /**
     * Formats a dosage without a decimal suffix when it is a whole number.
     *
     * @param dosage the dosage value
     * @return the compact dosage text
     */
    private String formatDosageAmount(double dosage) {
        if (dosage == Math.rint(dosage)) {
            return String.valueOf((long) dosage);
        }

        return String.valueOf(dosage);
    }
}
