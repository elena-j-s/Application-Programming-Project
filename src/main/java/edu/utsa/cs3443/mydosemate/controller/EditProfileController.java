package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.UserInputValidator;
import edu.utsa.cs3443.mydosemate.model.UserManager;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Controls profile editing by loading the persisted user, validating changes,
 * saving the updated profile, and returning to settings.
 */
public class EditProfileController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private Label firstNameErrorLabel;

    @FXML
    private Label lastNameErrorLabel;

    @FXML
    private Label emailErrorLabel;

    @FXML
    private Label phoneErrorLabel;

    private UserManager userManager;


    /** Loads the saved user profile and populates the editable fields. */
    @FXML
    public void initialize() {
        userManager = new UserManager();

        try {
            userManager.loadUser();

            if (userManager.getUser() != null) {
                firstNameField.setText(userManager.getUser().getFirstName());
                lastNameField.setText(userManager.getUser().getLastName());
                emailField.setText(userManager.getUser().getEmail());
                phoneField.setText(userManager.getUser().getPhoneNumber());
            }

        } catch (IOException e) {
            showAlert("Unable to Load Profile", e.getMessage());
        } catch (RuntimeException e) {
            UiErrorHandler.showError(
                    "Unable to Load Profile",
                    "The saved profile could not be displayed.",
                    e
            );
        }
    }


    /**
     * Validates and persists the profile fields before returning to settings.
     *
     * @param event the save-button action event
     */
    @FXML
    private void saveProfile(ActionEvent event) {
        String firstName = textOrEmpty(firstNameField);
        String lastName = textOrEmpty(lastNameField);
        String email = textOrEmpty(emailField);
        String phone = textOrEmpty(phoneField);

        try {
            String[] errors = UserInputValidator.validateUserCreation(
                    firstName,
                    lastName,
                    email,
                    phone
            );

            displayValidationErrors(errors);

            if (containsErrors(errors)) {
                return;
            }

            if (userManager.getUser() == null) {
                showAlert("Missing Profile", "No user profile is currently loaded.");
                return;
            }

            userManager.updateUserProfile(firstName, lastName, email, phone);

            goBack(event);

        } catch (IllegalArgumentException e) {
            showAlert("Invalid Input", e.getMessage());
        } catch (IOException e) {
            showAlert("Unable to Save Profile", e.getMessage());
        } catch (RuntimeException e) {
            UiErrorHandler.showError(
                    "Unable to Save Profile",
                    "The profile could not be saved. Please try again.",
                    e
            );
        }
    }

    /**
     * Displays each validation result beside its corresponding input field.
     *
     * @param errors errors ordered as first name, last name, email, and phone
     */
    private void displayValidationErrors(String[] errors) {
        if (firstNameErrorLabel != null) {
            firstNameErrorLabel.setText(errorText(errors[0]));
        }
        if (lastNameErrorLabel != null) {
            lastNameErrorLabel.setText(errorText(errors[1]));
        }
        if (emailErrorLabel != null) {
            emailErrorLabel.setText(errorText(errors[2]));
        }
        if (phoneErrorLabel != null) {
            phoneErrorLabel.setText(errorText(errors[3]));
        }
    }

    /**
     * Checks whether validation produced at least one error.
     *
     * @param errors the validation error array
     * @return {@code true} when any entry contains an error
     */
    private boolean containsErrors(String[] errors) {
        for (String error : errors) {
            if (error != null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Converts a nullable validation error into displayable label text.
     *
     * @param error the validation error
     * @return the error text, or an empty string for {@code null}
     */
    private String errorText(String error) {
        return error == null ? "" : error;
    }

    /**
     * Reads and trims a text field safely.
     *
     * @param field the field to read
     * @return the trimmed value, or an empty string when unavailable
     */
    private String textOrEmpty(TextField field) {
        return field == null || field.getText() == null ? "" : field.getText().trim();
    }

    /**
     * Displays an error dialog.
     *
     * @param title the dialog title
     * @param message the error message to display
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    /**
     * Returns to the settings screen.
     *
     * @param event the navigation action event
     */
    @FXML
    private void goBack(ActionEvent event) {
        UiErrorHandler.switchScene(
                event,
                getClass(),
                "/edu/utsa/cs3443/mydosemate/view/settings.fxml"
        );
    }
}
