package edu.utsa.cs3443.mydosemate.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Provides consistent, defensive error reporting and scene navigation for
 * JavaFX controllers.
 */
public final class UiErrorHandler {

    /** Prevents instantiation of this utility class. */
    private UiErrorHandler() {
    }

    /**
     * Displays a user-friendly error and records the underlying failure for
     * developers. The dialog is scheduled on the JavaFX application thread
     * when the failure originates on another thread.
     *
     * @param title the error-dialog title
     * @param message the user-facing error message
     * @param cause the underlying failure, or {@code null} when unavailable
     */
    public static void showError(String title, String message, Throwable cause) {
        if (cause != null) {
            cause.printStackTrace();
        }

        final String safeTitle = isBlank(title) ? "Application Error" : title;
        final String safeMessage = isBlank(message)
                ? "The requested action could not be completed."
                : message;

        Runnable displayAlert = new Runnable() {
            @Override
            public void run() {
                try {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(safeTitle);
                    alert.setHeaderText(null);
                    alert.setContentText(safeMessage);
                    alert.showAndWait();
                } catch (RuntimeException dialogFailure) {
                    dialogFailure.printStackTrace();
                    System.err.println(safeTitle + ": " + safeMessage);
                }
            }
        };

        try {
            if (Platform.isFxApplicationThread()) {
                displayAlert.run();
            } else {
                Platform.runLater(displayAlert);
            }
        } catch (RuntimeException platformUnavailable) {
            platformUnavailable.printStackTrace();
            System.err.println(safeTitle + ": " + safeMessage);
        }
    }

    /**
     * Safely replaces the current scene with an FXML view. Any missing
     * resource, malformed view, invalid event source, or loading failure is
     * reported to the user instead of escaping from the event handler.
     *
     * @param event the action event used to locate the current stage
     * @param resourceOwner the class used to resolve the FXML resource
     * @param fxml the classpath location of the destination FXML file
     * @return {@code true} when navigation succeeds; otherwise {@code false}
     */
    public static boolean switchScene(
            ActionEvent event,
            Class<?> resourceOwner,
            String fxml
    ) {
        try {
            if (event == null || !(event.getSource() instanceof Node)) {
                throw new IllegalStateException("Navigation event has no UI source");
            }

            if (resourceOwner == null || isBlank(fxml)) {
                throw new IllegalArgumentException("Navigation destination is missing");
            }

            URL resource = resourceOwner.getResource(fxml);
            if (resource == null) {
                throw new IOException("FXML resource was not found: " + fxml);
            }

            Node source = (Node) event.getSource();
            if (source.getScene() == null
                    || !(source.getScene().getWindow() instanceof Stage)) {
                throw new IllegalStateException("The current application window is unavailable");
            }

            Parent root = FXMLLoader.load(resource);
            Stage stage = (Stage) source.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            return true;
        } catch (IOException exception) {
            showError(
                    "Navigation Error",
                    "Unable to open the requested screen. Please try again.",
                    exception
            );
        } catch (RuntimeException exception) {
            showError(
                    "Navigation Error",
                    "Unable to open the requested screen. Please try again.",
                    exception
            );
        }

        return false;
    }

    /** Returns whether text is absent or contains only whitespace. */
    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
