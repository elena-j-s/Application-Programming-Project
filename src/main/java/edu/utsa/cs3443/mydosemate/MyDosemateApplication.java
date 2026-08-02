package edu.utsa.cs3443.mydosemate;

import edu.utsa.cs3443.mydosemate.controller.UiErrorHandler;
import edu.utsa.cs3443.mydosemate.model.UserManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;

/**
 * Launches MyDoseMate and selects the initial screen based on whether a valid
 * user profile has already been saved.
 */
public class MyDosemateApplication extends Application {
    /**
     * Creates the primary JavaFX stage and displays either the welcome screen
     * or the home dashboard.
     *
     * @param stage the primary application stage supplied by JavaFX
     */
    @Override
    public void start(Stage stage) {
        try {
            UserManager userManager = new UserManager();
            String startingScreen = "/edu/utsa/cs3443/mydosemate/view/welcome.fxml";

            if (userManager.userFileExists()) {
                try {
                    userManager.loadUser();
                    startingScreen = "/edu/utsa/cs3443/mydosemate/view/home-dashboard.fxml";
                } catch (IOException exception) {
                    UiErrorHandler.showError(
                            "Profile Error",
                            "The saved profile could not be read. "
                                    + "The welcome screen will be opened instead.",
                            exception
                    );
                } catch (RuntimeException exception) {
                    UiErrorHandler.showError(
                            "Profile Error",
                            "The saved profile could not be read. "
                                    + "The welcome screen will be opened instead.",
                            exception
                    );
                }
            }

            FXMLLoader fxmlLoader = new FXMLLoader(
                    MyDosemateApplication.class.getResource(startingScreen));
            Scene scene = new Scene(fxmlLoader.load(), 550, 840);
            stage.setTitle("MyDoseMate");
            loadApplicationIcon(stage);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException exception) {
            showStartupFailure(stage, exception);
        } catch (RuntimeException exception) {
            showStartupFailure(stage, exception);
        }
    }

    /**
     * Loads the optional application icon without preventing startup when the
     * image is missing or unreadable.
     *
     * @param stage the primary application stage
     */
    private void loadApplicationIcon(Stage stage) {
        try (InputStream iconStream = getClass().getResourceAsStream(
                "/edu/utsa/cs3443/mydosemate/images/MyDoseMateLogo.png")) {
            if (iconStream == null) {
                System.err.println("MyDoseMate application icon was not found.");
                return;
            }

            stage.getIcons().add(new Image(iconStream));
        } catch (IOException exception) {
            System.err.println("MyDoseMate application icon could not be closed.");
            exception.printStackTrace();
        } catch (RuntimeException exception) {
            System.err.println("MyDoseMate application icon could not be loaded.");
            exception.printStackTrace();
        }
    }

    /**
     * Presents a minimal fallback screen when normal application startup fails.
     *
     * @param stage the primary application stage
     * @param cause the startup failure
     */
    private void showStartupFailure(Stage stage, Throwable cause) {
        UiErrorHandler.showError(
                "Startup Error",
                "MyDoseMate could not load its data or interface. "
                        + "Check the project files and try again.",
                cause
        );

        try {
            Label fallbackMessage = new Label(
                    "MyDoseMate could not start.\n"
                            + "Please verify the data and application files, then restart."
            );
            fallbackMessage.setWrapText(true);
            fallbackMessage.setStyle("-fx-padding: 24; -fx-font-size: 16px;");
            stage.setTitle("MyDoseMate - Startup Error");
            stage.setScene(new Scene(fallbackMessage, 550, 840));
            stage.setResizable(false);
            stage.show();
        } catch (RuntimeException fallbackFailure) {
            UiErrorHandler.showError(
                    "Startup Error",
                    "MyDoseMate was unable to display its recovery screen.",
                    fallbackFailure
            );
        }
    }

    /**
     * Reports otherwise uncaught application exceptions instead of allowing
     * the JavaFX event thread to fail silently.
     *
     * @param thread the thread where the failure occurred
     * @param exception the uncaught failure
     */
    private static void handleUncaughtException(Thread thread, Throwable exception) {
        UiErrorHandler.showError(
                "Unexpected Error",
                "Something unexpected happened. Your saved data was not intentionally changed.",
                exception
        );
    }

    /**
     * Starts the JavaFX application.
     *
     * @param args command-line arguments passed to the JavaFX launcher
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable exception) {
                        handleUncaughtException(thread, exception);
                    }
                }
        );
        launch(args);
    }
}
