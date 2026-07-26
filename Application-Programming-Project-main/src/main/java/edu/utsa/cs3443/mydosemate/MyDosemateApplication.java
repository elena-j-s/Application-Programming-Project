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

    private final MedicationTracker medicationTracker = new MedicationTracker();

    @Override
    public void start(Stage stage) throws IOException {
        try {
            medicationTracker.loadMedications();
        } catch (FileNotFoundException exception) {
            // Creates an empty medications.csv with its header.
            medicationTracker.saveMedications();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(MyDosemateApplication.class.getResource("hello-view.fxml"));

        HomeScreenController controller = loader.getController();
        controller.setMedicationTracker(medicationTracker);

        Parent root = loader.load();
        HomeScreenController controller = loader.getController();

        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene); //stage.setScene(new Scene(root, 320, 240));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
