package edu.utsa.cs3443.mydosemate.controller;

import edu.utsa.cs3443.mydosemate.model.UserManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateUserController {

    // create an error message for empty fields !!
    // connect enter user data !!

    @FXML
    private Label MyDoseMate;

    @FXML
    private TextField createNameField;

    @FXML
    private TextField createEmailField;

    @FXML
    private TextField createPasswordField;

    @FXML
    private TextField confirmPasswordField;

    @FXML
    private Button createAccountButton;

    @FXML
    private void createAccountClicked(ActionEvent event) throws IOException {
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
