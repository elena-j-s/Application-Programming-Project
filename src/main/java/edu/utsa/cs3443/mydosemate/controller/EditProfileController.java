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

public class EditProfileController {

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

            firstNameField.setText(userManager.getUser().getFirstName());
            lastNameField.setText(userManager.getUser().getLastName());
            emailField.setText(userManager.getUser().getEmail());
            phoneField.setText(userManager.getUser().getPhoneNumber());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void saveProfile(ActionEvent event) {

        try {
            userManager.setFirstName(firstNameField.getText());
            userManager.setLastName(lastNameField.getText());
            userManager.setEmail(emailField.getText());
            userManager.setPhoneNumber(phoneField.getText());

            goBack(event);

        } catch (IOException e) {
            e.printStackTrace();
        }
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
