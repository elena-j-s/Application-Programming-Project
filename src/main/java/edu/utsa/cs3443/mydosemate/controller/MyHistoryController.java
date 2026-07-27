package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.Medication;
import edu.utsa.cs3443.mydosemate.model.MedicationTracker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class MyHistoryController {

    @FXML
    private TextArea historyArea;


    private MedicationTracker medicationTracker;

    @FXML
    public void initialize() {

    }

    @FXML
    public void loadHistory() {

    }

    @FXML
    public void clearHistory(ActionEvent event) {

    }

    @FXML
    public void goBack(ActionEvent event) {
        //return to home screen
    }
}
