package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.UserInputValidator;
import edu.utsa.cs3443.mydosemate.model.UserManager;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

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
        }
    }


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
        }
    }

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

    private boolean containsErrors(String[] errors) {
        for (String error : errors) {
            if (error != null) {
                return true;
            }
        }

        return false;
    }

    private String errorText(String error) {
        return error == null ? "" : error;
    }

    private String textOrEmpty(TextField field) {
        return field == null || field.getText() == null ? "" : field.getText().trim();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void goBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(
                getClass().getResource(
                        "/edu/utsa/cs3443/mydosemate/view/settings.fxml"));

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        stage.setScene(new Scene(root));
        stage.show();
    }
}
