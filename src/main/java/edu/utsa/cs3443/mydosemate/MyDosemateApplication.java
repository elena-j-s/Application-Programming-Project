package edu.utsa.cs3443.mydosemate;

import edu.utsa.cs3443.mydosemate.model.UserManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MyDosemateApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        UserManager userManager = new UserManager();
        String startingScreen = "/edu/utsa/cs3443/mydosemate/view/welcome.fxml";

        if (userManager.userFileExists()) {
            try {
                userManager.loadUser();
                startingScreen = "/edu/utsa/cs3443/mydosemate/view/home-dashboard.fxml";
            } catch (IOException exception) {
                // user.csv exists but couldn't be read/parsed - fall back to signup
                // rather than crash on startup.
                exception.printStackTrace();
                startingScreen = "/edu/utsa/cs3443/mydosemate/view/welcome.fxml";
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(MyDosemateApplication.class.getResource(startingScreen));
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