package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.UserManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    private UserManager userManager;

    @FXML
    public void initialize() {
        userManager = new UserManager();

        try {
            userManager.loadUser();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editProfile(ActionEvent event) throws IOException {
        switchScene(event,
                "/edu/utsa/cs3443/mydosemate/view/edit-profile.fxml");
    }

    @FXML
    private void toggleDarkMode(ActionEvent event) {
        try {
            userManager.toggleDarkMode();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

}
