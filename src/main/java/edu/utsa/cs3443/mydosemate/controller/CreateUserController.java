package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.UserManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateUserController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private Button createAccountButton;

    private final UserManager userManager = new UserManager();

    @FXML
    private void createAccountClicked(ActionEvent event) throws IOException {
        String firstName = firstNameField == null ? "" : firstNameField.getText().trim();
        String lastName = lastNameField == null ? "" : lastNameField.getText().trim();
        String email = emailField == null ? "" : emailField.getText().trim();
        String phone = phoneField == null ? "" : phoneField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Required Fields");
            alert.setHeaderText(null);
            alert.setContentText("First Name and Last Name are required.");
            alert.showAndWait();
            return;
        }

        userManager.createUserFile(firstName, lastName, email, phone);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/utsa/cs3443/mydosemate/view/home-dashboard.fxml"));

        Parent root = loader.load();
        Scene scene = new Scene(root);

        Stage stage =
                (Stage) ((Node) event.getSource())
                        .getScene()
                        .getWindow();

        stage.setScene(scene);
        stage.show();
    }


}
