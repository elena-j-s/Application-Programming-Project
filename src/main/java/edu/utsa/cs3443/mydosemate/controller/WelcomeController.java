package edu.utsa.cs3443.mydosemate.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;


public class WelcomeController {

    @FXML
    private Label MyDoseMate;

    @FXML
    private ImageView MyDoseMateLogo;

    @FXML
    private Button getStartedButton;

    @FXML
    private void getStartedClicked(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/utsa/cs3443/mydosemate/view/create-user.fxml"));

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
