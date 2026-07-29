package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.Medication;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MedicationCardController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label dosageLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label frequencyLabel;


    public void setMedication(Medication medication) {

        nameLabel.setText(medication.getName());

        dosageLabel.setText(
                medication.getDosage()
                        + " "
                        + medication.getUnit()
        );

        timeLabel.setText(
                medication.getScheduledTimes()
        );

        frequencyLabel.setText(
                medication.getFrequency()
        );
    }
}
