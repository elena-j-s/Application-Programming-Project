package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.DoseLogManager;
import edu.utsa.cs3443.mydosemate.model.Medication;
import edu.utsa.cs3443.mydosemate.model.MedicationTracker;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class HomeScreenController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private ListView<Medication> scheduledMedicationListView;

    @FXML
    private Label messageLabel;

    private MedicationTracker medicationTracker;

    private final DoseLogManager doseLogManager =
            new DoseLogManager();

    @FXML
    private void initialize() {
        scheduledMedicationListView.setCellFactory(
                listView -> new ListCell<Medication>() {

                    @Override
                    protected void updateItem(
                            Medication medication,
                            boolean empty) {

                        super.updateItem(medication, empty);

                        if (empty || medication == null) {
                            setText(null);
                            return;
                        }

                        String displayTimes =
                                medication.getScheduledTimes()
                                        .replace(";", " and ");

                        setText(
                                medication.getName()
                                        + " — "
                                        + formatDosage(
                                                medication.getDosage())
                                        + " "
                                        + medication.getUnit()
                                        + " — "
                                        + displayTimes
                                        + " — "
                                        + medication.getCurrentAmount()
                                        + " doses left"
                        );
                    }
                }
        );
    }

    public void setMedicationTracker(
            MedicationTracker medicationTracker) {

        this.medicationTracker = medicationTracker;
        refreshMedicationList();
    }

    /**
     * Loads medications that are scheduled for today.
     */
    public void refreshMedicationList() {
        scheduledMedicationListView.getItems().clear();

        if (medicationTracker == null) {
            return;
        }

        List<Medication> todaysMedications =
                new ArrayList<Medication>();

        for (Medication medication
                : medicationTracker.getMedications()) {

            if (isScheduledToday(medication)) {
                todaysMedications.add(medication);
            }
        }

        scheduledMedicationListView
                .getItems()
                .addAll(todaysMedications);
    }

    @FXML
    private void handleTakeMedication() {
        Medication selectedMedication =
                getSelectedMedication();

        if (selectedMedication == null) {
            messageLabel.setText(
                    "Please select a medication first."
            );
            return;
        }

        LocalDateTime scheduledTime =
                getClosestScheduledTime(selectedMedication);

        int previousAmount =
                selectedMedication.getCurrentAmount();

        boolean medicationAmountChanged = false;

        try {
            if (doseLogManager.isDoseRecorded(
                    selectedMedication.getMedicationId(),
                    scheduledTime)) {

                messageLabel.setText(
                        "This scheduled dose has already "
                                + "been recorded."
                );
                return;
            }

            boolean taken =
                    medicationTracker.takeMedication(
                            selectedMedication.getMedicationId()
                    );

            if (!taken) {
                messageLabel.setText(
                        "This medication has no doses remaining."
                );
                return;
            }

            medicationAmountChanged = true;

            boolean recorded =
                    doseLogManager.recordTaken(
                            selectedMedication,
                            scheduledTime
                    );

            if (!recorded) {
                restoreMedicationAmount(
                        selectedMedication,
                        previousAmount
                );

                messageLabel.setText(
                        "This scheduled dose has already "
                                + "been recorded."
                );
                return;
            }

            refreshMedicationList();

            messageLabel.setText(
                    selectedMedication.getName()
                            + " was marked as taken. "
                            + selectedMedication.getCurrentAmount()
                            + " doses remain."
            );

        } catch (IOException exception) {
            if (medicationAmountChanged) {
                try {
                    restoreMedicationAmount(
                            selectedMedication,
                            previousAmount
                    );
                } catch (IOException rollbackException) {
                    exception.addSuppressed(
                            rollbackException
                    );
                }
            }

            messageLabel.setText(
                    "The taken dose could not be saved."
            );
        }
    }

    @FXML
    private void handleMissedMedication() {
        Medication selectedMedication =
                getSelectedMedication();

        if (selectedMedication == null) {
            messageLabel.setText(
                    "Please select a medication first."
            );
            return;
        }

        LocalDateTime scheduledTime =
                getClosestScheduledTime(selectedMedication);

        try {
            if (!medicationTracker.markMedicationMissed(
                    selectedMedication.getMedicationId())) {

                messageLabel.setText(
                        "The selected medication was not found."
                );
                return;
            }

            boolean recorded =
                    doseLogManager.recordMissed(
                            selectedMedication,
                            scheduledTime
                    );

            if (!recorded) {
                messageLabel.setText(
                        "This scheduled dose has already "
                                + "been recorded."
                );
                return;
            }

            messageLabel.setText(
                    selectedMedication.getName()
                            + " was marked as missed. "
                            + "The remaining supply was not changed."
            );

        } catch (IOException exception) {
            messageLabel.setText(
                    "The missed dose could not be saved."
            );
        }
    }

    private Medication getSelectedMedication() {
        return scheduledMedicationListView
                .getSelectionModel()
                .getSelectedItem();
    }

    private void restoreMedicationAmount(
            Medication medication,
            int previousAmount) throws IOException {

        medication.setCurrentAmount(previousAmount);
        medicationTracker.saveMedications();
        refreshMedicationList();
    }

    /**
     * Determines whether a medication belongs on today's schedule.
     */
    private boolean isScheduledToday(
            Medication medication) {

        LocalDate today = LocalDate.now();
        LocalDate startDate;

        try {
            startDate = LocalDate.parse(
                    medication.getStartDate()
            );
        } catch (RuntimeException exception) {
            return false;
        }

        if (today.isBefore(startDate)) {
            return false;
        }

        String frequency =
                medication.getFrequency()
                        .trim()
                        .toLowerCase();

        if (frequency.contains("weekly")) {
            long daysSinceStart =
                    ChronoUnit.DAYS.between(
                            startDate,
                            today
                    );

            return daysSinceStart % 7 == 0;
        }

        if (frequency.contains("once")) {
            return today.equals(startDate);
        }

        // Daily and unknown frequency values are displayed.
        return true;
    }

    /**
     * Finds the scheduled time closest to the current time.
     *
     * This is needed because one medication can contain multiple
     * scheduled times in one string, such as 08:00;20:00.
     */
    private LocalDateTime getClosestScheduledTime(
            Medication medication) {

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        String[] scheduledTimes =
                medication.getScheduledTimes().split(";");

        LocalDateTime closestTime = null;
        long smallestDifference = Long.MAX_VALUE;

        for (String scheduledTimeText : scheduledTimes) {
            LocalTime time = LocalTime.parse(
                    scheduledTimeText.trim()
            );

            LocalDateTime candidate =
                    LocalDateTime.of(today, time);

            long difference = Math.abs(
                    Duration.between(
                            now,
                            candidate
                    ).toMinutes()
            );

            if (difference < smallestDifference) {
                smallestDifference = difference;
                closestTime = candidate;
            }
        }

        if (closestTime == null) {
            return now.withSecond(0).withNano(0);
        }

        return closestTime;
    }

    private String formatDosage(double dosage) {
        if (dosage == Math.rint(dosage)) {
            return String.valueOf((long) dosage);
        }

        return String.valueOf(dosage);
    }

    /**
     * These temporary bodies allow the uploaded dashboard FXML
     * to load without missing-handler errors.
     */

    /*
    @FXML
    private void handleOpenAddMedication() {
        messageLabel.setText(
                "Add Medication navigation is handled "
                        + "by the medication screen."
        );
    }

    @FXML
    private void handleOpenMyMedications() {
        messageLabel.setText(
                "My Medications navigation is handled "
                        + "by the medication screen."
        );
    }

    @FXML
    private void handleOpenHistory() {
        messageLabel.setText(
                "History is still being completed."
        );
    }

    @FXML
    private void handleOpenSettings() {
        messageLabel.setText(
                "Settings is still being completed."
        );
    }
    */
}