package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.MedicationTracker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class MyMedicationsController {

    @FXML
    private ListView<String> medicationList;


    private MedicationTracker medicationTracker;

    @FXML
    public void initialize() {
        //initialize mymedications screen
    }

    @FXML
    public void loadMedications() {
        // load user medications into list view
    }

    @FXML
    public void addMedication(ActionEvent event) {
        // switch to add medication screen
    }

    @FXML
    public void deleteMedication(ActionEvent event) {
        // remove the selected medication from medication tracker
    }

    @FXML
    public void editMedication(ActionEvent event) {
        // edit selected medication
    }

    @FXML
    public void goBack(ActionEvent event) {
        // return to home screen
    }
}
