package edu.utsa.cs3443.mydosemate.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;


/** Controls the welcome screen shown before a user profile is created. */
public class WelcomeController {

    @FXML
    private Label MyDoseMate;

    @FXML
    private ImageView MyDoseMateLogo;

    @FXML
    private Button getStartedButton;

    /**
     * Opens the account-creation screen.
     *
     * @param event the get-started button action event
     */
    @FXML
    private void getStartedClicked(ActionEvent event) {
        UiErrorHandler.switchScene(
                event,
                getClass(),
                "/edu/utsa/cs3443/mydosemate/view/create-user.fxml"
        );
    }
}
