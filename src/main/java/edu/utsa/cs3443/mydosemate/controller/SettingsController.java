package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.UserManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controls the settings screen, including profile navigation and persistence
 * of the user's display-mode preference.
 */
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

    /** Loads the current user so settings changes can be persisted. */
    @FXML
    public void initialize() {
        userManager = new UserManager();

        try {
            userManager.loadUser();
        } catch (IOException e) {
            UiErrorHandler.showError(
                    "Settings Error",
                    "Your saved settings could not be loaded.",
                    e
            );
        } catch (RuntimeException e) {
            UiErrorHandler.showError(
                    "Settings Error",
                    "Your saved settings could not be loaded.",
                    e
            );
        }
    }

    /**
     * Opens the profile-editing screen.
     *
     * @param event the edit-profile action event
     */
    @FXML
    private void editProfile(ActionEvent event) {
        switchScene(event,
                "/edu/utsa/cs3443/mydosemate/view/edit-profile.fxml");
    }

    /**
     * Toggles and persists the user's dark-mode preference.
     *
     * @param event the toggle action event
     */
    @FXML
    private void toggleDarkMode(ActionEvent event) {
        try {
            userManager.toggleDarkMode();
        } catch (IOException e) {
            UiErrorHandler.showError(
                    "Settings Error",
                    "The display preference could not be saved.",
                    e
            );
        } catch (RuntimeException e) {
            UiErrorHandler.showError(
                    "Settings Error",
                    "The display preference could not be changed because no valid profile is loaded.",
                    e
            );
        }
    }

    /**
     * Replaces the current scene with the requested FXML view.
     *
     * @param event the action event used to locate the current stage
     * @param fxml the classpath location of the destination FXML file
     */
    private void switchScene(ActionEvent event, String fxml) {
        UiErrorHandler.switchScene(event, getClass(), fxml);
    }

    /**
     * Opens the home dashboard.
     *
     * @param event the navigation action event
     */
    @FXML
    private void goToDash(ActionEvent event) {
        switchScene(event, "/edu/utsa/cs3443/mydosemate/view/home-dashboard.fxml");
    }

    /**
     * Opens the medication list.
     *
     * @param event the navigation action event
     */
    @FXML
    private void goToMedicine(ActionEvent event) {
        switchScene(event, "/edu/utsa/cs3443/mydosemate/view/my-medications.fxml");
    }

    /**
     * Opens the dose-history screen.
     *
     * @param event the navigation action event
     */
    @FXML
    private void goToHistory(ActionEvent event) {
        switchScene(event, "/edu/utsa/cs3443/mydosemate/view/my-history.fxml");
    }

}
