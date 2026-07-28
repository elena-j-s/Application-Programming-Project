package edu.utsa.cs3443.mydosemate;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MyDosemateApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MyDosemateApplication.class.getResource("/edu/utsa/cs3443/mydosemate/view/welcome.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 550, 840);
        stage.setTitle("MyDoseMate");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
