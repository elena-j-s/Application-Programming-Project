package edu.utsa.cs3443.mydosemate;

import edu.utsa.cs3443.mydosemate.controller.HomeScreenController;
import edu.utsa.cs3443.mydosemate.model.MedicationTracker;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MyDosemateApplication extends Application {

    private final MedicationTracker medicationTracker =
            new MedicationTracker();

    @Override
    public void start(Stage stage) throws IOException {
        try {
            medicationTracker.loadMedications();

        } catch (FileNotFoundException exception) {
            // Creates an empty medications.csv with its header.
            medicationTracker.saveMedications();
        }

        FXMLLoader loader = new FXMLLoader(
                MyDosemateApplication.class.getResource(
                        "/edu/utsa/cs3443/mydosemate/view/"
                                + "home-dashboard.fxml"
                )
        );

        Parent root = loader.load();

        HomeScreenController controller =
                loader.getController();

        controller.setMedicationTracker(
                medicationTracker
        );

        Scene scene = new Scene(root, 600, 400);

        stage.setTitle("My DoseMate");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
