package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.Medication;
import edu.utsa.cs3443.mydosemate.model.MedicationTracker;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddMedicationController {

    // These connect to controls in add-medication.fxml
    @FXML
    private TextField nameField;

    @FXML
    private TextField dosageField;

    @FXML
    private ComboBox<String> unitComboBox;

    @FXML
    private ComboBox<String> frequencyComboBox;

    @FXML
    private Spinner<Integer> timesPerDaySpinner;

    @FXML
    private TextField scheduledTimesField;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TextField currentAmountField;

    @FXML
    private TextArea notesArea;

    @FXML
    private Label errorLabel;

    // This is supplied by the application or previous controller
    private MedicationTracker medicationTracker;

    public void setMedicationTracker(
            MedicationTracker medicationTracker) {
        this.medicationTracker = medicationTracker;
    }

    // Other methods go below this point
}