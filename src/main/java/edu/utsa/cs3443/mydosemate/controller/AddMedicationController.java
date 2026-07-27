package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.Medication;
import edu.utsa.cs3443.mydosemate.model.MedicationTracker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddMedicationController {

    @FXML
    private TextField medicationIdField;

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
    private TextField startDateField;

    @FXML
    private TextField currentAmountField;

    @FXML
    private TextArea notesField;


    private MedicationTracker medicationTracker;

    @FXML
    public void initialize() {
        medicationTracker = new MedicationTracker();
    }

    @FXML
    public void saveMedication(ActionEvent event) {

        Medication medication = new Medication(
                medicationIdField.getText(),
                nameField.getText(),
                Integer.parseInt(dosageField.getText()),
                unitField.getText(),
                frequencyField.getText(),
                Integer.parseInt(timesPerDayField.getText()),
                startDateField.getText(),
                currentAmountField.getText(),
                notesField.getText()
        );

        medicationTracker.addMedication(medication);

        System.out.println("Medication added: " + medication.getName());
    }

    @FXML
    public void cancel(ActionEvent event) {

        medicationIdField.clear();
        nameField.clear();
        dosageField.clear();
        unitField.clear();
        frequencyField.clear();
        timesPerDayField.clear();
        startDateField.clear();
        currentAmountField.clear();
        notesField.clear();
    }
}