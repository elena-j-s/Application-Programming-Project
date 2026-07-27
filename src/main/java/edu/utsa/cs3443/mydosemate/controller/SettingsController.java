package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.UserManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

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

        loadUserSettings();
    }

    private void loadUserSettings() {
        // load user information
    }

    @FXML
    public void saveSettings(ActionEvent event) {

    }


    @FXML
    public void logout(ActionEvent event) {
        // return user to welcome/sign-in screen
    }

    @FXML
    public void goBack(ActionEvent event) {
        //return to home screen
    }
}
