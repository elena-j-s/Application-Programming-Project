package edu.utsa.cs3443.mydosemate.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WelcomeController {

    //this is nothing it's just something to test the launch

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
