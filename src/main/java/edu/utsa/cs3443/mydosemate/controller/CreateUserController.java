package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.UserManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CreateUserController {

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

    }

    @FXML
    public void createUser(ActionEvent event) {

    }

    @FXML
    public void cancel(ActionEvent event) {

    }

    private void showMessage(String message) {

    }
}
