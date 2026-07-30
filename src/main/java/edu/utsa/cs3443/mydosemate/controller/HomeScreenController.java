package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.MedicationTracker;
import edu.utsa.cs3443.mydosemate.model.ScheduledDose;
import edu.utsa.cs3443.mydosemate.model.UserManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HomeScreenController {

    @FXML
    private Label greetingLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label takenDataLabel;

    @FXML
    private Label missedDataLabel;

    @FXML
    private Label upcomingDataLabel;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private VBox todaysDosesContainer;

    @FXML
    private Label doseMessageLabel;

    private MedicationTracker medicationTracker;

    @FXML
    public void initialize() {
        // Current Date
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        dateLabel.setText(today.format(formatter));

        // Greet User
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
            e.printStackTrace();
        }

        medicationTracker = new MedicationTracker();

        refreshDashboard();
    }

    /** Refreshes both the daily dose list and progress summary. */
    private void refreshDashboard() {
        refreshTodaysDoses();
        updateProgress();
    }

    /** Builds one dashboard row for every dose scheduled today. */
    private void refreshTodaysDoses() {
        todaysDosesContainer.getChildren().clear();

        try {
            List<ScheduledDose> doses =
                    medicationTracker.getTodaysScheduledDoses();

            if (doses.isEmpty()) {
                Label emptyLabel = new Label(
                        "No medications are scheduled for today.");
                emptyLabel.setStyle(
                        "-fx-font-size: 15px; -fx-text-fill: #6b6b6b;");
                todaysDosesContainer.getChildren().add(emptyLabel);
                return;
            }

            for (ScheduledDose dose : doses) {
                todaysDosesContainer.getChildren().add(
                        createDoseRow(dose));
            }
        } catch (IOException exception) {
            doseMessageLabel.setText(
                    "Today's doses could not be loaded.");
            exception.printStackTrace();
        }
    }

    /** Creates the visual row and action button for one scheduled dose. */
    private HBox createDoseRow(final ScheduledDose dose) {
        DateTimeFormatter timeFormatter =
                DateTimeFormatter.ofPattern("h:mm a");

        Label nameLabel = new Label(dose.getMedication().getName());
        nameLabel.setStyle(
                "-fx-font-size: 16px; -fx-font-weight: bold;");
        nameLabel.setTextFill(Color.BLACK);

        String dosage = formatDosage(dose.getMedication().getDosage())
                + " " + dose.getMedication().getUnit();
        Label detailsLabel = new Label(
                dosage + " • "
                        + dose.getScheduledTime().format(timeFormatter));
        detailsLabel.setStyle(
                "-fx-font-size: 13px; -fx-text-fill: #6b6b6b;");

        VBox information = new VBox(3.0, nameLabel, detailsLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label statusLabel = new Label(formatStatus(dose));
        statusLabel.setStyle(statusStyle(dose.getStatus()));

        Button takenButton = new Button("Mark Taken");
        takenButton.getStyleClass().add("dose-action-button");
        takenButton.setDisable(
                dose.getStatus() == ScheduledDose.Status.TAKEN);
        takenButton.setOnAction(event -> markDoseTaken(dose));

        VBox actionArea = new VBox(5.0, statusLabel, takenButton);
        actionArea.setAlignment(Pos.CENTER_RIGHT);

        HBox row = new HBox(10.0, information, spacer, actionArea);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(9.0));
        row.setStyle(
                "-fx-background-color: #f4f6f8;"
                        + "-fx-background-radius: 10;"
                        + "-fx-border-color: #dde2e8;"
                        + "-fx-border-radius: 10;");

        return row;
    }

    /** Marks the selected schedule slot as taken and refreshes the screen. */
    private void markDoseTaken(ScheduledDose dose) {
        try {
            boolean recorded = medicationTracker.takeScheduledDose(
                    dose.getMedication().getMedicationId(),
                    dose.getScheduledTime());

            if (recorded) {
                doseMessageLabel.setText(
                        dose.getMedication().getName()
                                + " was marked as taken.");
            } else if (dose.getMedication().getCurrentAmount() <= 0) {
                doseMessageLabel.setText(
                        "There are no doses of "
                                + dose.getMedication().getName()
                                + " remaining.");
            } else {
                doseMessageLabel.setText(
                        "That dose was already marked as taken.");
            }

            refreshDashboard();
        } catch (IOException exception) {
            doseMessageLabel.setText(
                    "The dose could not be saved. Please try again.");
            exception.printStackTrace();
        }
    }

    private String formatStatus(ScheduledDose dose) {
        if (dose.getStatus() == ScheduledDose.Status.TAKEN
                && dose.getTakenTime() != null) {
            return "Taken " + dose.getTakenTime().format(
                    DateTimeFormatter.ofPattern("h:mm a"));
        }

        if (dose.getStatus() == ScheduledDose.Status.MISSED) {
            return "Missed";
        }

        return "Upcoming";
    }

    private String statusStyle(ScheduledDose.Status status) {
        String color = "#5B85EB";

        if (status == ScheduledDose.Status.TAKEN) {
            color = "#17a315";
        } else if (status == ScheduledDose.Status.MISSED) {
            color = "#c62828";
        }

        return "-fx-font-size: 13px; -fx-font-weight: bold;"
                + "-fx-text-fill: " + color + ";";
    }

    private String formatDosage(double dosage) {
        if (dosage == Math.rint(dosage)) {
            return String.valueOf((long) dosage);
        }

        return String.valueOf(dosage);
    }

    private void updateProgress() {
        int[] report = medicationTracker.getProgressReport();
        int takenDoses = report[0];
        int missedDoses = report[1];
        int upcomingDoses = report[2];
        int progressPercent = report[3];

        progressIndicator.setProgress(progressPercent / 100.0);

        takenDataLabel.setText(String.valueOf(takenDoses));
        missedDataLabel.setText(String.valueOf(missedDoses));
        upcomingDataLabel.setText(String.valueOf(upcomingDoses));
    }

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
